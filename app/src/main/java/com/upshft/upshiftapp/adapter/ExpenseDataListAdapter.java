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
import com.upshft.upshiftapp.database.expenses.ExpenseRecord;
import com.upshft.upshiftapp.signup.EditExpenseData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * Created by new on 5/13/2016.
 */
public class ExpenseDataListAdapter extends RecyclerView.Adapter<ExpenseDataListAdapter.MyViewHolder>
{
    ArrayList<ExpenseRecord> expenseRecords;
    Activity mactivity;
    String inputPattern = "yyyy-MM-dd HH:mm:ss";
    String outputPattern = "dd-MMM-yyyy";
    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenselist_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        holder.txt_platform_name.setText("Category Name: " + expenseRecords.get(position).getCategory());
        holder.txt_amount.setText("Amount: " + expenseRecords.get(position).getAmount());
        holder.txt_desc.setText("Desc: " + expenseRecords.get(position).getDescription());

        Date date = expenseRecords.get(position).getDate();
        String str = outputFormat.format(date);
        holder.txt_date.setText("Date: " + str);

        holder.editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String club_key = expenseRecords.get(position).getClueKey();
                String category = expenseRecords.get(position).getCategory();
                String amount = "" + expenseRecords.get(position).getAmount();
                String desc = "" + expenseRecords.get(position).getDescription();
                Date date = expenseRecords.get(position).getDate();
                String date_str = outputFormat.format(date);

                Intent intent = new Intent(mactivity, EditExpenseData.class);
                intent.putExtra("category", category);
                intent.putExtra("amount", amount);
                intent.putExtra("desc", desc);
                intent.putExtra("club_key", club_key);
                intent.putExtra("date_str", date_str);
                mactivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseRecords.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_platform_name;
        TextView txt_amount, txt_desc;
        TextView txt_date;
        Button editButton;

        public MyViewHolder(View view) {
            super(view);
            txt_platform_name = (TextView) view.findViewById(R.id.txt_platform_name);
            txt_amount = (TextView) view.findViewById(R.id.txt_amount);
            txt_desc = (TextView) view.findViewById(R.id.txt_desc);
            txt_date = (TextView) view.findViewById(R.id.txt_date);
            editButton = (Button) view.findViewById(R.id.editButton);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public ExpenseDataListAdapter(ArrayList<ExpenseRecord> arrayList, Activity activity)
    {
        this.expenseRecords = arrayList;
        this.mactivity = activity;
    }

    public ExpenseDataListAdapter()
    {
    }

}
