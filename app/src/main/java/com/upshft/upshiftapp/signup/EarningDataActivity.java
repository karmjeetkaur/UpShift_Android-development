package com.upshft.upshiftapp.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.upshft.upshiftapp.adapter.ExpenseDataListAdapter;
import com.upshft.upshiftapp.adapter.RevenueDataListAdapter;
import com.upshft.upshiftapp.database.expenses.ExpenseRecord;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarningDataActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView txt_revenue, txt_expenses;
    RecyclerView recycleView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private ProgressDialog mProgressDialog;
    ArrayList<RevenueRecord> All_revenue_Records = new ArrayList<RevenueRecord>();
    ArrayList<ExpenseRecord> All_expense_Records = new ArrayList<ExpenseRecord>();
    TextView txt_send;
    private SharedPreferences sharedPreferences;
    String email;
    double revenueTotalValue, expenseTotalValue;
    String outputPattern = "dd-MMM-yyyy";
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
    public static boolean tab_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_data);

        txt_revenue = (TextView) findViewById(R.id.txt_revenue);
        txt_expenses = (TextView) findViewById(R.id.txt_expenses);
        txt_send = (TextView) findViewById(R.id.txt_send);
        recycleView = (RecyclerView) findViewById(R.id.recycleView);

        txt_revenue.setOnClickListener(this);
        txt_expenses.setOnClickListener(this);

        LinearLayoutManager add_morelayoutManager = new LinearLayoutManager(EarningDataActivity.this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(add_morelayoutManager);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            Log.e("mUserId", ".........mUserId.........." + mUserId);
        }


        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (tab_check == true)
                {
                    if (All_revenue_Records.size() != 0) {
                        String all_data = "";
                        for (int i = 0; i < All_revenue_Records.size(); i++) {
                            Date date = All_revenue_Records.get(i).getDate();
                            String str = outputFormat.format(date);
                            all_data = all_data + "Platform: " + All_revenue_Records.get(i).getPlatform() + " , " + "Amount: " + All_revenue_Records.get(i).getAmount() + " , " + "Tip: " + All_revenue_Records.get(i).getTip() + " , " + "Date: " + str + ",\n";
                            Log.e("all_data", "......all_data......." + all_data);
                        }
                        String mailData = "User Information: \n" + all_data + "Total Amount : " + revenueTotalValue;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        i.putExtra(Intent.EXTRA_SUBJECT, "User Details");
                        i.putExtra(Intent.EXTRA_TEXT, mailData);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(EarningDataActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    if (All_expense_Records.size() != 0)
                    {
                        String all_data = "";
                        for (int i = 0; i < All_expense_Records.size(); i++)
                        {
                            Date date = All_expense_Records.get(i).getDate();
                            String str = outputFormat.format(date);
                            all_data = all_data + "Category: " + All_expense_Records.get(i).getCategory() + " , " + "Amount: " + All_expense_Records.get(i).getAmount() + " , " + "Desc: " + All_expense_Records.get(i).getDescription() + " , " + "Date: " + str + ",\n";
                            Log.e("all_data", "......all_data......." + all_data);
                        }
                        String mailData = "User Information: \n" + all_data + "Total Amount : " + expenseTotalValue;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        i.putExtra(Intent.EXTRA_SUBJECT, "User Details");
                        i.putExtra(Intent.EXTRA_TEXT, mailData);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(EarningDataActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_revenue:
                tab_check = true;
                txt_revenue.setBackgroundColor(Color.parseColor("#405266"));
                txt_revenue.setTextColor(Color.parseColor("#ffffff"));
                txt_expenses.setBackgroundColor(Color.parseColor("#ffffff"));
                txt_expenses.setTextColor(Color.parseColor("#000000"));

                setRevenueData();

                break;

            case R.id.txt_expenses:
                tab_check = false;
                txt_revenue.setBackgroundColor(Color.parseColor("#ffffff"));
                txt_revenue.setTextColor(Color.parseColor("#000000"));
                txt_expenses.setBackgroundColor(Color.parseColor("#405266"));
                txt_expenses.setTextColor(Color.parseColor("#ffffff"));

                setExpensesData();

                break;

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

    public void setExpensesData() {
        final ArrayList<ExpenseRecord> expense_Records = new ArrayList<ExpenseRecord>();
        mDatabase.child("ExpenseRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.e("ExpenseRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                } else {
                    String club_key = dataSnapshot.getKey();
                    Log.e("clubkey", "============clubkey========" + club_key);

                    long startDate_val = (long) dataSnapshot.child("date_time").getValue();
                    String amnt = (String) dataSnapshot.child("total_amount").getValue();
                    ExpenseRecord record = new ExpenseRecord();
                    record.setDate(new Date(startDate_val));
                    record.setCategory((String) dataSnapshot.child("category").getValue());
                    record.setAmount(Double.parseDouble(amnt));
                    record.setDescription((String) dataSnapshot.child("description").getValue());
                    record.setClueKey(club_key);
                    expense_Records.add(record);
                    addExpenseRecordChangeListener(expense_Records);

                    All_expense_Records = expense_Records;
                    ExpenseDataListAdapter mAdapter = new ExpenseDataListAdapter(expense_Records, EarningDataActivity.this);
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

    public void setRevenueData() {
        final ArrayList<RevenueRecord> revenue_Records = new ArrayList<RevenueRecord>();
        mDatabase.child("RevenueRecord").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.e("RevenueRecord", ".....getChildrenCount....." + dataSnapshot.getChildrenCount());
                } else {
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
                    RevenueDataListAdapter mAdapter = new RevenueDataListAdapter(revenue_Records, EarningDataActivity.this);
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

    private void addExpenseRecordChangeListener(ArrayList<ExpenseRecord> expenseRecords) {
        double expenseTotal = 0;
        if (expenseRecords.size() != 0) {
            for (ExpenseRecord record : expenseRecords) {
                // check data is within the desired range
                expenseTotal += record.getAmount();
            }
            expenseTotalValue = expenseTotal;
        }
    }


    @Override
    protected void onResume() {
        Log.e("onResume", "=======onResume working==========");
        setRevenueData();
        setExpensesData();
        super.onResume();
    }
}
