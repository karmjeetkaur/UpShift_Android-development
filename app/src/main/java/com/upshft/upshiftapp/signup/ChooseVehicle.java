package com.upshft.upshiftapp.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.adapter.RevenueDataListAdapter;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChooseVehicle extends AppCompatActivity
{
    RecyclerView recycleView;
    // database connections
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private ProgressDialog mProgressDialog;
    ArrayList<RevenueRecord> All_revenue_Records = new ArrayList<RevenueRecord>();
    TextView txt_send;
    private SharedPreferences sharedPreferences;
    String email;
    double revenueTotalValue;
    String outputPattern = "dd-MMM-yyyy";
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_vehicle);
        recycleView = (RecyclerView) findViewById(R.id.recycleView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = sharedPreferences.getString("SIGN_IN_EMAIL","");

        txt_send = (TextView) findViewById(R.id.txt_send);

        LinearLayoutManager add_morelayoutManager = new LinearLayoutManager(ChooseVehicle.this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(add_morelayoutManager);

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

        txt_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("email","......email......." + email);
                if(All_revenue_Records.size() !=0)
                {
                    String all_data = "";
                    for(int i=0; i<All_revenue_Records.size(); i++)
                    {
                        Date date =  All_revenue_Records.get(i).getDate();
                        String str = outputFormat.format(date);
                        all_data = all_data + "Platform: " + All_revenue_Records.get(i).getPlatform() + " , " + "Amount: " + All_revenue_Records.get(i).getAmount() + " , " + "Tip: " + All_revenue_Records.get(i).getTip() + " , " +"Date: " + str + ",\n";
                        Log.e("all_data","......all_data......." + all_data);
                    }
                    String mailData = "User Information: \n" + all_data + "Total Amount : " + revenueTotalValue;
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                    i.putExtra(Intent.EXTRA_SUBJECT, "User Details");
                    i.putExtra(Intent.EXTRA_TEXT   , mailData);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ChooseVehicle.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addRevenueRecordChangeListener(ArrayList<RevenueRecord> revenueRecords) {
        double faresTotal = 0;
        double tipsTotal = 0;
        if (revenueRecords.size() != 0) {
            for (RevenueRecord record : revenueRecords) {
                    faresTotal += record.getAmount();
                    tipsTotal += record.getTip();
            }
            double revenueTotal = faresTotal + tipsTotal;
            revenueTotalValue = revenueTotal;
        }
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void setData()
    {
        final ArrayList<RevenueRecord> revenue_Records = new ArrayList<RevenueRecord>();
        mDatabase.child("RevenueRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.getChildrenCount() == 0)
                {
                    Log.e("RevenueRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                }
                else
                {
                    String club_key = dataSnapshot.getKey();
                    Log.e("clubkey", "============clubkey========" + club_key);

                    long startDate_val = (long) dataSnapshot.child("date_time").getValue();
                    String amnt = (String) dataSnapshot.child("total_amount").getValue();
                    String tip = (String) dataSnapshot.child("total_tip").getValue();

                    Log.e("RevenueRecord", ".....amnt....." + amnt);
                    RevenueRecord record = new RevenueRecord();
                    record.setDate(new Date(startDate_val));
                    record.setPlatform((String) dataSnapshot.child("platform").getValue());
                    record.setAmount(Double.parseDouble(amnt));
                    record.setTip(Double.parseDouble(tip));
                    record.setClueKey(club_key);
                    revenue_Records.add(record);
                    addRevenueRecordChangeListener(revenue_Records);
                    All_revenue_Records = revenue_Records;
                    RevenueDataListAdapter mAdapter = new RevenueDataListAdapter(revenue_Records, ChooseVehicle.this);
                    recycleView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

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

    @Override
    protected void onResume()
    {
        Log.e("onResume", "=======onResume working==========");
        setData();
        super.onResume();
    }
}
