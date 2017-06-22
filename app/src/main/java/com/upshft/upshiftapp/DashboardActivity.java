package com.upshft.upshiftapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.fragment.NotificationFragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The main {@link Activity} and entry-point for this app
 */
public class DashboardActivity extends Activity implements View.OnClickListener {

    AdView mAdView;
    LinearLayout bottomBar_layout;
    /**
     * This {@link Activity}'s {@link StickyWidgetFragment} for governing the states, behaviors and UI components for the sticky widget feature
     */
    private StickyWidgetFragment stickyWidgetFragment;

    /**
     * This {@link Activity}'s {@link SummaryFragment} for governing the states, behaviors and UI components for the summary panel
     */
    private SummaryFragment summaryFragment;

    /**
     * This {@link Activity}'s {@link ShiftTimerFragment} for governing the states, behaviors and UI components for the shift timer feature
     */
    private ShiftTimerFragment shiftTimerFragment;

    /**
     * This {@link Activity}'s {@link GooglePlayServicesFragment} for governing the states, behaviors and UI components for connecting to Google Play Services
     */
    private GooglePlayServicesFragment googlePlayServicesFragment;

    /**
     * This {@link Activity}'s {@link DeadheadTrackerFragment} for governing the states, behaviors and UI components for the deadhead miles tracker feature
     */
    private DeadheadTrackerFragment deadheadTrackerFragment;

    /**
     * This {@link Activity}'s {@link RevenueTrackerFragment} for governing the states, behaviors and UI components for the revenue tracker feature
     */
    private RevenueTrackerFragment revenueTrackerFragment;

    /**
     * This {@link Activity}'s {@link ExpenseTrackerFragment} for governing the states, behaviors and UI components for the expense tracker feature
     */
    private ExpenseTrackerFragment expenseTrackerFragment;

    /**
     * This {@link Activity}'s fragment ID tag for its {@link StickyWidgetFragment}
     */
    public static final String StickyWidgetFragmentTag = "WidgetFrag";

    /**
     * This {@link Activity}'s fragment ID tag for its {@link SummaryFragment}
     */
    public static final String SummaryFragmentTag = "SummaryFrag";

    /**
     * This {@link Activity}'s fragment ID tag for its {@link ShiftTimerFragment}
     */
    public static final String ShiftTimerFragmentTag = "ShiftFrag";

    /**
     * This {@link Activity}'s fragment ID tag for its {@link GooglePlayServicesFragment}
     */
    public static final String GooglePlayServicesFragmentTag = "GpsFrag";

    /**
     * This {@link Activity}'s fragment ID tag for its {@link DeadheadTrackerFragment}
     */
    public static final String DeadheadTrackerFragmentTag = "DeadheadFrag";

    /**
     * This {@link Activity}'s fragment ID tag for its {@link RevenueTrackerFragment}
     */
    public static final String RevenueTrackerFragmentTag = "RevenueFrag";
    /**
     * This {@link Activity}'s fragment ID tag for its {@link ExpenseTrackerFragment}
     */
    public static final String ExpenseTrackerFragmentTag = "ExpenseFrag";
    public static final String ACTION = "Action";
    public static final String ACTION_STICKY_WIDGET_SHOW = "ShowWidget";
    public static final String ACTION_STICKY_WIDGET_HIDE = "HideWidget";
    public static final String ACTION_STICKY_WIDGET_STOP_WINDOW_SERVICE = "CloseWidget";
    public static final String ACTION_SHIFT_TIMER_START = "StartShift";
    public static final String ACTION_SHIFT_TIMER_PAUSE = "PauseShift";
    public static final String ACTION_SHIFT_TIMER_STOP = "StopShift";
    public static final String ACTION_DEADHEAD_TRACKER_START = "StartDeadhead";
    public static final String ACTION_DEADHEAD_TRACKER_PAUSE = "PauseDeadhead";
    public static final String ACTION_DEADHEAD_TRACKER_STOP = "StopDeadhead";
    public static final String ACTION_EXPENSE_TRACKER_DIALOG_SHOW = "ExpenseDialogShow";
    public static final String ACTION_REVENUE_TRACKER_DIALOG_SHOW = "RevenueDialogShow";

