package com.upshft.upshiftapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by Andrew on 3/13/2016.
 */
public class WidgetIcons
{
    // a dummy variable to demonstrate removal of an icon when set to true
    public static boolean testRemoveOnRender = false;
    private Context context;
    private ArrayList<WidgetIcon> icons;
    private View layout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private boolean deadheadTrackerRunning;
    private BroadcastReceiver broadcastReceiver;
    private ImageButton deadheadButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef;
    private Handler mHandler = new Handler();
    ArrayList<String> toolsList = new ArrayList<>();

    public WidgetIcons(final Context context) {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processIntent(intent);
            }
        };
        context.registerReceiver((broadcastReceiver), new IntentFilter(StickyWidgetWindow.ACTION_BROADCAST));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(broadcastReceiver, filter);

        this.context = context;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/users/");
        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            Log.e("WidgetIcons", "========mUserId===============" + mUserId);
            myFirebaseRef.child(mUserId).child("tools_arrayList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        ArrayList<String> tools_arrayList = (ArrayList<String>) dataSnapshot.getValue();
                        toolsList = tools_arrayList;
                        Log.e("WidgetIcons", "========tools_arrayList===============" + tools_arrayList.size());
                        createIcons(toolsList);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }

        createIcons(toolsList);
        updateLayout();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // update the layout uzing the WidgetIcons' rendering functionality
                StickyWidgetWindow.icons.updateLayout();

                // calculates new dimensions and resizes the bounding box
                StickyWidgetWindow.windowService.resizeWidgetBounds();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private void createIcons(ArrayList<String> tools_arrayList)
    {
        if (icons == null) {
            icons = new ArrayList<WidgetIcon>();
        } else {
            while (icons.size() != 0) {
                icons.remove(0);
            }
        }

        View upshiftIconView = createImageButton(context, R.drawable.menu_icon, null);
        upshiftIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WidgetIcons.this.context, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WidgetIcons.this.context.startActivity(intent);
            }
        });
        upshiftIconView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_STICKY_WIDGET_HIDE);
                context.sendBroadcast(intent);
                return false;
            }
        });
        icons.add(new WidgetIcon(context, upshiftIconView, null));

        View uberDriverIconView = createImageButton(context, R.drawable.ub_partner, "com.ubercab.driver");
        uberDriverIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Uber Partner", "com.ubercab.driver");
            }
        });
        icons.add(new WidgetIcon(context, uberDriverIconView, "pref_uberpartner_checkbox"));

        View uberRiderIconView = createImageButton(context, R.drawable.ub_rider, "com.ubercab");
        uberRiderIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Uber Rider", "com.ubercab");
            }
        });
        icons.add(new WidgetIcon(context, uberRiderIconView, "pref_uberrider_checkbox"));

        View lyftIconView = createImageButton(context, R.drawable.lyft, "me.lyft.android");
        lyftIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Lyft", "me.lyft.android");
            }
        });
        icons.add(new WidgetIcon(context, lyftIconView, "pref_lyft_checkbox"));

        View doordashIconView = createImageButton(context, R.drawable.doordash, "com.doordash.driverapp");
        doordashIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Dasher", "com.doordash.driverapp");
            }
        });
        icons.add(new WidgetIcon(context, doordashIconView, "pref_doordash_checkbox"));

        View postmatesIconView = createImageButton(context, R.drawable.postmates, "com.postmates.android.courier");
        postmatesIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Postmates", "com.postmates.android.courier");
            }
        });
        icons.add(new WidgetIcon(context, postmatesIconView, "pref_postmates_checkbox"));

        View curbIconView = createImageButton(context, R.drawable.ic_roadies, "com.roadie.android.app");
        curbIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Roadie", "com.roadie.android.app");
            }
        });
        icons.add(new WidgetIcon(context, curbIconView, "pref_curb_checkbox"));

        View wingzIconView = createImageButton(context, R.drawable.ic_gett_driver, "com.gettaxi.dbx.android");
        wingzIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Gett Drivers", "com.gettaxi.dbx.android");
            }
        });
        icons.add(new WidgetIcon(context, wingzIconView, "pref_wingz_checkbox"));

        View squareIconView = createImageButton(context, R.drawable.square, "com.squareup");
        squareIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Square", "com.squareup");
            }
        });
        icons.add(new WidgetIcon(context, squareIconView, "pref_square_checkbox"));

        View creditCardReaderIconView = createImageButton(context, R.drawable.ccreader, "com.ics.creditcardreader");
        creditCardReaderIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Credit Card Reader", "com.ics.creditcardreader");
            }
        });
        icons.add(new WidgetIcon(context, creditCardReaderIconView, "pref_creditcardreader_checkbox"));

        View pref_grab_checkbox = createImageButton(context, R.drawable.ic_grav, "com.grabtaxi.driver2");
        pref_grab_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Grab", "com.grabtaxi.driver2");
            }
        });
        icons.add(new WidgetIcon(context, pref_grab_checkbox, "pref_grab_checkbox"));

        View pref_amazon_checkbox = createImageButton(context, R.drawable.ic_ride_pax, "com.floatingtimerliteapp");
        pref_amazon_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Rideshare", "com.floatingtimerliteapp");
            }
        });
        icons.add(new WidgetIcon(context, pref_amazon_checkbox, "pref_amazon_checkbox"));

        View pref_uzurv_checkbox = createImageButton(context, R.drawable.ic_uzurv_icon, "com.uzurv.android.driver");
        pref_uzurv_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Uzurv", "com.uzurv.android.driver");
            }
        });
        icons.add(new WidgetIcon(context, pref_uzurv_checkbox, "pref_uzurv_checkbox"));

        View pref_fare_checkbox = createImageButton(context, R.drawable.ic_fare_icon, "com.fare.drivefare");
        pref_fare_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLaunchApp("Fare", "com.fare.drivefare");
            }
        });
        icons.add(new WidgetIcon(context, pref_fare_checkbox, "pref_fare_checkbox"));

