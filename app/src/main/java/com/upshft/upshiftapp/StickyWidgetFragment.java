package com.upshft.upshiftapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import wei.mark.standout.StandOutWindow;

/**
 * The {@link Fragment} for governing the states, behaviors and UI components for the sticky widget feature
 */
public class StickyWidgetFragment extends Fragment {
    /**
     * Value representing whether the sticky widget's window service is running
     */
    private boolean windowServiceRunning;

    /**
     * Value representing whether the sticky widget is currently showing
     */
    private boolean stickyWidgetShowing;

    /**
     * UI object representing the switch to show or hide the sticky widget
     */
    private ToggleButton widgetVisibilityToggleButton;

    /**
     * Object to access and modify app preferences
     */
    private SharedPreferences sharedPreferences;

    /**
     * Default constructor for creating a {@link StickyWidgetFragment}
     */
    public StickyWidgetFragment() {
        // required empty default constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // assume we have permissions on lower build versions due to the old-style manifest permission declaration
        boolean widgetPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getActivity())) {
                widgetPermission = false;

                AlertDialog dialog = new AlertDialog.Builder(StickyWidgetFragment.this.getActivity())
                        .setTitle("Permission Request")
                        .setMessage("The widget feature requires a special permission to function correctly. When asked on the next screen, please enable UpShift to draw overlays. Then you can hit the BACK button to return to the app.")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                intent.setData(Uri.parse("package:com.upshft.upshiftprime"));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

        /**
         * initialize instance variables
         */
        windowServiceRunning = false;

        stickyWidgetShowing = false;

        widgetVisibilityToggleButton = (ToggleButton) getActivity().findViewById(R.id.togglewidgetSwitch);
        widgetVisibilityToggleButton.setChecked(false);
        widgetVisibilityToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showStickyWidget();
                } else {
                    hideStickyWidget();
                }
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /**
         * perform initialization actions
         */

        // autostart sticky widget based on preferences
        if(widgetPermission)
            showStickyWidget();
        boolean autostart = sharedPreferences.getBoolean("pref_autostart_checkbox", true);
        boolean isfirstRun = sharedPreferences.getBoolean("pref_first_run", true);
        if((!autostart || isfirstRun) && widgetPermission) {
            hideStickyWidget();
        }

        // update UI
        widgetVisibilityToggleButton.setChecked(stickyWidgetShowing);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopWindowService();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Show the sticky widget
     */
    public void showStickyWidget() {
        if(!stickyWidgetShowing) {
            StandOutWindow.show(getActivity(), StickyWidgetWindow.class, StandOutWindow.DEFAULT_ID);

            windowServiceRunning = true;
            stickyWidgetShowing = true;

            widgetVisibilityToggleButton.setChecked(stickyWidgetShowing);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }

    /**
     * Hide the sticky widget
     */
    public void hideStickyWidget() {
        if(windowServiceRunning && stickyWidgetShowing) {
            StandOutWindow.hide(getActivity(), StickyWidgetWindow.class, StandOutWindow.DEFAULT_ID);

            stickyWidgetShowing = false;

            widgetVisibilityToggleButton.setChecked(stickyWidgetShowing);
        }
    }

    /**
     * Stop the window service
     */
    public void stopWindowService() {
        if (windowServiceRunning) {
            StandOutWindow.closeAll(getActivity(), StickyWidgetWindow.class);
            StickyWidgetWindow.windowService.stopSelf();

            windowServiceRunning = false;
            stickyWidgetShowing = false;

            widgetVisibilityToggleButton.setChecked(stickyWidgetShowing);
        }
    }
}