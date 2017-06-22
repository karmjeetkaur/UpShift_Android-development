package com.upshft.upshiftapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import com.upshft.upshiftapp.signup.EditEarningData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by new on 5/13/2016.
 */
public class RevenueDataListAdapter extends RecyclerView.Adapter<RevenueDataListAdapter.MyViewHolder>
{
    ArrayList<RevenueRecord> revenueRecords;
    Activity mactivity;
    String inputPattern = "yyyy-MM-dd HH:mm:ss";
    String outputPattern = "dd-MMM-yyyy";
    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txt_platform_name.setText("Platform Name: " + revenueRecords.get(position).getPlatform());
        holder.txt_amount.setText("Amount: " + revenueRecords.get(position).getAmount());
        holder.txt_tip.setText("Tip: " + revenueRecords.get(position).getTip());

        Date date =  revenueRecords.get(position).getDate();
        String str = outputFormat.format(date);
        holder.txt_date.setText("Date: " + str);


        holder.editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String club_key = revenueRecords.get(position).getClueKey();
                String platform = revenueRecords.get(position).getPlatform();
                String amount = "" + revenueRecords.get(position).getAmount();
                String tip = "" + revenueRecords.get(position).getTip();
                Date date =  revenueRecords.get(position).getDate();
                String date_str = outputFormat.format(date);

                Intent intent = new Intent(mactivity, EditEarningData.class);
                intent.putExtra("platform", platform);
                intent.putExtra("amount", amount);
                intent.putExtra("tip", tip);
                intent.putExtra("club_key", club_key);
                intent.putExtra("date_str",date_str);
                mactivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return revenueRecords.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_platform_name;
        TextView txt_amount,txt_tip;
        TextView txt_date;
        Button editButton;

        public MyViewHolder(View view)
        {
            super(view);
            txt_platform_name = (TextView) view.findViewById(R.id.txt_platform_name);
            txt_amount = (TextView) view.findViewById(R.id.txt_amount);
            txt_tip = (TextView) view.findViewById(R.id.txt_tip);
            txt_date = (TextView) view.findViewById(R.id.txt_date);
            editButton = (Button) view.findViewById(R.id.editButton);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public RevenueDataListAdapter(ArrayList<RevenueRecord> arrayList, Activity activity)
    {
        this.revenueRecords = arrayList;
        this.mactivity = activity;
    }

    public RevenueDataListAdapter() {
    }

}
