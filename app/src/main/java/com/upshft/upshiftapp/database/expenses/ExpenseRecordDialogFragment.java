package com.upshft.upshiftapp.database.expenses;

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

public class ExpenseRecordDialogFragment extends DialogFragment {
    // UI references
    private EditText expCategoryEtxt;
    private EditText expAmountEtxt;
    private EditText expDescriptionEtxt;
    private EditText expDateEtxt;
    private Button addButton;
    private Button resetButton;
    private RadioGroup revPlatformGroup;
    private EditText CategoryTxt;

  //  private ExpenseRecordDAO expenseRecordDAO;
    private ExpenseRecord expenseRecord;
    private boolean isAddDialog;
    public static final String FRAGMENT_ID = "add_exp";
    public static final String ARGUMENT_SELECTED_RECORD = "selectedExpenseRecord";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    public ExpenseRecordDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
     //   expenseRecordDAO = new ExpenseRecordDAO(getActivity());

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
            expenseRecord = (ExpenseRecord) bundle.getParcelable(ARGUMENT_SELECTED_RECORD);
        }

        if (expenseRecord == null) {
            isAddDialog = true;
        } else {
            isAddDialog = false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.fragment_expense_record_dialog, null);
        builder.setView(dialogLayout);

        expCategoryEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_category);
        expAmountEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_exp_amt);
        expDescriptionEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_desc);
        expDateEtxt = (EditText) dialogLayout.findViewById(R.id.etxt_exp_date);

        resetButton = (Button) dialogLayout.findViewById(R.id.button_reset);

        revPlatformGroup = (RadioGroup) dialogLayout.findViewById(R.id.expCategoryGroup);

        CategoryTxt = (EditText) dialogLayout.findViewById(R.id.etxt_category);

        View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                // Check which radio button was clicked
                switch (view.getId()) {
                    case R.id.radio_food:
                        if (checked)
                            CategoryTxt.setText("Food");
                        break;
                    case R.id.radio_gas:
                        if (checked)
                            CategoryTxt.setText("Gas");
                        break;
                    case R.id.radio_miantenance:
                        if (checked)
                            CategoryTxt.setText("Maintenance");
                        break;
                    case R.id.radio_other:
                        if (checked)
                            CategoryTxt.setText("Other");
                        break;
                }
            }
        };

        RadioButton radio1 = (RadioButton) dialogLayout.findViewById(R.id.radio_food);
        radio1.setOnClickListener(radioListener);
        RadioButton radio2 = (RadioButton) dialogLayout.findViewById(R.id.radio_gas);
        radio2.setOnClickListener(radioListener);
        RadioButton radio3 = (RadioButton) dialogLayout.findViewById(R.id.radio_miantenance);
        radio3.setOnClickListener(radioListener);
        RadioButton radio4 = (RadioButton) dialogLayout.findViewById(R.id.radio_other);
        radio4.setOnClickListener(radioListener);


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

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (validationSuccess())
                    {
                        Date created_date = null;
                        String amount;
                        long date;
                        try {
                            expenseRecord.setDate(ExpenseTable.COLUMN_DATE_FORMATTER.parse(expDateEtxt.getText().toString()));
                            created_date = ExpenseTable.COLUMN_DATE_FORMATTER.parse(expDateEtxt.getText().toString());
                            date = created_date.getTime();
                        } catch (ParseException e) {
                            Toast.makeText(getActivity(), "Invalid date format!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        expenseRecord.setCategory(expCategoryEtxt.getText().toString());
                        try {
                            expenseRecord.setAmount(Double.parseDouble(expAmountEtxt.getText().toString()));
                            amount = expAmountEtxt.getText().toString();
                                    //Double.parseDouble(expAmountEtxt.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Invalid amount! Please enter a valid number.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        expenseRecord.setDescription(expDescriptionEtxt.getText().toString());


                        long result = 0;
                        if (isAddDialog) {
                          //  result = expenseRecordDAO.insert(expenseRecord);
                            ExpenseRecord item = new ExpenseRecord(date, expCategoryEtxt.getText().toString(), amount, expDescriptionEtxt.getText().toString());
                            mDatabase.child("ExpenseRecord").child(mUserId).child("items").push().setValue(item);

                        } else {
                        //    result = expenseRecordDAO.update(expenseRecord);
                            ExpenseRecord item = new ExpenseRecord(date, expCategoryEtxt.getText().toString(), amount, expDescriptionEtxt.getText().toString());
                            mDatabase.child("ExpenseRecord").child(mUserId).child("items").push().setValue(item);
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

    private void setUiValues() {
        if (expenseRecord != null) {
            expCategoryEtxt.setText(expenseRecord.getCategory());
            expAmountEtxt.setText(String.valueOf(expenseRecord.getAmount()));
            expDescriptionEtxt.setText(expenseRecord.getDescription());
            expDateEtxt.setText(ExpenseTable.COLUMN_DATE_FORMATTER.format(expenseRecord.getDate()));
            // TODO revPlatformGroup.check(R.id.radio_doordash);
        } else {
            expenseRecord = new ExpenseRecord();
            expDateEtxt.setText(ExpenseTable.COLUMN_DATE_FORMATTER.format(new Date()));
        }
    }

    protected void resetAllFields() {
        expCategoryEtxt.setText("");
        expAmountEtxt.setText("");
        expDescriptionEtxt.setText("");
    }

    //	pulls a new current date and sets it as a variable to be inserted as the date stamp for revenue tracking
    Date date = new Date();

    private void setRevenue() {
        expenseRecord = new ExpenseRecord();
        expenseRecord.setCategory(expCategoryEtxt.getText().toString());
        expenseRecord.setAmount(Double.parseDouble(expAmountEtxt.getText().toString()));
        expenseRecord.setDescription(expDescriptionEtxt.getText().toString());
        expenseRecord.setDate(date);
    }

    //	VALIDATION OF FORM FIELDS - JUST TO MAKE SURE ALL FIELDS HAVE SOMETHING ENTERED OR SELECTED
    private Boolean validationSuccess() {
        if (revPlatformGroup.getCheckedRadioButtonId() <= 0) {
            Toast.makeText(getActivity(), "Please select a category.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (expAmountEtxt.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter an expense amount or 0.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (expDescriptionEtxt.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter a description.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
