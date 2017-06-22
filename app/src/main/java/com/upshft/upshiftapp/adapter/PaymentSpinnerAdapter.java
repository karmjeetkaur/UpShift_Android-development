package com.upshft.upshiftapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upshft.upshiftapp.R;

import java.util.ArrayList;

public class PaymentSpinnerAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private ArrayList<String> data;
    public Resources res;
    LayoutInflater inflater;


    public PaymentSpinnerAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<String> paymentTypeArrayList, Resources resLocal) {
        super(activitySpinner, textViewResourceId, paymentTypeArrayList);

        activity = activitySpinner;
        data = paymentTypeArrayList;
        res = resLocal;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        View row = inflater.inflate(R.layout.spinner_rows, parent, false);
        TextView txt_payment_value = (TextView) row.findViewById(R.id.txt_payment_value);

        if (position == 0) {
            // Default selected Spinner item
            txt_payment_value.setText("Please select Payment");
        } else {
            txt_payment_value.setText(data.get(position));
        }
        return row;
    }
}