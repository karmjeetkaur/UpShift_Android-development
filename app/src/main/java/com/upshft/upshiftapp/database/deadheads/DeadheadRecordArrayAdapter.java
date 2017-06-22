package com.upshft.upshiftapp.database.deadheads;

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
public class DeadheadRecordArrayAdapter extends ArrayAdapter<DeadheadRecord>
{
	private Context context;
	private static List<DeadheadRecord> deadheadRecords;

	public DeadheadRecordArrayAdapter(Context context, List<DeadheadRecord> deadheadRecords) {
		super(context, R.layout.activity_deadhead_record_list, deadheadRecords);

		this.context = context;
		DeadheadRecordArrayAdapter.deadheadRecords = deadheadRecords;
	}

	private class ViewHolder {
		TextView deadheadIdTextView;
		TextView deadheadStartTextView;
		TextView deadheadEndTextView;
		TextView deadheadDistanceTextView;
	}

	@Override
	public int getCount() {
		return deadheadRecords.size();
	}

	@Override
	public DeadheadRecord getItem(int position) {
		return deadheadRecords.get(position);
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
			convertView = inflater.inflate(R.layout.activity_deadhead_record_list_item, null);

			holder = new ViewHolder();
			holder.deadheadIdTextView = (TextView) convertView.findViewById(R.id.deadheadRecordIdTextView);
			holder.deadheadStartTextView = (TextView) convertView.findViewById(R.id.deadheadRecordStartTextView);
			holder.deadheadEndTextView = (TextView) convertView.findViewById(R.id.deadheadRecordEndTextView);
			holder.deadheadDistanceTextView = (TextView) convertView.findViewById(R.id.deadheadRecordDistanceTextView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DeadheadRecord deadheadRecord = (DeadheadRecord) getItem(position);
		holder.deadheadIdTextView.setText(String.valueOf(deadheadRecord.getId()));
		holder.deadheadStartTextView.setText(DeadheadTable.COLUMN_START_FORMATTER.format(deadheadRecord.getStart()));
		holder.deadheadEndTextView.setText(DeadheadTable.COLUMN_END_FORMATTER.format(deadheadRecord.getEnd()));
		holder.deadheadDistanceTextView.setText(String.valueOf(deadheadRecord.getDistance()));

		return convertView;
	}

	@Override
	public void add(DeadheadRecord deadheadRecord) {
		deadheadRecords.add(deadheadRecord);

		notifyDataSetChanged();

		super.add(deadheadRecord);
	}

	@Override
	public void remove(DeadheadRecord deadheadRecord) {
		deadheadRecords.remove(deadheadRecord);

		notifyDataSetChanged();

		super.remove(deadheadRecord);
	}
}
