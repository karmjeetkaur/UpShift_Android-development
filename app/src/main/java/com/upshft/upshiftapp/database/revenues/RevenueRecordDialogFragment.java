package com.upshft.upshiftapp.database.revenues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.DashboardActivity;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.SummaryFragment;

import java.text.ParseException;
import java.util.Date;

public class RevenueRecordDialogFragment extends DialogFragment {
    // UI references
    private EditText revPlatformEtxt;
    private EditText revAmtEtxt;
    private EditText revTipEtxt;
    private EditText revDateEtxt;
    private Button addButton;
    private Button resetButton;
    private RadioGroup revPlatformGroup;
    private EditText platformTxt;

//    private RevenueRecordDAO revenueRecordDAO;
    private RevenueRecord revenueRecord;

    private boolean isAddDialog;

    public static final String FRAGMENT_ID = "add_rev";
    public static final String ARGUMENT_SELECTED_RECORD = "selectedRevenueRecord";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    public RevenueRecordDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      //  revenueRecordDAO = new RevenueRecordDAO(getActivity());

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            revenueRecord = (RevenueRecord) bundle.getParcelable(ARGUMENT_SELECTED_RECORD);
        }

        if (revenueRecord == null) {
            isAddDialog = true;
        } else {
            isAddDialog = false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.fragment_revenue_record_dialog, null);
        builder.setView(dialogLayout);

        revPlatformEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_platform);
        revAmtEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_amt);
        revTipEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_tip);
        revDateEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_date);
        resetButton = (Button) dialogLayout.findViewById(R.id.button_reset);
        revPlatformGroup = (RadioGroup) dialogLayout.findViewById(R.id.revPlatformGroup);
        platformTxt = (EditText) dialogLayout.findViewById(R.id.etxt_platform);

        View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                // Check which radio button was clicked
                switch (view.getId()) {
                    case R.id.radio_uber:
                        if (checked)
                            platformTxt.setText("Uber");
                        break;
                    case R.id.radio_lyft:
                        if (checked)
                            platformTxt.setText("Lyft");
                        break;
                    case R.id.radio_doordash:
                        if (checked)
                            platformTxt.setText("Door Dash");
                        break;
                    case R.id.radio_postmates:
                        if (checked)
                            platformTxt.setText("Postmates");
                        break;
                    case R.id.radio_curb:
                        if (checked)
                            platformTxt.setText("Roadie");
                        break;
                    case R.id.radio_wingz:
                        if (checked)
                            platformTxt.setText("Wingz");
                        break;
                    case R.id.radio_orderup:
                        if (checked)
                            platformTxt.setText("Rideshare");
                        break;
                    case R.id.radio_uzurv:
                        if (checked)
                            platformTxt.setText("Uzurv");
                        break;
                    case R.id.radio_fare:
                        if (checked)
                            platformTxt.setText("Fare");
                        break;
                }
            }
        };

        RadioButton radio1 = (RadioButton) dialogLayout.findViewById(R.id.radio_uber);
        radio1.setOnClickListener(radioListener);
        RadioButton radio2 = (RadioButton) dialogLayout.findViewById(R.id.radio_lyft);
        radio2.setOnClickListener(radioListener);
        RadioButton radio3 = (RadioButton) dialogLayout.findViewById(R.id.radio_doordash);
        radio3.setOnClickListener(radioListener);
        RadioButton radio4 = (RadioButton) dialogLayout.findViewById(R.id.radio_postmates);
        radio4.setOnClickListener(radioListener);
        RadioButton radio5 = (RadioButton) dialogLayout.findViewById(R.id.radio_curb);
        radio5.setOnClickListener(radioListener);
        RadioButton radio6 = (RadioButton) dialogLayout.findViewById(R.id.radio_wingz);
        radio6.setOnClickListener(radioListener);
        RadioButton radio7 = (RadioButton) dialogLayout.findViewById(R.id.radio_orderup);
        radio7.setOnClickListener(radioListener);
        RadioButton radio8 = (RadioButton) dialogLayout.findViewById(R.id.radio_uzurv);
        radio8.setOnClickListener(radioListener);
        RadioButton radio9 = (RadioButton) dialogLayout.findViewById(R.id.radio_fare);
        radio9.setOnClickListener(radioListener);

        resetButton = (Button) dialogLayout.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAllFields();
            }
        });

        setUiValues();

        builder.setTitle(isAddDialog ? "Add" : "Update");
        builder.setCancelable(false);
        builder.setPositiveButton(isAddDialog ? "Add" : "Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void setUiValues()
    {
        if (revenueRecord != null) {
            revPlatformEtxt.setText(revenueRecord.getPlatform());
            revAmtEtxt.setText(String.valueOf(revenueRecord.getAmount()));
            revTipEtxt.setText(String.valueOf(revenueRecord.getTip()));
            revDateEtxt.setText(RevenueTable.COLUMN_DATE_FORMATTER.format(revenueRecord.getDate()));
            // TODO revPlatformGroup.check(R.id.radio_doordash);
        } else {
            revenueRecord = new RevenueRecord();
            revDateEtxt.setText(RevenueTable.COLUMN_DATE_FORMATTER.format(new Date()));
        }
    }

    protected void resetAllFields() {
        revPlatformEtxt.setText("");
        revAmtEtxt.setText("");
        revTipEtxt.setText("");

    }

    //	pulls a new current date and sets it as a variable to be inserted as the date stamp for revenue tracking
    Date date = new Date();

    private void setRevenue() {
        revenueRecord = new RevenueRecord();
        revenueRecord.setPlatform(revPlatformEtxt.getText().toString());
        revenueRecord.setAmount(Double.parseDouble(revAmtEtxt.getText().toString()));
        revenueRecord.setTip(Double.parseDouble(revTipEtxt.getText().toString()));
        revenueRecord.setDate(date);
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validationSuccess()) {
                        Date created_date = null;
                        long date;
                        String tip;
                        String amount;
                        try {
                            created_date = RevenueTable.COLUMN_DATE_FORMATTER.parse(revDateEtxt.getText().toString());
                            date = created_date.getTime();
                            revenueRecord.setDate(RevenueTable.COLUMN_DATE_FORMATTER.parse(revDateEtxt.getText().toString()));
                        } catch (ParseException e) {
                            Toast.makeText(getActivity(), "Invalid date format!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        revenueRecord.setPlatform(revPlatformEtxt.getText().toString());

                        try {
                            revenueRecord.setAmount(Double.parseDouble(revAmtEtxt.getText().toString()));
                            amount = revAmtEtxt.getText().toString();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Invalid amount! Please enter a valid number.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            revenueRecord.setTip(Double.parseDouble(revTipEtxt.getText().toString()));
                            tip = revTipEtxt.getText().toString();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Invalid tip! Please enter a valid number.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        long result = 0;
                        if (isAddDialog) {
                           // result = revenueRecordDAO.insert(revenueRecord);
                            RevenueRecord item = new RevenueRecord(date, revPlatformEtxt.getText().toString(), amount, tip);
                            mDatabase.child("RevenueRecord").child(mUserId).child("items").push().setValue(item);
                        } else {
                         //   result = revenueRecordDAO.update(revenueRecord);
                            RevenueRecord item = new RevenueRecord(date, revPlatformEtxt.getText().toString(), amount, tip);
                            mDatabase.child("RevenueRecord").child(mUserId).child("items").push().setValue(item);
                        }

                        if (result >= 0) {
                            // update successful
                            SummaryFragment summaryFragment = (SummaryFragment) getFragmentManager().findFragmentByTag(DashboardActivity.SummaryFragmentTag);
                            summaryFragment.updateSummaries("");
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Unable to " + (isAddDialog ? "add" : "update") + " record", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    //	VALIDATION OF FORM FIELDS - JUST TO MAKE SURE ALL FIELDS HAVE SOMETHING ENTERED OR SELECTED
    private Boolean validationSuccess() {
        if (revPlatformGroup.getCheckedRadioButtonId() <= 0) {
            Toast.makeText(getActivity(), "Please select a platform.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (revAmtEtxt.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter a revenue amount or 0.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (revTipEtxt.getText().toString().equalsIgnoreCase("")) {
            revTipEtxt.setText("0");
        }

        return true;
    }
}