    private BroadcastReceiver broadcastReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    Toolbar toolbar;
    ImageView settings_button;
    FrameLayout mainContainer;
    RelativeLayout notification_layout, category_layout, add_push_layout, search_layout, share_layout;
    RelativeLayout mainDashboard;
    ImageView notification_imageView, category_imageView, home_imageView, search_imageView, share_imageView;

    // Monthly Payment
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef;
    private DatabaseReference mDatabase;
    private Firebase mRef = new Firebase("https://upshift-db2cf.firebaseio.com/");
    String endDate = "";
    Dialog dialog;
    String paymentstatus = "";
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AQng_ZkWjh1srgfufsVhxCbusD6jitwx90cDx4IrsWVaBPaJq6dWHgEivht-rATF3tyWqmNVEPSiQY4b";
    //"Aa_lCgKJvVI5zfu661cdmG3h48Px3oVTYOtDBeBOE5qDdTgZUcu0VImr_Sx2IZ-wctkFXOv6raDJCprS";
    private static final int REQUEST_CODE_PAYMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // read and set default values from the preferences XML file
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // set content layout
        setContentView(R.layout.main_dashboard);
        // set the screen orientation to portrait at all times so that is never rotates to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/PaymentDetails");

        bottomBar_layout = (LinearLayout) findViewById(R.id.bottomBar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        settings_button = (ImageView) toolbar.findViewById(R.id.settingsImageView);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "UpShift Settings...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();
        mAdView.loadAd(request);

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            getDateDetails(mUserId);
        }

        // create fragments to add
        stickyWidgetFragment = new StickyWidgetFragment();
        summaryFragment = new SummaryFragment();
        shiftTimerFragment = new ShiftTimerFragment();
        googlePlayServicesFragment = new GooglePlayServicesFragment();
        deadheadTrackerFragment = new DeadheadTrackerFragment();
        revenueTrackerFragment = new RevenueTrackerFragment();
        expenseTrackerFragment = new ExpenseTrackerFragment();

