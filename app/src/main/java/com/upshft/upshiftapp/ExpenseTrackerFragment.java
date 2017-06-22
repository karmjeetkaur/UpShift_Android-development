package com.upshft.upshiftapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.upshft.upshiftapp.database.expenses.ExpenseRecordDialogFragment;

public class ExpenseTrackerFragment extends Fragment {
    public ExpenseTrackerFragment() {
        // required empty default constructor
    }

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

        Button button = (Button)getActivity().findViewById(R.id.expensesButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseRecordDialogFragment expenseRecordDialogFragment = new ExpenseRecordDialogFragment();
                expenseRecordDialogFragment.show(getFragmentManager(), ExpenseRecordDialogFragment.FRAGMENT_ID);
            }
        });
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
}