//        View wazeIconView = createImageButton(context, R.drawable.waze, "com.waze");
//    //    wazeIconView.setVisibility(View.GONE);
//        wazeIconView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Waze", "com.waze");
//            }
//        });
//        icons.add(new WidgetIcon(context, wazeIconView, "pref_waze_checkbox"));

//        View gmapsIconView = createImageButton(context, R.drawable.gmaps, "com.google.android.apps.maps");
////        gmapsIconView.setVisibility(View.GONE);
//        gmapsIconView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Google Maps", "com.google.android.apps.maps");
//            }
//        });
//        icons.add(new WidgetIcon(context, gmapsIconView, "pref_maps_checkbox"));

//        View spotifyIconView = createImageButton(context, R.drawable.spotify, "com.spotify.music");
//       // spotifyIconView.setVisibility(View.GONE);
//        spotifyIconView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Spotify", "com.spotify.music");
//            }
//        });
//        icons.add(new WidgetIcon(context, spotifyIconView, "pref_spotify_checkbox"));

//        View pandoraIconView = createImageButton(context, R.drawable.pandora, "com.pandora.android");
//     //   pandoraIconView.setVisibility(View.GONE);
//        pandoraIconView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Pandora", "com.pandora.android");
//            }
//        });
//        icons.add(new WidgetIcon(context, pandoraIconView, "pref_pandora_checkbox"));

//        View pref_rideshare_pax_checkbox = createImageButton(context, R.drawable.ic_rideshare, "com.hayava.android.paxnotify");
//     //   pref_rideshare_pax_checkbox.setVisibility(View.GONE);
//        pref_rideshare_pax_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Rideshare PAX Notify", "com.hayava.android.paxnotify");
//            }
//        });
//        icons.add(new WidgetIcon(context, pref_rideshare_pax_checkbox, "pref_rideshare_pax_checkbox"));

//        View pref_surgechaser_checkbox = createImageButton(context, R.drawable.ic_surge_chaser, "com.surgechaser");
//    //    pref_surgechaser_checkbox.setVisibility(View.GONE);
//        pref_surgechaser_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Surge Chaser", "com.surgechaser");
//            }
//        });
//        icons.add(new WidgetIcon(context, pref_surgechaser_checkbox, "pref_surgechaser_checkbox"));

//        View pref_surgechas_checkbox = createImageButton(context, R.drawable.square_cash2, "com.squareup.cash");
//    //    pref_surgechas_checkbox.setVisibility(View.GONE);
//        pref_surgechas_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Square Cash", "com.squareup.cash");
//            }
//        });
//        icons.add(new WidgetIcon(context, pref_surgechas_checkbox, "pref_surgechas_checkbox"));


