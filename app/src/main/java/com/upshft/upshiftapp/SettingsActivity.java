package com.upshft.upshiftapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upshft.upshiftapp.signup.AccountDetailsActivity;
import com.upshft.upshiftapp.signup.CouponListActivity;
import com.upshft.upshiftapp.signup.DocumentActivity;
import com.upshft.upshiftapp.signup.EarningDataActivity;
import com.upshft.upshiftapp.signup.ReferralActivity;
import com.upshft.upshiftapp.signup.SignInActivity;
import com.upshft.upshiftapp.signup.SignUpActivity;
import java.util.ArrayList;

import grid.IconPreferenceScreen;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements GoogleApiClient.OnConnectionFailedListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }
            if (preference instanceof RingtonePreference) {

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) || SettingsFragment.class.getName().equals(fragmentName);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment implements RewardedVideoAdListener {
        private FirebaseAuth mAuth;
        private SharedPreferences sharedPreferences;
        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirebaseUser;
        private String mUserId;
        Firebase myFirebaseRef;
        PreferenceScreen preferenceScreen;
        Context mcontext;
        InterstitialAd mInterstitialAd;

        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";
        private RewardedVideoAd mRewardedVideoAd;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            mAuth = FirebaseAuth.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(false);

            MobileAds.initialize(getActivity(), APP_ID);
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
            mRewardedVideoAd.setRewardedVideoAdListener(this);
            loadRewardedVideoAd();

            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1712485313");

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    signOut();
                }
            });
            requestNewInterstitial();

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/users/");


            if (mFirebaseUser == null) {
                // Not logged in, launch the Log In activity
            } else {
                mUserId = mFirebaseUser.getUid();

                myFirebaseRef.child(mUserId).child("platform_arrayList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> platform_arraylist = (ArrayList<String>) dataSnapshot.getValue();
                            update_selectedBox(platform_arraylist);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // Toast.makeText(context, "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                myFirebaseRef.child(mUserId).child("tools_arrayList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> tools_arrayList = (ArrayList<String>) dataSnapshot.getValue();
                            add_selectedCheckBox(tools_arrayList);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // Toast.makeText(context, "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            preferenceScreen = (PreferenceScreen) findPreference("main");
            Resources preferenceScreen_res = getResources();
            Drawable preferenceScreen_icon = preferenceScreen_res.getDrawable(R.drawable.stack);
            preferenceScreen.setIcon(preferenceScreen_icon);

            ListPreference pref_widget_startup_appearance = (ListPreference) findPreference("pref_widget_startup_appearance");
            Resources pref_widget_startup_appearance_res = getResources();
            Drawable pref_widget_startup_appearance_icon = pref_widget_startup_appearance_res.getDrawable(R.drawable.widget);
            pref_widget_startup_appearance.setIcon(pref_widget_startup_appearance_icon);

            CheckBoxPreference pref_autostart_checkbox = (CheckBoxPreference) findPreference("pref_autostart_checkbox");
            Resources pref_autostart_checkbox_res = getResources();
            Drawable pref_autostart_checkbox_icon = pref_autostart_checkbox_res.getDrawable(R.drawable.off);
            pref_autostart_checkbox.setIcon(pref_autostart_checkbox_icon);


            ListPreference pref_geo_interval = (ListPreference) findPreference("pref_geo_interval");
            Resources pref_geo_interval_res = getResources();
            Drawable pref_geo_interval_icon = pref_geo_interval_res.getDrawable(R.drawable.location);
            pref_geo_interval.setIcon(pref_geo_interval_icon);

            ListPreference pref_geo_accuracy_priority = (ListPreference) findPreference("pref_geo_accuracy_priority");
            Resources pref_geo_accuracy_priority_res = getResources();
            Drawable pref_geo_accuracy_priority_icon = pref_geo_accuracy_priority_res.getDrawable(R.drawable.mob);
            pref_geo_accuracy_priority.setIcon(pref_geo_accuracy_priority_icon);


            Preference account_details = (Preference) findPreference("account_details");
            Resources res = getResources();
            Drawable icon = res.getDrawable(R.drawable.user);
            account_details.setIcon(icon);
            account_details.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference close_upshift = (Preference) findPreference("close_upshift");
            Resources close_upshift_res = getResources();
            Drawable close_upshift_icon = close_upshift_res.getDrawable(R.drawable.ic_logout);
            close_upshift.setIcon(close_upshift_icon);
            close_upshift.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference)
                {
                    showRewardedVideo();
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                       // signOut();
//                    }
                    return true;
                }
            });

            IconPreferenceScreen update_data = (IconPreferenceScreen) findPreference("update_data");
            update_data.setSummary("Edit Your Personal Information");
            Resources res1 = getResources();
            Drawable icon1 = res1.getDrawable(R.drawable.user);
            update_data.setIcon(icon1);
            update_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), SignUpActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference choose_vehicle = (Preference) findPreference("choose_vehicle");
            Resources res2 = getResources();
            Drawable icon2 = res2.getDrawable(R.drawable.car);
            choose_vehicle.setIcon(icon2);
            choose_vehicle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), EarningDataActivity.class);
                    startActivity(intent);
                    return false;
                }
            });

            Preference document_data = (Preference) findPreference("document_data");
            Resources document_data_res = getResources();
            Drawable document_data_icon = document_data_res.getDrawable(R.drawable.car);
            document_data.setIcon(document_data_icon);
            document_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), DocumentActivity.class);
                    startActivity(intent);
                    return false;
                }
            });

            Preference sign_in_email = (Preference) findPreference("SIGN_IN_EMAIL");
            Resources sign_in_email_res = getResources();
            Drawable sign_in_email_icon = sign_in_email_res.getDrawable(R.drawable.msg_black);
            sign_in_email.setIcon(sign_in_email_icon);
            sign_in_email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Logout!")
                            .setMessage("Do you want to Logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // revoke access
                                    signOut();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });

            Preference myPref = (Preference) findPreference("pref_action_revoke_access");
            Resources myPref_res = getResources();
            Drawable myPref_icon = myPref_res.getDrawable(R.drawable.key_black);
            myPref.setIcon(myPref_icon);
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Warning!")
                            .setMessage("Revoking access to this app will delete ALL your user data and settings. Do you want to proceed?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // revoke access
                                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                                    intent.putExtra(SignInActivity.EXTRA_ACTION, SignInActivity.EXTRA_ACTION_REVOKE_ACCESS);
                                    startActivity(intent);
                                    getActivity().finish();
                                    return;
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });

            Preference referral = (Preference) findPreference("referral");
            Resources referral_res = getResources();
            Drawable referral_icon = referral_res.getDrawable(R.drawable.mem);
            referral.setIcon(referral_icon);
            referral.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), ReferralActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference coupons = (Preference) findPreference("coupons");
            Resources coupons_res = getResources();
            Drawable coupons_icon = coupons_res.getDrawable(R.drawable.ticket);
            coupons.setIcon(coupons_icon);
            coupons.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), CouponListActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
            bindPreferenceSummaryToValue(findPreference("SIGN_IN_EMAIL"));
        }

        private void requestNewInterstitial() {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("1BB84974748D2DD1B8CC768F4EE0EAB3")
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }

