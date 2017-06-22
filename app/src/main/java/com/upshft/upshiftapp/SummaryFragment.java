package com.upshft.upshiftapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecord;
import com.upshft.upshiftapp.database.expenses.ExpenseRecord;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import com.upshft.upshiftapp.database.shifts.ShiftRecord;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class SummaryFragment extends Fragment
{
    // vars for spinner info that need to be moved to fragment...
    private static final String TODAY = "Today";
    private static final String WEEK = "Week";
    private static final String MONTH = "Month";
    private static final String YEAR = "Year";
    private String[] strings = {TODAY, WEEK, MONTH, YEAR};
    private int arr_spinner_icons[] = {R.drawable.calendar,
            R.drawable.calendar,
            R.drawable.calendar,
            R.drawable.calendar};

    // database connections
    //  private DeadheadRecordDAO deadheadRecordDAO;
    // private ExpenseRecordDAO expenseRecordDAO;
    //   private RevenueRecordDAO revenueRecordDAO;
    //  private ShiftRecordDAO shiftRecordDAO;
    // ui elements for summary info

    private TextView deadheadLabel;
    private TextView deadheadTextView;
    private TextView shiftLabel;
    private TextView shiftTextView;
    private TextView faresTextView;
    private TextView tipsTextView;
    private TextView moneyLabel;
    private TextView expenseTextView;
    private TextView revenueTextView;
    private TextView txt_hourly_rate;
    private Spinner timeRangeSpinner;
    private TextView profitTextView;
    SimpleDateFormat df2 = new SimpleDateFormat("E MMM dd HH:mm:ss ZZZZ yyyy");
    // DateFormat dfm = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private ProgressDialog mProgressDialog;
    double revenueTotalValue;
    double expenseTotalValue;
    long shiftTotalValue;
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    public SummaryFragment() {
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

        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "com.upshft.upshift",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            Log.e("mUserId", ".........mUserId.........." + mUserId);
        }
        // deadheadRecordDAO = new DeadheadRecordDAO(getActivity());
        //expenseRecordDAO = new ExpenseRecordDAO(getActivity());
        // revenueRecordDAO = new RevenueRecordDAO(getActivity());
        // shiftRecordDAO = new ShiftRecordDAO(getActivity());

        deadheadLabel = (TextView) getActivity().findViewById(R.id.textView3);
        deadheadTextView = (TextView) getActivity().findViewById(R.id.deadheadTrackerTotalDistanceTextView);
        shiftLabel = (TextView) getActivity().findViewById(R.id.titleTotalTime);
        shiftTextView = (TextView) getActivity().findViewById(R.id.shiftTimerTotalShiftsClockTextView);
        faresTextView = (TextView) getActivity().findViewById(R.id.textView7);
        tipsTextView = (TextView) getActivity().findViewById(R.id.textView9);
        moneyLabel = (TextView) getActivity().findViewById(R.id.textView2);
        expenseTextView = (TextView) getActivity().findViewById(R.id.textView10);
        revenueTextView = (TextView) getActivity().findViewById(R.id.textView11);
        profitTextView = (TextView) getActivity().findViewById(R.id.textView13);
        txt_hourly_rate = (TextView) getActivity().findViewById(R.id.txt_hourly_rate);

        // this spinner still needs to be converted to a fragment
        timeRangeSpinner = (Spinner) getActivity().findViewById(R.id.spinner_time_range);
        timeRangeSpinner.setAdapter(new SpinnerAdapter(getActivity(), R.layout.spinner_row, strings));

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                updateSummaries("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

        updateSummaries("");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // the functioning spinner code that still needs to be added to a fragment
    // there were issues with the Layout Inflater when I tried to put it in a fragment sorry, I tried - Adam
    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.time_range);
            label.setText(strings[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.image);
            icon.setImageResource(arr_spinner_icons[position]);

            return row;
        }
    }

    public void updateSummaries(final String status)
    {
        int index = timeRangeSpinner.getSelectedItemPosition();
        String periodAdverb = "";
        if (strings[index].equals(TODAY))
            periodAdverb = "DAILY";
        else if (strings[index].equals(WEEK))
            periodAdverb = "WEEKLY";
        else if (strings[index].equals(MONTH))
            periodAdverb = "MONTHLY";
        else if (strings[index].equals(YEAR))
            periodAdverb = "YEARLY";

        String periodPossessive = "";
        if (strings[index].equals(TODAY))
            periodPossessive = "Today's";
        else if (strings[index].equals(WEEK))
            periodPossessive = "This Week's";
        else if (strings[index].equals(MONTH))
            periodPossessive = "This Month's";
        else if (strings[index].equals(YEAR))
            periodPossessive = "This Year's";

        // get a Calendar object for now
        GregorianCalendar now = new GregorianCalendar();
        // create a Calendar object for the beginning of today, including the local timezone
        final GregorianCalendar filterStart = new GregorianCalendar(now.get(GregorianCalendar.YEAR), now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH));

        // adjust the filter start Calendar object by the Spinner amount
        if (strings[index].equals(WEEK))
            filterStart.add(GregorianCalendar.DAY_OF_MONTH, -7);
        else if (strings[index].equals(MONTH))
            filterStart.add(GregorianCalendar.MONTH, -1);
        else if (strings[index].equals(YEAR))
            filterStart.add(GregorianCalendar.YEAR, -1);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            Log.e("mUserId", ".........mUserId.........." + mUserId);

            // get DeadheadRecord data from FireBase database

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
                        addDeadheadRecordChangeListener(deadhead_Records, filterStart,status);
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


