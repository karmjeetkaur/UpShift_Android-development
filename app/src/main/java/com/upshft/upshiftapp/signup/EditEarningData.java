package com.upshft.upshiftapp.signup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEarningData extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    String platform, amount, tip, date_str, club_key;
    EditText ed_platform_name, ed_amount, ed_tip, ed_date;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_earning_data);

        platform = getIntent().getStringExtra("platform");
        amount = getIntent().getStringExtra("amount");
        tip = getIntent().getStringExtra("tip");
        date_str = getIntent().getStringExtra("date_str");
        club_key = getIntent().getStringExtra("club_key");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
        }

        ed_platform_name = (EditText) findViewById(R.id.ed_platform_name);
        ed_amount = (EditText) findViewById(R.id.ed_amount);
        ed_tip = (EditText) findViewById(R.id.ed_tip);
        ed_date = (EditText) findViewById(R.id.ed_date);
        editButton = (Button) findViewById(R.id.editButton);

        ed_platform_name.setText(platform);
        ed_amount.setText(amount);
        ed_tip.setText(tip);
        ed_date.setText(date_str);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setMatch(platform, club_key);
            }
        });

    }

    public void setMatch(String platform, final String clue_key) {

        mDatabase.child("RevenueRecord")
                .child(mUserId)
                .child("items")
                .orderByChild("platform")
                .equalTo(platform)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String clueKey = childSnapshot.getKey();
                            Log.e("clubkey", "============clubkey========" + clueKey);

                            if (clue_key.equals(clueKey)) {
                                RevenueRecord newPost = childSnapshot.getValue(RevenueRecord.class);
                                long startDate_val = newPost.getDate_time();
                                String amnt = newPost.getTotal_amount();
                                String tip = newPost.getTotal_tip();
                                String platform = newPost.getPlatform();

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String strDate = sdf.format(c.getTime());
                                Log.e("strDate", "=================" + strDate);
                                long timeInMilliseconds = 0;
                                try {
                                    Date mDate = sdf.parse(strDate);
                                    timeInMilliseconds = mDate.getTime();
                                    Log.e("timeInMilliseconds", "=======timeInMilliseconds==========" + timeInMilliseconds);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Log.e("clubkey", "===========" + startDate_val + "====" + amnt + "====" + tip + "===========platform============" + platform);
                                RevenueRecord addListDataModal = new RevenueRecord(timeInMilliseconds, ed_platform_name.getText().toString(),
                                        ed_amount.getText().toString(), ed_tip.getText().toString());
                                mDatabase.child("RevenueRecord").child(mUserId).child("items").child(clueKey).setValue(addListDataModal);
                                onBackPressed();
                                Toast.makeText(getApplicationContext(), "Upadted Data Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