//        private void beginPlayingGame() {
//            // Play for a while, then display the New Game Button
//            getActivity().finish();
//            System.exit(0);
//        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void signOut() {
            // Firebase sign out
            if (AccessToken.getCurrentAccessToken() != null) {
                Log.e("Login_Status", ".........facebook logged in.........");
                LoginManager.getInstance().logOut();
                return; // already logged out
            } else {
                Log.e("Login_Status", ".........simple logged in.........");
            }

            sharedPreferences.edit().putBoolean("pref_signed_in", false).commit();
            sharedPreferences.edit().putString("SIGN_IN_ID", "").commit();
            sharedPreferences.edit().putString("SIGN_IN_EMAIL", "").commit();
            clearPreferences();
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            // intent.putExtra(SignInActivity.EXTRA_ACTION, SignInActivity.EXTRA_ACTION_REVOKE_ACCESS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }

        public void update_selectedBox(ArrayList<String> platform_arraylist) {


            for (int k = 0; k < platform_arraylist.size(); k++) {
                String select_tool = platform_arraylist.get(k);
                if (select_tool.equals("Uber")) {
                    CheckBoxPreference pref_uberpartner_checkbox = (CheckBoxPreference) findPreference("pref_uberpartner_checkbox");
                    pref_uberpartner_checkbox.setChecked(true);
                } else if (select_tool.equals("Lyft")) {
                    CheckBoxPreference pref_lyft_checkbox = (CheckBoxPreference) findPreference("pref_lyft_checkbox");
                    pref_lyft_checkbox.setChecked(true);
                } else if (select_tool.equals("Doordash")) {
                    CheckBoxPreference pref_doordash_checkbox = (CheckBoxPreference) findPreference("pref_doordash_checkbox");
                    pref_doordash_checkbox.setChecked(true);

                } else if (select_tool.equals("Postmates")) {
                    CheckBoxPreference pref_postmates_checkbox = (CheckBoxPreference) findPreference("pref_postmates_checkbox");
                    pref_postmates_checkbox.setChecked(true);

                } else if (select_tool.equals("Roadie")) {
                    CheckBoxPreference pref_curb_checkbox = (CheckBoxPreference) findPreference("pref_curb_checkbox");
                    pref_curb_checkbox.setChecked(true);

                } else if (select_tool.equals("Grab")) {
                    CheckBoxPreference pref_grab_checkbox = (CheckBoxPreference) findPreference("pref_grab_checkbox");
                    pref_grab_checkbox.setChecked(true);

                } else if (select_tool.equals("Rideshare")) {
                    CheckBoxPreference pref_amazon_checkbox = (CheckBoxPreference) findPreference("pref_amazon_checkbox");
                    pref_amazon_checkbox.setChecked(true);

                } else if (select_tool.equals("Uzurv")) {
                    CheckBoxPreference pref_uzurv_checkbox = (CheckBoxPreference) findPreference("pref_uzurv_checkbox");
                    pref_uzurv_checkbox.setChecked(true);

                } else if (select_tool.equals("Fare")) {
                    CheckBoxPreference pref_fare_checkbox = (CheckBoxPreference) findPreference("pref_fare_checkbox");
                    pref_fare_checkbox.setChecked(true);
                }
            }
        }

        public void add_selectedCheckBox(ArrayList<String> tools_arrayList)
        {
            if (tools_arrayList.size() != 0)
            {
                int count = preferenceScreen.getPreferenceCount();
                Log.e("getPreferenceCount", "..................." + count);
                for (int i = 0; i < count; i++) {
                    Log.e("getPreferenceCount", "........getPreference..........." + preferenceScreen.getPreference(i));
                    if (i == 1) {
                        preferenceScreen.removePreference((PreferenceCategory) findPreference("pref_tools"));
                    }
                }
                try {
                    PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
                    preferenceCategory.setKey("pref_tools");
                    preferenceCategory.setTitle("Rideshare Tools Apps");
                    preferenceScreen.addPreference(preferenceCategory);

                    for (int k = 0; k < tools_arrayList.size(); k++) {
                        String select_tool = tools_arrayList.get(k);

                        if (select_tool.equals("Square Reader")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_reader_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Waze")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_waze_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Use Waze to help get GPS and traffic information");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Google Maps")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_maps_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Use Google Maps for navigation assistance");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Spotify")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_spotify_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Listen to music with Spotify");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Pandora")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_pandora_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Listen to music with Pandora Radio");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Surge Chaser")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_surgechaser_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("SurgeChaser is a utility for Uber drivers that helps track and chase the surge");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Rideshare PAX")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_rideshare_pax_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("PAX Notify lets you select a message to send your rideshare PAX");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Square Cash")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_surgechas_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Pay your friends instantly, and cash out to your bank for FREE! Standard deposits show up the very next business day");
                            preferenceCategory.addPreference(checkBoxPreference);
                        } else if (select_tool.equals("Mystro")) {
                            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                            checkBoxPreference.setKey("pref_mystro_checkbox");
                            checkBoxPreference.setTitle(tools_arrayList.get(k));
                            checkBoxPreference.setChecked(true);
                            checkBoxPreference.setSummary("Mystro allows users to drive smarter and safer and is NOT a ride-sharing app so is only available for ride-sharing drivers");
                            preferenceCategory.addPreference(checkBoxPreference);
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }

        private void clearPreferences() {
            try {
                // clearing app data
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear com.upshft.upshiftapp");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadRewardedVideoAd()
        {
            if (!mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
            }
        }

        private void showRewardedVideo() {
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        //    Toast.makeText(getActivity(), "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdClosed()
        {
           // Toast.makeText(getActivity(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // Preload the next video ad.
           // loadRewardedVideoAd();
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int errorCode) {
            Toast.makeText(getActivity(), "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdLoaded() {
          //  Toast.makeText(getActivity(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdOpened() {
            Toast.makeText(getActivity(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewarded(RewardItem reward) {
//            Toast.makeText(getActivity(),
//                    String.format(" onRewarded! currency: %s amount: %d", reward.getType(), reward.getAmount()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoStarted() {
         //   Toast.makeText(getActivity(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPause() {
            super.onPause();
            mRewardedVideoAd.pause(getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();
            mRewardedVideoAd.resume(getActivity());
        }
    }
}

