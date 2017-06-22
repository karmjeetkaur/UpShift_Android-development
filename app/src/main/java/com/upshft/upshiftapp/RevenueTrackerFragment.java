package com.upshft.upshiftapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.upshft.upshiftapp.database.revenues.RevenueRecordDialogFragment;

public class RevenueTrackerFragment extends Fragment {
    public RevenueTrackerFragment() {
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = (Button)getActivity().findViewById(R.id.addRevButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RevenueRecordDialogFragment revenueRecordDialogFragment = new RevenueRecordDialogFragment();
                revenueRecordDialogFragment.show(getFragmentManager(), RevenueRecordDialogFragment.FRAGMENT_ID);
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}