//        View pref_mystro_checkbox = createImageButton(context, R.drawable.mystro2, "partners.driver.mystro");
//    //    pref_mystro_checkbox.setVisibility(View.GONE);
//        pref_mystro_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryLaunchApp("Mystro", "partners.driver.mystro");
//            }
//        });
//        icons.add(new WidgetIcon(context, pref_mystro_checkbox, "pref_mystro_checkbox"));

        if (tools_arrayList.size() != 0) {
            for (int k = 0; k < tools_arrayList.size(); k++) {
                String select_tool = tools_arrayList.get(k);

                if (select_tool.equals("Waze")) {
                    View wazeIconView = createImageButton(context, R.drawable.waze, "com.waze");
                    wazeIconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Waze", "com.waze");
                        }
                    });
                    icons.add(new WidgetIcon(context, wazeIconView, "pref_waze_checkbox"));
                } else if (select_tool.equals("Google Maps")) {
                    View gmapsIconView = createImageButton(context, R.drawable.gmaps, "com.google.android.apps.maps");
                    gmapsIconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Google Maps", "com.google.android.apps.maps");
                        }
                    });
                    icons.add(new WidgetIcon(context, gmapsIconView, "pref_maps_checkbox"));
                } else if (select_tool.equals("Spotify")) {
                    View spotifyIconView = createImageButton(context, R.drawable.spotify, "com.spotify.music");
                    spotifyIconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Spotify", "com.spotify.music");
                        }
                    });
                    icons.add(new WidgetIcon(context, spotifyIconView, "pref_spotify_checkbox"));
                } else if (select_tool.equals("Pandora")) {
                    View pandoraIconView = createImageButton(context, R.drawable.pandora, "com.pandora.android");
                    pandoraIconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Pandora", "com.pandora.android");
                        }
                    });
                    icons.add(new WidgetIcon(context, pandoraIconView, "pref_pandora_checkbox"));
                } else if (select_tool.equals("Surge Chaser")) {
                    View pref_surgechaser_checkbox = createImageButton(context, R.drawable.ic_surge_chaser, "com.surgechaser");
                    pref_surgechaser_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Surge Chaser", "com.surgechaser");
                        }
                    });
                    icons.add(new WidgetIcon(context, pref_surgechaser_checkbox, "pref_surgechaser_checkbox"));
                } else if (select_tool.equals("Rideshare PAX")) {
                    View pref_rideshare_pax_checkbox = createImageButton(context, R.drawable.ic_rideshare, "com.hayava.android.paxnotify");
                    pref_rideshare_pax_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Rideshare PAX Notify", "com.hayava.android.paxnotify");
                        }
                    });
                    icons.add(new WidgetIcon(context, pref_rideshare_pax_checkbox, "pref_rideshare_pax_checkbox"));
                } else if (select_tool.equals("Square Cash")) {
                    View pref_surgechas_checkbox = createImageButton(context, R.drawable.square_cash2, "com.squareup.cash");
                    pref_surgechas_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Square Cash", "com.squareup.cash");
                        }
                    });
                    icons.add(new WidgetIcon(context, pref_surgechas_checkbox, "pref_surgechas_checkbox"));
                } else if (select_tool.equals("Mystro")) {
                    View pref_mystro_checkbox = createImageButton(context, R.drawable.mystro2, "partners.driver.mystro");
                    pref_mystro_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryLaunchApp("Mystro", "partners.driver.mystro");
                        }
                    });
                    icons.add(new WidgetIcon(context, pref_mystro_checkbox, "pref_mystro_checkbox"));
                }
            }
        }

        deadheadButton = new ImageButton(context);
        LinearLayout.LayoutParams deadheadButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        deadheadButtonLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; //android:layout_gravity="top|center_horizontal"
        deadheadButtonLayoutParams.setMargins(0, 0, 0, 0);
        deadheadButton.setLayoutParams(deadheadButtonLayoutParams);
        deadheadButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.deadhead_off));
        deadheadButton.setPadding(0, 0, 0, 0);
        deadheadButton.setBackgroundColor(Color.parseColor("#00ffffff")); //android:background="#00ffffff"
        deadheadButton.setScaleType(ImageView.ScaleType.FIT_START); // android:scaleType="fitStart"
        deadheadTrackerRunning = false;
        deadheadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deadheadTrackerRunning) {
                    Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                    intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_STOP);
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                    intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_START);
                    context.sendBroadcast(intent);
                }
            }
        });
        icons.add(new WidgetIcon(context, deadheadButton, null));
        View moveIconView = createImageView(context, R.drawable.ic_arrow);
        icons.add(new WidgetIcon(context, moveIconView, null));
    }

    public int count() {
        return icons.size();
    }

    public View getView(int i) {
        return icons.get(i).getView();
    }

    public View getLayout() {
        return layout;
    }

    private static View createImageButton(Context context, int drawableId, String packageName) {
        ImageButton imageButton = new ImageButton(context);

        LinearLayout.LayoutParams uberViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        uberViewLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; //android:layout_gravity="top|center_horizontal"
        uberViewLayoutParams.setMargins(0, 0, 0, 0);
        imageButton.setLayoutParams(uberViewLayoutParams);

        PackageManager manager = context.getPackageManager();
        Drawable drawable = null;
        if (packageName == null) {
            drawable = ContextCompat.getDrawable(context, drawableId);
        } else {
            try {
                Intent intentStartApp = manager.getLaunchIntentForPackage(packageName);
                if (intentStartApp != null) {
                    intentStartApp.addCategory(Intent.CATEGORY_LAUNCHER);
                    drawable = manager.getActivityIcon(intentStartApp);
                } else {
                    throw new PackageManager.NameNotFoundException();
                }
            } catch (PackageManager.NameNotFoundException e) {
                drawable = ContextCompat.getDrawable(context, drawableId);
            }
        }
        imageButton.setImageDrawable(drawable);
        imageButton.setPadding(0, 0, 0, 0);
        imageButton.setBackgroundColor(Color.parseColor("#00ffffff")); //android:background="#00ffffff"
        imageButton.setScaleType(ImageView.ScaleType.FIT_START); // android:scaleType="fitStart"
        return imageButton;
    }

    private static View createImageView(Context context, int drawableId) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams uberViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        uberViewLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; //android:layout_gravity="top|center_horizontal"
        uberViewLayoutParams.setMargins(0, 0, 0, 0);
        imageView.setLayoutParams(uberViewLayoutParams);

        imageView.setImageDrawable(ContextCompat.getDrawable(context, drawableId));
        imageView.setPadding(0, 0, 0, 0);
        imageView.setBackgroundColor(Color.parseColor("#00ffffff")); // android:background="#00ffffff"
        imageView.setScaleType(ImageView.ScaleType.FIT_START); // android:scaleType="fitStart"
        imageView.setAdjustViewBounds(false);
        return imageView;
    }

    private void tryLaunchApp(String appName, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent intentStartApp = manager.getLaunchIntentForPackage(packageName);
            if (intentStartApp != null) {
                intentStartApp.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(intentStartApp);
                Toast.makeText(context, "Switching to " + appName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, appName + " is not installed", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Please download it from the Play Store", Toast.LENGTH_SHORT).show();

                Intent intentLaunchPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                intentLaunchPlayStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                context.startActivity(intentLaunchPlayStore);
            }
        } catch (Exception e) {
        }
    }

    public void updateLayout() {
        if (layout == null) {
            // create layout (only once)
            layout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
        } else {
            // remove all subviews
            ((LinearLayout) layout).removeAllViews();
        }

        // generate and add subviews
        LinearLayout iconLayout = ((LinearLayout) layout);
        // iconLayout.setBackgroundColor(Color.parseColor("#006c6c6c"));
        iconLayout.setBackgroundResource(R.drawable.field_background);
        iconLayout.setOrientation(LinearLayout.VERTICAL);
        iconLayout.setPadding(5, 5, 5, 5);
        iconLayout.setWeightSum(1);
        iconLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        iconLayout.setBaselineAligned(false);

        // attach icons to widget
        for (int i = 0, j = 0; i < count(); ++i) {
            // dummy code to demonstrate removing the first icon while testRemoveOnRender is enabled
            if (testRemoveOnRender && i == 0)
                continue;

            if (icons.get(i).isVisible()) {
                iconLayout.addView(getView(i), j++);
            }
        }
    }

    public void setToggleDeadheadIconOn(boolean on) {
        if (on) {
            deadheadButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.deadhead_on));
            deadheadTrackerRunning = true;
        } else {
            deadheadButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.deadhead_off));
            deadheadTrackerRunning = false;
        }
    }

    public void processIntent(Intent intent) {
        if (intent == null)
            return;

        String intentAction = intent.getAction();

        if (intentAction != null && intentAction.equals(StickyWidgetWindow.ACTION_BROADCAST)) {
            String dashboardAction = intent.getStringExtra(DashboardActivity.ACTION);
            if (dashboardAction != null) {
                switch (dashboardAction) {
                    case DashboardActivity.ACTION_DEADHEAD_TRACKER_START:
                        setToggleDeadheadIconOn(true);
                        break;
                    case DashboardActivity.ACTION_DEADHEAD_TRACKER_STOP:
                        setToggleDeadheadIconOn(false);
                        break;
                    default:
                }
            }
        } else if (intentAction != null && (intentAction.equals(Intent.ACTION_PACKAGE_ADDED) || intentAction.equals(Intent.ACTION_PACKAGE_REMOVED))) {
            createIcons(toolsList);
            updateLayout();
        }
    }

}

class WidgetIcon {
    private Context context;
    private View view;
    private String visiblePreferenceName;

    public WidgetIcon(Context context, View view, String visiblePreferenceName) {
        this.context = context;
        this.view = view;
        this.visiblePreferenceName = visiblePreferenceName;
    }

    public Context getContext() {
        return context;
    }

    public View getView() {
        return view;
    }

    public boolean isVisible() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(visiblePreferenceName, true);
    }
}