        // attach fragments to this activity
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stickyWidgetFragment, StickyWidgetFragmentTag);
        fragmentTransaction.add(summaryFragment, SummaryFragmentTag);
        fragmentTransaction.add(shiftTimerFragment, ShiftTimerFragmentTag);
        fragmentTransaction.add(googlePlayServicesFragment, GooglePlayServicesFragmentTag);
        fragmentTransaction.add(deadheadTrackerFragment, DeadheadTrackerFragmentTag);
        fragmentTransaction.add(revenueTrackerFragment, RevenueTrackerFragmentTag);
        fragmentTransaction.add(expenseTrackerFragment, ExpenseTrackerFragmentTag);
        fragmentTransaction.commit();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processIntent(intent);
            }
        };

        registerReceiver((broadcastReceiver), new IntentFilter(StickyWidgetWindow.ACTION_BROADCAST));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
        mainDashboard = (RelativeLayout) findViewById(R.id.mainDashboard);

        notification_layout = (RelativeLayout) findViewById(R.id.notification_layout);
        category_layout = (RelativeLayout) findViewById(R.id.category_layout);
        add_push_layout = (RelativeLayout) findViewById(R.id.add_push_layout);
        search_layout = (RelativeLayout) findViewById(R.id.search_layout);
        share_layout = (RelativeLayout) findViewById(R.id.share_layout);

        notification_layout.setOnClickListener(this);
        category_layout.setOnClickListener(this);
        add_push_layout.setOnClickListener(this);
        search_layout.setOnClickListener(this);
        share_layout.setOnClickListener(this);

        notification_imageView = (ImageView) findViewById(R.id.notification_imageView);
        category_imageView = (ImageView) findViewById(R.id.category_imageView);
        home_imageView = (ImageView) findViewById(R.id.home_imageView);
        search_imageView = (ImageView) findViewById(R.id.search_imageView);
        share_imageView = (ImageView) findViewById(R.id.share_imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu from an XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            // action with ID settings_button was selected
            case R.id.settings_button:
                Toast.makeText(this, "UpShift Settings...", Toast.LENGTH_SHORT).show();
                intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
//            case R.id.deadhead_db_button:
//                Toast.makeText(this, "Deadhead Database...", Toast.LENGTH_SHORT).show();
//
//                intent = new Intent(DashboardActivity.this, DeadheadRecordListActivity.class);
//                startActivity(intent);
//
//                break;
//            case R.id.expenses_db_button:
//                Toast.makeText(this, "Expenses Database...", Toast.LENGTH_SHORT).show();
//
//                intent = new Intent(DashboardActivity.this, ExpenseRecordListActivity.class);
//                startActivity(intent);
//
//                break;
//            case R.id.rev_db_button:
//                Toast.makeText(this, "Revenue Database...", Toast.LENGTH_SHORT).show();
//
//                intent = new Intent(DashboardActivity.this, RevenueRecordListActivity.class);
//                startActivity(intent);
//
//                break;
//            case R.id.shift_db_button:
//                Toast.makeText(this, "Shift Database...", Toast.LENGTH_SHORT).show();
//
//                intent = new Intent(DashboardActivity.this, ShiftRecordListActivity.class);
//                startActivity(intent);
//
//                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        // sign out for corresponding login request in onCreate()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("pref_signed_in", false).commit();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        String contentTitle = "Dashboard";
        String deepLinkUri = "http://upshift-app.com";
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(Action.TYPE_VIEW, contentTitle, Uri.parse(deepLinkUri));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        String contentTitle = "Dashboard";
        String objectId = "http://host/path";
        //String deepLinkUri = "android-app://com.upshft.upshift/http/host/path";
        String deepLinkUri = "http://upshift-app.com";
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(Action.TYPE_VIEW, contentTitle, Uri.parse(deepLinkUri));
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean signed_in = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_sign_in", false);
        // sign-in is never being checked anywhere else, meaning if they revoke access on Google's site, they can remained "signed in" to our app
        if (!signed_in) {
//            Intent intent = new Intent(this, GoogleSignInActivity.class);
//            startActivity(intent);
//            finish();
            return; // to stop immediately and prevent any further processing
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);
    }

    public void processIntent(Intent intent)
    {
        String action = intent.getStringExtra(ACTION);
        if (action != null) {
            switch (action) {
                case ACTION_STICKY_WIDGET_SHOW:
                    stickyWidgetFragment.showStickyWidget();
                    break;
                case ACTION_STICKY_WIDGET_HIDE:
                    stickyWidgetFragment.hideStickyWidget();
                    break;
                case ACTION_STICKY_WIDGET_STOP_WINDOW_SERVICE:
                    stickyWidgetFragment.stopWindowService();
                    break;
                case ACTION_SHIFT_TIMER_START:
                    shiftTimerFragment.startShift();
                    break;
                case ACTION_SHIFT_TIMER_PAUSE:
                    shiftTimerFragment.pauseShift();
                    break;
                case ACTION_SHIFT_TIMER_STOP:
                    shiftTimerFragment.stopShift();
                    break;
                case ACTION_DEADHEAD_TRACKER_START:
                    deadheadTrackerFragment.onStartTrackDeadhead();
                    break;
                case ACTION_DEADHEAD_TRACKER_PAUSE:
                    break;
                case ACTION_DEADHEAD_TRACKER_STOP:
                    deadheadTrackerFragment.onStopTrackDeadhead();
                    break;
                case ACTION_EXPENSE_TRACKER_DIALOG_SHOW:
                    break;
                case ACTION_REVENUE_TRACKER_DIALOG_SHOW:
                    break;
                default:
            }
        }
    }


    @Override
    public void onClick(View v) {
        android.app.Fragment fragment = null;
        android.app.FragmentManager fragmentManager = null;
        FragmentTransaction fragmentTransaction = null;

        switch (v.getId()) {
            case R.id.notification_layout:
                mainDashboard.setVisibility(View.VISIBLE);
                mainContainer.setVisibility(View.GONE);
                notification_imageView.setImageResource(R.drawable.ic_white_meter);
                category_imageView.setImageResource(R.drawable.ic_briefcase);
                home_imageView.setImageResource(R.drawable.ic_home);
                search_imageView.setImageResource(R.drawable.ic_option);
                share_imageView.setImageResource(R.drawable.ic_menu);

                notification_layout.setBackgroundColor(Color.parseColor("#405266"));
                category_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                search_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                share_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                add_push_layout.setBackgroundColor(Color.parseColor("#ffffff"));

                break;

            case R.id.category_layout:
                mainDashboard.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
                notification_imageView.setImageResource(R.drawable.ic_meter);
                category_imageView.setImageResource(R.drawable.ic_white_briefcase);
                home_imageView.setImageResource(R.drawable.ic_home);
                search_imageView.setImageResource(R.drawable.ic_option);
                share_imageView.setImageResource(R.drawable.ic_menu);

                category_layout.setBackgroundColor(Color.parseColor("#405266"));
                search_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                share_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                notification_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                add_push_layout.setBackgroundColor(Color.parseColor("#ffffff"));

                fragment = new NotificationFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContainer, fragment);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.add_push_layout:

                mainDashboard.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);

                home_imageView.setImageResource(R.drawable.ic_white_home);
                notification_imageView.setImageResource(R.drawable.ic_meter);
                category_imageView.setImageResource(R.drawable.ic_briefcase);
                search_imageView.setImageResource(R.drawable.ic_option);
                share_imageView.setImageResource(R.drawable.ic_menu);

                add_push_layout.setBackgroundColor(Color.parseColor("#405266"));
                share_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                notification_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                category_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                search_layout.setBackgroundColor(Color.parseColor("#ffffff"));

                fragment = new NotificationFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContainer, fragment);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.search_layout:
                mainDashboard.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
                notification_imageView.setImageResource(R.drawable.ic_meter);
                home_imageView.setImageResource(R.drawable.ic_home);
                category_imageView.setImageResource(R.drawable.ic_briefcase);
                search_imageView.setImageResource(R.drawable.ic_white_option);
                share_imageView.setImageResource(R.drawable.ic_menu);

                search_layout.setBackgroundColor(Color.parseColor("#405266"));
                share_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                notification_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                category_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                add_push_layout.setBackgroundColor(Color.parseColor("#ffffff"));

                fragment = new NotificationFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContainer, fragment);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.share_layout:
                mainDashboard.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
                notification_imageView.setImageResource(R.drawable.ic_meter);
                category_imageView.setImageResource(R.drawable.ic_briefcase);
                home_imageView.setImageResource(R.drawable.ic_home);
                search_imageView.setImageResource(R.drawable.ic_option);
                share_imageView.setImageResource(R.drawable.ic_white_menu);

                share_layout.setBackgroundColor(Color.parseColor("#405266"));
                notification_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                category_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                search_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                add_push_layout.setBackgroundColor(Color.parseColor("#ffffff"));

                fragment = new NotificationFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainContainer, fragment);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment);
                fragmentTransaction.commit();
                break;
        }
    }


    public void getDateDetails(String mUserId)
    {
        myFirebaseRef.child(mUserId).child("paymentStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String paymentStatus = dataSnapshot.getValue(String.class);
                    mAdView.setVisibility(View.GONE);
                    bottomBar_layout.setVisibility(View.VISIBLE);
                    Log.e("paymentStatus", "=========paymentStatus===========" + paymentStatus);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        myFirebaseRef.child(mUserId).child("paymentEndDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String paymentEndDate = dataSnapshot.getValue(String.class);
                    Log.e("paymentStatus", "=========paymentEndDate===========" + paymentEndDate);

                    //  String date ="29/07/13";
                    SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date oneWayTripDate = input.parse(paymentEndDate);                 // parse input
                        // tripDate.setText(output.format(oneWayTripDate));    // format output
                      //  getDateAgo(output.format(oneWayTripDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (shiftTimerFragment.isShiftTimerRunning() || deadheadTrackerFragment.isDeadheadTrackerRunning()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Cannot close")
                    .setMessage("You cannot close this app with either the Shift Timer or Deadhead Tracker running.")
                    .setNeutralButton("OK", null)
                    .show();
        } else {
            super.onBackPressed();

        }
    }


}
