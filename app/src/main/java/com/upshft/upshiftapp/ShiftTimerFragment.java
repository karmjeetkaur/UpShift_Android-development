package com.upshft.upshiftapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.database.shifts.ShiftRecord;
import java.util.Date;
/**
 * The {@link Fragment} for governing the states, behaviors and UI components for the shift timer feature
 */
public class ShiftTimerFragment extends Fragment {
    /**
     * UI object to toggle the shift timer on and off
     */
    private Switch shiftToggleSwitch;

    /**
     * UI object to contain the real-time readout of the shift timer
     */
    private TextView shiftTimerShiftClockTextView;

    /**
     * Value of the shift timer's start time in milliseconds from the system clock
     */
    private long shiftTimerStartTime;

    /**
     * Value of the shift timer's elapsed time in milliseconds since the shift timer's start time not including the paused time
     */
    private long shiftTimerElapsedTime;

    /**
     * Value of the shift timer's paused time in milliseconds since the shift timer's start time
     */
    private long shiftTimerPausedTime;

    /**
     * Value describing whether the shift timer is currently running
     */
    private boolean shiftTimerClockRunning;

    /**
     * A {@link Handler} to attach a daemon thread to handle the running shift timer
     */
    private Handler shiftTimerHandler;

    /**
     * A daemon thread to handle the running shift timre
     */
    private Runnable shiftTimerUpdateThread;

    /**
     * A database access object connection to shift timer records
     */
 //   private ShiftRecordDAO shiftRecordDAO;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;


    /**
     * Default constructor for creating a {@link ShiftTimerFragment}
     */
    public ShiftTimerFragment() {
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /**
         * Initialize instance variables
         */
        // start/stop shift timer button
        shiftToggleSwitch = (Switch) getActivity().findViewById(R.id.shiftTimerSwitch);
        shiftToggleSwitch.setChecked(false);
        shiftToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startShift();
                } else {
                    stopShift();
                }
            }
        });
        // real-time shift clock
        shiftTimerShiftClockTextView = (TextView) getActivity().findViewById(R.id.shiftTimerShiftClockTextView);
     //   shiftTimerShiftClockTextView.setVisibility(TextView.GONE);

        // variables keeping track of the timer properties
        shiftTimerStartTime = 0L;
        shiftTimerElapsedTime = 0L;
        shiftTimerPausedTime = 0L;
        shiftTimerClockRunning = false;
        shiftTimerHandler = new Handler();

        // define thread to keep track of shift timer
        shiftTimerUpdateThread = new Runnable() {
            public void run() {
                // calculate elapsed time from the initial start time to the final stop time accounting for the total paused time
                shiftTimerElapsedTime = (System.currentTimeMillis() - shiftTimerStartTime) + shiftTimerPausedTime;
                // update UI objects
                shiftTimerShiftClockTextView.setText(getTimeAsString(shiftTimerElapsedTime));
                // schedule thread updates
                shiftTimerHandler.postDelayed(this, 0);
            }
        };
        // initialize database connection
   //     shiftRecordDAO = new ShiftRecordDAO(getActivity());
        /**
         * perform initialization actions
         */
        // updateTotalShiftsClock();
    }

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

    public boolean isShiftTimerRunning()
    {
        return shiftTimerClockRunning;
    }

    /**
     * Start shift
     */
    public void startShift() {
        if (!shiftTimerClockRunning) {
            // grab a store current time
            shiftTimerStartTime = System.currentTimeMillis();

            // schedule handler to update
            shiftTimerHandler.postDelayed(shiftTimerUpdateThread, 0);

            // set timer as running
            shiftTimerClockRunning = true;

            shiftTimerShiftClockTextView.setVisibility(TextView.VISIBLE);
            shiftToggleSwitch.setChecked(shiftTimerClockRunning);
        } else {
            Toast.makeText(getActivity(), "Shift timer cannot start. Already running!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pause shift
     */
    public void pauseShift()
    {
        if(shiftTimerClockRunning) {
            // update paused time
            shiftTimerPausedTime += shiftTimerElapsedTime;
            // removed scheduled updates from handler
            shiftTimerHandler.removeCallbacks(shiftTimerUpdateThread);
            // set timer as no longer running
            shiftTimerClockRunning = false;
        } else {
            Toast.makeText(getActivity(), "Shift timer cannot pause. Already stopped!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stop shift
     */
    public void stopShift()
    {
        // test for significant records via non-zero elapsed time
        if (shiftTimerElapsedTime != 0L)
        {
            // create a new shift record from the timer data
            ShiftRecord newRecord = new ShiftRecord();
            newRecord.setStart(new Date(shiftTimerStartTime));
            newRecord.setEnd(new Date(shiftTimerStartTime + shiftTimerElapsedTime));
            // insert the new record
        //    shiftRecordDAO.insert(newRecord);

            if (mFirebaseUser == null) {
                // Not logged in, launch the Log In activity
            } else {
                mUserId = mFirebaseUser.getUid();
                ShiftRecord item = new ShiftRecord(shiftTimerStartTime, shiftTimerStartTime + shiftTimerElapsedTime);
                mDatabase.child("ShiftRecord").child(mUserId).child("items").push().setValue(item);
            }
            // update total shifts clock
            updateTotalShiftsClock();

            // re-initialize instance variables
            shiftTimerStartTime = 0L;
            shiftTimerElapsedTime = 0L;
            shiftTimerPausedTime = 0L;
            shiftTimerClockRunning = false;
            shiftTimerHandler.removeCallbacks(shiftTimerUpdateThread);
            shiftTimerShiftClockTextView.setText("00:00:00");
         //   shiftTimerShiftClockTextView.setVisibility(TextView.GONE);
            shiftToggleSwitch.setChecked(shiftTimerClockRunning);
        } else {
            // Nothing yet to add to the database
        }
    }

    /**
     * Update the UI summary info objects as well as trigger other fragments to update their summary info
     */
    private void updateTotalShiftsClock() {
        SummaryFragment summaryFragment = (SummaryFragment) getFragmentManager().findFragmentByTag(DashboardActivity.SummaryFragmentTag);
        summaryFragment.updateSummaries("");
    }

    /**
     * Helper method to convert a time in milliseconds to a {@link String}
     *
     * @param milliseconds
     *      the time in milliseconds to be converted into a {@link String}
     * @return
     *      A {@link String} representation of a time in milliseconds in the format hh:mm:ss
     */
    public static String getTimeAsString(long milliseconds) {
        long seconds, minutes, hours;

        seconds = (int) (milliseconds / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
        milliseconds = (int) (milliseconds % 1000);
        hours = minutes / 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}