// get ShiftRecord data from FireBase database

            final ArrayList<ShiftRecord> shiftRecords = new ArrayList<ShiftRecord>();
            //    showProgressDialog();
            mDatabase.child("ShiftRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        Log.e("dataSnapshot", ".....dataSnapshot.getChildrenCount()....." + dataSnapshot.getChildrenCount());
                    } else {
                        long startDate_val = (long) dataSnapshot.child("startDate").getValue();
                        long endDate_val = (long) dataSnapshot.child("endDate").getValue();
                        ShiftRecord record = new ShiftRecord();
                        record.setStart(new Date(startDate_val));
                        record.setEnd(new Date(endDate_val));
                        shiftRecords.add(record);
                        addShiftRecordChangeListener(shiftRecords, filterStart);
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

// get RevenueRecord data from FireBase database

            final ArrayList<RevenueRecord> revenue_Records = new ArrayList<RevenueRecord>();
            mDatabase.child("RevenueRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        Log.e("RevenueRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                    } else {
                        long startDate_val = (long) dataSnapshot.child("date_time").getValue();
                        String amnt = (String) dataSnapshot.child("total_amount").getValue();
                        String tip = (String) dataSnapshot.child("total_tip").getValue();
                        Log.e("RevenueRecord", ".....amnt....." + amnt);
                        RevenueRecord record = new RevenueRecord();
                        record.setDate(new Date(startDate_val));
                        record.setPlatform((String) dataSnapshot.child("platform").getValue());
                        record.setAmount(Double.parseDouble(amnt));
                        record.setTip(Double.parseDouble(tip));
                        revenue_Records.add(record);
                        addRevenueRecordChangeListener(revenue_Records, filterStart);
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

// get ExpenseRecord data from FireBase database

            final ArrayList<ExpenseRecord> expense_Records = new ArrayList<ExpenseRecord>();
            mDatabase.child("ExpenseRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        Log.e("ExpenseRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                    } else {
                        long startDate_val = (long) dataSnapshot.child("date_time").getValue();
                        String amnt = (String) dataSnapshot.child("total_amount").getValue();
                        ExpenseRecord record = new ExpenseRecord();
                        record.setDate(new Date(startDate_val));
                        record.setCategory((String) dataSnapshot.child("category").getValue());
                        record.setAmount(Double.parseDouble(amnt));
                        record.setDescription((String) dataSnapshot.child("description").getValue());
                        expense_Records.add(record);
                        addExpenseRecordChangeListener(expense_Records, filterStart);
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
        }

//        double deadheadTotal = 0;
//        ArrayList<DeadheadRecord> deadheadRecords = deadheadRecordDAO.getRecords();
//        for (DeadheadRecord record : deadheadRecords) {
//            // check data is within the desired range
//            if (record.getStart().after(filterStart.getTime()) || record.getStart().equals(filterStart.getTime())) {
//                deadheadTotal += record.getDistance();
//                Log.e("DeadheadRecord", ".....getDistance....." + deadheadTotal);
//            }
//        }

//        long shiftTotal = 0;
//        ArrayList<ShiftRecord> shift_Records = shiftRecordDAO.getRecords();
//        for (ShiftRecord record : shift_Records) {
//            if (record.getStart().after(filterStart.getTime()) || record.getStart().equals(filterStart.getTime())) {
//                shiftTotal += record.getEnd().getTime() - record.getStart().getTime();
//                Log.e("dataSnapshot", "......SQL_shiftTotal......" + shiftTotal);
//                Log.e("dataSnapshot", "......SQL_shiftTotal......" + ShiftTimerFragment.getTimeAsString(Math.abs(shiftTotal)));
//            }
//        }

//        double faresTotal = 0;
//        double tipsTotal = 0;
//        ArrayList<RevenueRecord> revenueRecords = revenueRecordDAO.getRecords();
//        for (RevenueRecord record : revenueRecords) {
//            // check data is within the desired range
//            if (record.getDate().after(filterStart.getTime()) || record.getDate().equals(filterStart.getTime())) {
//                faresTotal += record.getAmount();
//                tipsTotal += record.getTip();
//                Log.e("RevenueRecord", ".....SQL_faresTotal....." + faresTotal + ".....tipsTotal......" +tipsTotal);
//            }
//        }

//        double expenseTotal = 0;
//        ArrayList<ExpenseRecord> expenseRecords = expenseRecordDAO.getRecords();
//        for (ExpenseRecord record : expenseRecords) {
//            // check data is within the desired range
//            if (record.getDate().after(filterStart.getTime()) || record.getDate().equals(filterStart.getTime())) {
//                expenseTotal += record.getAmount();
//            }
//        }

//        double revenueTotal = faresTotal + tipsTotal;
//        double netProfitTotal = revenueTotal - expenseTotal;
//
//        String shift_time = ShiftTimerFragment.getTimeAsString(Math.abs(shiftTotal));
//        String timeSplit[] = shift_time.split(":");
//        int seconds = Integer.parseInt(timeSplit[0]) * 60 * 60 + Integer.parseInt(timeSplit[1]) * 60 + Integer.parseInt(timeSplit[2]);
//        double hourly_rate = netProfitTotal / seconds;
//        txt_hourly_rate.setText(String.format(netProfitTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(hourly_rate)));

        deadheadLabel.setText(String.format("%s MILES", periodAdverb));
        shiftLabel.setText(String.format("%s HOURS", periodAdverb));
        moneyLabel.setText(String.format("%s", periodPossessive));
        // deadheadTextView.setText(String.format(deadheadTotal < 0 ? "-$.2f" : "%.2f", Math.abs(deadheadTotal)));
        // shiftTextView.setText(String.format(shiftTotal < 0 ? "-%s" : "%s", ShiftTimerFragment.getTimeAsString(Math.abs(shiftTotal))));
        // faresTextView.setText(String.format(faresTotal < 0 ? "FARES: -$%.2f" : "FARES: $%.2f", Math.abs(faresTotal)));
        // tipsTextView.setText(String.format(tipsTotal < 0 ? "TIPS: -$%.2f" : "TIPS: $%.2f", Math.abs(tipsTotal)));
        // expenseTextView.setText(String.format(expenseTotal < 0 ? "Expenses: -$%.2f" : "Expenses: $%.2f", Math.abs(expenseTotal)));

//         revenueTextView.setText(String.format(revenueTotal < 0 ? "Revenue: -$%.2f" : "Revenue: $%.2f", Math.abs(revenueTotal)));
//         profitTextView.setText(String.format(netProfitTotal < 0 ? "Net: -$%.2f" : "Net: $%.2f", Math.abs(netProfitTotal)));
    }


    /**
     * User data change listener
     */
    private void addShiftRecordChangeListener(ArrayList<ShiftRecord> shiftRecords, GregorianCalendar filterStart) {
        long shiftTotal = 0;
        if (shiftRecords.size() != 0) {
            for (ShiftRecord record : shiftRecords) {
                if (record.getStart().after(filterStart.getTime()) || record.getStart().equals(filterStart.getTime())) {
                    shiftTotal += record.getEnd().getTime() - record.getStart().getTime();
                }
            }
            shiftTotalValue = shiftTotal;
            Log.e("shiftTotal","...shiftTotal........" +shiftTotal);
            shiftTextView.setText(String.format(shiftTotal < 0 ? "-%s" : "%s", ShiftTimerFragment.getTimeAsString(Math.abs(shiftTotal))));
            updateCalculation();
        }
    }

    private void addDeadheadRecordChangeListener(ArrayList<DeadheadRecord> deadheadRecords, GregorianCalendar filterStart,String status) {
        double deadheadTotal = 0;
        if (deadheadRecords.size() != 0) {
            for (DeadheadRecord record : deadheadRecords) {
                if (record.getStart().after(filterStart.getTime()) || record.getStart().equals(filterStart.getTime())) {
                    deadheadTotal += record.getDistance();
                }
            }
            deadheadTextView.setText(String.format(deadheadTotal < 0 ? "-$.2f" : "%.2f", Math.abs(deadheadTotal)));
        }
    }

    private void addRevenueRecordChangeListener(ArrayList<RevenueRecord> revenueRecords, GregorianCalendar filterStart) {
        double faresTotal = 0;
        double tipsTotal = 0;
        if (revenueRecords.size() != 0) {
            for (RevenueRecord record : revenueRecords) {
                if (record.getDate().after(filterStart.getTime()) || record.getDate().equals(filterStart.getTime())) {
                    faresTotal += record.getAmount();
                    tipsTotal += record.getTip();
                }
            }
            double revenueTotal = faresTotal + tipsTotal;
            revenueTotalValue = revenueTotal;
            faresTextView.setText(String.format(faresTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(faresTotal)));
            tipsTextView.setText(String.format(tipsTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(tipsTotal)));
            revenueTextView.setText(String.format(revenueTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(revenueTotal)));
            updateCalculation();
        }
    }

    private void addExpenseRecordChangeListener(ArrayList<ExpenseRecord> expenseRecords, GregorianCalendar filterStart) {
        double expenseTotal = 0;
        if (expenseRecords.size() != 0) {
            for (ExpenseRecord record : expenseRecords) {
                // check data is within the desired range
                if (record.getDate().after(filterStart.getTime()) || record.getDate().equals(filterStart.getTime())) {
                    expenseTotal += record.getAmount();
                }
            }
            expenseTotalValue = expenseTotal;
            expenseTextView.setText(String.format(expenseTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(expenseTotal)));
            updateCalculation();
        }
    }

    public void updateCalculation()
    {
        long timeInMilliseconds = 0;
        String givenDateString =  shiftTextView.getText().toString();
        Log.e("givenDateString" ,"Date in givenDateString :: " + givenDateString);
        double netProfitTotal = revenueTotalValue - expenseTotalValue;
        totalTime(givenDateString,netProfitTotal,txt_hourly_rate);
        profitTextView.setText(String.format(netProfitTotal < 0 ? "-$%.2f" : "$%.2f", Math.abs(netProfitTotal)));
    }

    public static void totalTime(String time2,double netProfitTotal,TextView txt_hourly_rate)
    {
      //  time2 = "01:30:21";
        double hourly_rate = 0;
        int total_hour = getTotalHour(time2);
        Log.e("ResultTime", " .......total_hour........" + total_hour);
        int total_minute = getTotalMinutes(time2);
        Log.e("ResultTime", " .......total_minute........" + total_minute);
        int total_seconds = getTotalSeconds(time2);
        Log.e("ResultTime", " .......total_seconds........" + total_seconds);

        if(total_minute != 0)
        {
            hourly_rate = (netProfitTotal*total_minute)/60;
            Log.e("ResultTime", " .......hourly_rate........" + hourly_rate);
            txt_hourly_rate.setText(String.format(hourly_rate < 0 ? "-$%.2f" : "$%.2f", Math.abs(hourly_rate)));
        }
        else {
            txt_hourly_rate.setText(String.format(hourly_rate < 0 ? "-$%.2f" : "$%.2f", Math.abs(hourly_rate)));
        }
        String result = getResult(total_minute);
       // Log.e("ResultTime",result);
    }

    public static int getTotalMinutes(String time) {
        String[] t = time.split(":");
        return Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1]);
    }

    public static int getTotalSeconds(String time) {
        String[] t = time.split(":");
        return Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1] + Integer.valueOf(t[2]));
    }

    public static int getTotalHour(String time) {
        String[] t = time.split(":");
        return Integer.valueOf(t[0]) * 60;
    }

    public static String getResult(int total)
    {
        int minutes = total % 60;
        int hours = ((total - minutes) / 60) % 24;
        return String.format("%02d:%02d", hours, minutes);
    }
}