package com.upshft.upshiftapp;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecord;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.upshft.upshiftapp.modal.TotalMiles;
import java.util.ArrayList;
import java.util.Date;

public class DeadheadTrackerFragment extends Fragment implements LocationListener {
    private Switch deadheadSwitch;
    private TextView deadheadTrackerDistanceTextView;
    TextView textViewdeadhead_value;
    private TextView deadheadTrackerTotalDistanceTextView;
    private boolean deadheadTrackerRunning;
    private Location lastLocation;
    private long deadheadTrackerStartTime;
    private double deadheadTrackerDistance;
    //  private DeadheadRecordDAO deadheadRecordDAO;
    public int PERM_REQUEST_CODE_DRAW_OVERLAYS = 21324;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    double deadheadTotalmiles = 0;

    public DeadheadTrackerFragment() {
        // required empty default constructor
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog dialog = new AlertDialog.Builder(DeadheadTrackerFragment.this.getActivity())
                    .setTitle("Permission Request")
                    .setMessage("The deadhead tracker feature requires permission to access your GPS location in order to function correctly. When asked on the upcoming dialog, please enable UpShift to access your GPS.")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        }
                    })
                    .show();
        }

      //  final RelativeLayout container = (RelativeLayout) getActivity().findViewById(R.id.deadhead_row);
     //   LayoutTransition transition = container.getLayoutTransition();
    //    transition.enableTransitionType(LayoutTransition.CHANGING);
        // start/stop deadhead tracker from switch
        deadheadSwitch = (Switch) getActivity().findViewById(R.id.deadhead_switch);
        deadheadSwitch.setChecked(false);
        deadheadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                    intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_START);
                    getActivity().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                    intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_STOP);
                    getActivity().sendBroadcast(intent);
                }
            }
        });


        deadheadTrackerDistanceTextView = (TextView) getActivity().findViewById(R.id.deadheadTrackerDistanceTextView);
        textViewdeadhead_value = (TextView) getActivity().findViewById(R.id.textViewdeadhead_value);
        //  deadheadTrackerDistanceTextView.setVisibility(View.GONE);
        deadheadTrackerTotalDistanceTextView = (TextView) getActivity().findViewById(R.id.deadheadTrackerTotalDistanceTextView);

        deadheadTrackerRunning = false;
        deadheadTrackerStartTime = 0L;
        deadheadTrackerDistance = 0;

        //deadheadRecordDAO = new DeadheadRecordDAO(getActivity());

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
        }

        updateTotalDistance();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public boolean isDeadheadTrackerRunning() {
        return deadheadTrackerRunning;
    }

    public void onStartTrackDeadhead() {
        if (!deadheadTrackerRunning) {
            // check if gps is available
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // notify
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("GPS was found to be disabled. Would you like to enable it?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                        // get gps and automatically enable deadhead tracker as expected
                        Toast.makeText(getActivity(), "Once you enable GPS, please re-initiate deadhead miles tracker", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(getActivity(), "Could not start deadhead miles tracker", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

                deadheadSwitch.setChecked(false);
                Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_STOP);
                getActivity().sendBroadcast(intent);
            } else if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
                // notify
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Google Play Services Error");
                // might give more information about the specific ConnectionResult returned
                dialog.setMessage("There was an error starting Google Play Services. Try updating it in the Play Store.");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // nothing to do
                    }
                });
                dialog.show();

                deadheadSwitch.setChecked(false);
                Intent intent = new Intent(StickyWidgetWindow.ACTION_BROADCAST);
                intent.putExtra(DashboardActivity.ACTION, DashboardActivity.ACTION_DEADHEAD_TRACKER_STOP);
                getActivity().sendBroadcast(intent);
            } else {
                deadheadTrackerStartTime = System.currentTimeMillis();
                deadheadTrackerDistance = 0;
                lastLocation = null;
                deadheadTrackerRunning = startLocationUpdates();
                deadheadTrackerRunning = true;
                deadheadSwitch.setChecked(deadheadTrackerRunning);
                deadheadTrackerDistanceTextView.setVisibility(View.VISIBLE);
            }
        } else {
            // Deadhead tracker is already running
        }
    }

    public void onStopTrackDeadhead() {
        if (deadheadTrackerRunning) {
            if (deadheadTrackerDistance != 0) {
                DeadheadRecord newRecord = new DeadheadRecord();
                newRecord.setStart(new Date(deadheadTrackerStartTime));
                newRecord.setEnd(new Date(System.currentTimeMillis()));
                newRecord.setDistance(deadheadTrackerDistance);

                // deadheadRecordDAO.insert(newRecord);
                textViewdeadhead_value.setText(String.format("%.2f", deadheadTrackerDistance));
                DeadheadRecord item = new DeadheadRecord(deadheadTrackerStartTime, System.currentTimeMillis(), deadheadTrackerDistance);
                mDatabase.child("DeadheadRecord").child(mUserId).child("items").push().setValue(item);
            }

            updateTotalDistance();
            stopLocationUpdates();

            deadheadTrackerRunning = false;
            deadheadTrackerStartTime = 0L;
            deadheadTrackerDistance = 0;
            lastLocation = null;

            deadheadTrackerDistanceTextView.setText("0.00");
          //  textViewdeadhead_value.setText("0.00");
            deadheadSwitch.setChecked(deadheadTrackerRunning);
            //  deadheadTrackerDistanceTextView.setVisibility(View.GONE);
        } else {
            // Nothing yet to add to the database
        }
    }

    private boolean startLocationUpdates() {
//        if (getActivity().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED ||
//                (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, getActivity());
            LocationServices.FusedLocationApi.requestLocationUpdates(GooglePlayServicesFragment.googleApiClient, GooglePlayServicesFragment.locationRequest, this);
            return true;
        } else {
            Toast.makeText(getActivity(), "Oops, GPS permission check failed", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(GooglePlayServicesFragment.googleApiClient, this);
    }

    private void updateTotalDistance() {

        SummaryFragment summaryFragment = (SummaryFragment) getFragmentManager().findFragmentByTag(DashboardActivity.SummaryFragmentTag);
        summaryFragment.updateSummaries("");
        double total = 0;
//        for (DeadheadRecord record : deadheadRecordDAO.getRecords()) {
//            total += record.getDistance();
//        }
//
//        deadheadTrackerTotalDistanceTextView.setText(String.format("%.2f", total));

        final ArrayList<DeadheadRecord> deadhead_Records = new ArrayList<DeadheadRecord>();
        mDatabase.child("DeadheadRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.e("DeadheadRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                } else {
                    long startDate_val = (long) dataSnapshot.child("startDate").getValue();
                    long endDate_val = (long) dataSnapshot.child("endDate").getValue();
                    DeadheadRecord record = new DeadheadRecord();
                    record.setStart(new Date(startDate_val));
                    record.setEnd(new Date(endDate_val));
                    record.setDistance((Double) dataSnapshot.child("distance").getValue());
                    deadhead_Records.add(record);

                    double deadheadTotal = 0;
                    if (deadhead_Records.size() != 0) {
                        for (DeadheadRecord Record : deadhead_Records) {
                            deadheadTotal += Record.getDistance();
                        }
                        deadheadTotalmiles = deadheadTotal;
                   //     deadheadTrackerTotalDistanceTextView.setText(String.format("%.2f", deadheadTotal));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Log.e("deadheadTotalmiles","...........deadheadTotalmiles......" + deadheadTotalmiles);
        if(deadheadTotalmiles != 0)
        {
            Log.e("deadheadTotalmiles","...........if......" + deadheadTotalmiles);
            TotalMiles miles = new TotalMiles(deadheadTotalmiles);
            mDatabase.child("TotalMiles").child(mUserId).child("items").push().setValue(miles);
        }
        else
        {
            Log.e("deadheadTotalmiles","...........elase......" + deadheadTotalmiles);
        }


    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (lastLocation != null) {
                double miles = (lastLocation.distanceTo(location)) / 1609.344;
                deadheadTrackerDistance += miles;

                deadheadTrackerDistanceTextView.setText(String.format("%.2f", deadheadTrackerDistance));
                textViewdeadhead_value.setText(String.format("%.2f", deadheadTrackerDistance));
            }

            lastLocation = location;
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(getActivity())) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                } else {
                    Log.d("ScreenOverLay", "Screen overlay detected");
                    getActivity().finish();
                    Intent mStartActivity = new Intent(getActivity(), DashboardActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                }
            }
        }
    }
}