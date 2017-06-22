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

public class FeatureListAdapter extends RecyclerView.Adapter<FeatureListAdapter.Holder>
{
    ArrayList<String> stringArrayList;
    Activity m_activity;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position)
    {
        holder.list_item.setText("\u2022 " + stringArrayList.get(position));
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

    public FeatureListAdapter() {
    }

    public FeatureListAdapter(ArrayList<String> arrayList, Activity activity) {

        this.stringArrayList = arrayList;
        m_activity = activity;
    }

}
