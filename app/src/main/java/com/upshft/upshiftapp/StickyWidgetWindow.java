package com.upshft.upshiftapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
/**
 * The {@link StandOutWindow} describing the sticky widget {@link Window}
 */
public class StickyWidgetWindow extends StandOutWindow {
    /**
     * The {@link WidgetIcons} object collecting all data for the sticky widget icons
     */
    public static WidgetIcons icons;

    /**
     * The window service for this {@link StickyWidgetWindow}
     */
    public static StickyWidgetWindow windowService;

    /**
     *
     */
    private SharedPreferences sharedPreferences;

    public static final String ACTION_BROADCAST = "com.upshft.upshift.intent.ACTION_BROADCAST";

    @Override
    public String getAppName() {
        return "UpShift";
    }

    @Override
    public int getAppIcon() {
        return R.mipmap.ic_launcher;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        // create a static reference to WidgetIcon object
        icons = new WidgetIcons(this);

        // create a static reference to this window service
        windowService = this;

        // generate and attach the icon layout to the provided frame
        frame.addView(icons.getLayout());
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        // connect and get preferences for the start position of the sticky widgets
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String widgetStartLocation = sharedPreferences.getString("pref_widget_startup_appearance", "0");

        int windowHorizontalPosition = 0;
        switch (widgetStartLocation) {
            case "0" :
                windowHorizontalPosition = StandOutLayoutParams.LEFT;
                break;

            case "1" :
                windowHorizontalPosition = StandOutLayoutParams.RIGHT;
                break;
        }

        int windowVerticalPosition = StandOutLayoutParams.CENTER;
        return new StandOutLayoutParams(id, 0, 0, windowHorizontalPosition, windowVerticalPosition);
    }

    @Override
    public int getFlags(int id) {
        return super.getFlags(id)
                //| StandOutFlags.FLAG_ADD_FUNCTIONALITY_ALL_DISABLE
                //| StandOutFlags.FLAG_ADD_FUNCTIONALITY_DROP_DOWN_DISABLE
                //| StandOutFlags.FLAG_ADD_FUNCTIONALITY_RESIZE_DISABLE
                | StandOutFlags.FLAG_BODY_MOVE_ENABLE
                //| StandOutFlags.FLAG_DECORATION_CLOSE_DISABLE
                //| StandOutFlags.FLAG_DECORATION_MAXIMIZE_DISABLE
                //| StandOutFlags.FLAG_DECORATION_MOVE_DISABLE
                //| StandOutFlags.FLAG_DECORATION_RESIZE_DISABLE
                //| StandOutFlags.FLAG_DECORATION_SYSTEM
                //| StandOutFlags.FLAG_FIX_COMPATIBILITY_ALL_DISABLE
                //| StandOutFlags.FLAG_WINDOW_ASPECT_RATIO_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE
                | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                //| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE
                ;
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return "UpShift window service running";
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "";
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return null;
    }

    @Override
    public String getHiddenNotificationTitle(int id) {
        return "UpShift widget is hidden";
    }

    @Override
    public String getHiddenNotificationMessage(int id) {
        return "Click to show widget";
    }

    @Override
    public Intent getHiddenNotificationIntent(int id) {
        return new Intent(ACTION_BROADCAST).putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_STICKY_WIDGET_SHOW);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int value = super.onStartCommand(intent, flags, startId);

        resizeWidgetBounds();

        return value;
    }

    private boolean wasNotVisibleOnRotation = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            boolean isVisible = getWindow(DEFAULT_ID).visibility != Window.VISIBILITY_GONE;

            if(!isVisible) {
                wasNotVisibleOnRotation = true;

                // discard the hidden notification from a previous hide command
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }
            else {
                wasNotVisibleOnRotation = false;
//                startService(getHideIntent(this, StickyWidgetWindow.class, DEFAULT_ID));
//
//                // discard the hidden notification from the hide command made on rotation
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancelAll();

                getWindow(DEFAULT_ID).setVisibility(View.GONE);
            }
        } else {
            if(wasNotVisibleOnRotation) {
                startService(getShowIntent(this, StickyWidgetWindow.class, DEFAULT_ID));
                startService(getHideIntent(this, StickyWidgetWindow.class, DEFAULT_ID));
            }
            else {
                getWindow(DEFAULT_ID).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Calculate and resize widget bounding box
     */
    public void resizeWidgetBounds()
    {
        // using the intrinsic drawable dimensions to calculate the widget dimensions
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ub_partner);
        int icon_width = drawable.getIntrinsicWidth();
        int icon_height = drawable.getIntrinsicHeight();

        int paddingTop = 1;
        int paddingBottom = 1;
        int paddingLeft = 1;
        int paddingRight = 1;
        int icons_per_row = 1;
        int icons_per_column = ((LinearLayout)icons.getLayout()).getChildCount();
        int new_width = (icon_width + paddingLeft + paddingRight)*icons_per_row;
        int new_height = (icon_height + paddingTop + paddingBottom)*icons_per_column;

        if(getWindow(DEFAULT_ID) != null)
            getWindow(DEFAULT_ID).edit().setSize(new_width, new_height).commit();
    }


    @Override
    public Notification getHiddenNotification(int id) {
        long when = System.currentTimeMillis();
        int icon = getHiddenIcon();
        String contentTitle = getHiddenNotificationTitle(id);
        String contentText = getHiddenNotificationMessage(id);
        String tickerText = String.format("%s: %s", contentTitle, contentText);

        // the difference here is we are providing the same id
        Intent notificationIntent = getHiddenNotificationIntent(id);

        PendingIntent contentIntent = null;

        if (notificationIntent != null) {
            contentIntent = PendingIntent.getBroadcast(this, 0,
                    notificationIntent,
                    // flag updates existing persistent notification
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

//        Notification notification = new Notification(icon, tickerText, when);
//        notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setWhen(when)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .build();

        return notification;
    }
}