package com.upshft.upshiftapp.database.revenues;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upshft.upshiftapp.R;

import java.util.List;

//RevListAdapter goes through each db entry and uses the data to populate each list item into the view list_item and repeats this until all entries have been read
public class RevenueRecordArrayAdapter extends ArrayAdapter<RevenueRecord> {
	private Context context;
	private static List<RevenueRecord> revenueRecords;

	public RevenueRecordArrayAdapter(Context context, List<RevenueRecord> revenueRecords) {
		super(context, R.layout.activity_revenue_record_list_item, revenueRecords);

		this.context = context;
		RevenueRecordArrayAdapter.revenueRecords = revenueRecords;
	}

	private class ViewHolder {
		TextView revenueIdTextView;
		TextView revenueDateTextView;
		TextView revenuePlatformTextView;
		TextView revenueAmountTextView;
		TextView revenueTipTextView;
	}

	@Override
	public int getCount() {
		return revenueRecords.size();
	}

	@Override
	public RevenueRecord getItem(int position) {
		return revenueRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	//	inflates view list_item to populate each value of db entry into list_item sections
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_revenue_record_list_item, null);

			holder = new ViewHolder();
			holder.revenueIdTextView = (TextView) convertView.findViewById(R.id.txt_rev_id);
			holder.revenueDateTextView = (TextView) convertView.findViewById(R.id.txt_date);
			holder.revenuePlatformTextView = (TextView) convertView.findViewById(R.id.txt_platform);
			holder.revenueAmountTextView = (TextView) convertView.findViewById(R.id.txt_amt);
			holder.revenueTipTextView = (TextView) convertView.findViewById(R.id.txt_tip);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RevenueRecord revenueRecord = (RevenueRecord) getItem(position);
		holder.revenueIdTextView.setText(String.valueOf(revenueRecord.getId()));
		holder.revenueDateTextView.setText(RevenueTable.COLUMN_DATE_FORMATTER.format(revenueRecord.getDate()));
		holder.revenuePlatformTextView.setText(revenueRecord.getPlatform());
		holder.revenueAmountTextView.setText(String.valueOf(revenueRecord.getAmount()));
		holder.revenueTipTextView.setText(String.valueOf(revenueRecord.getTip()));

		return convertView;
	}

	@Override
	public void add(RevenueRecord revenueRecord) {
		revenueRecords.add(revenueRecord);

		notifyDataSetChanged();

		super.add(revenueRecord);
	}

	@Override
	public void remove(RevenueRecord revenueRecord) {
		revenueRecords.remove(revenueRecord);

		notifyDataSetChanged();

		super.remove(revenueRecord);
	}
}
