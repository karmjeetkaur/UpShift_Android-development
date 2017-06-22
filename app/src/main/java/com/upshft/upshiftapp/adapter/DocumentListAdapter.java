package com.upshft.upshiftapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.signup.ScreenShotActivity;
import java.util.ArrayList;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.Holder> {
    ArrayList<String> stringArrayList;
    Activity m_activity;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.couponlistitem, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        holder.list_item.setText(stringArrayList.get(position));
        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_activity, ScreenShotActivity.class);
                intent.putExtra("Doc_Value", stringArrayList.get(position));
                m_activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView list_item;

        public Holder(View itemView) {
            super(itemView);
            list_item = (TextView) itemView.findViewById(R.id.list_item_value);
        }
    }

    public DocumentListAdapter() {
    }

    public DocumentListAdapter(ArrayList<String> arrayList, Activity activity) {

        this.stringArrayList = arrayList;
        m_activity = activity;
    }

}
