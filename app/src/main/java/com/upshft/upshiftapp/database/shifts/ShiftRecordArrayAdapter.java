package com.upshft.upshiftapp.database.shifts;

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
public class ShiftRecordArrayAdapter extends ArrayAdapter<ShiftRecord> {
	private Context context;
	private static List<ShiftRecord> shiftRecords;

	public ShiftRecordArrayAdapter(Context context, List<ShiftRecord> shiftRecords) {
		super(context, R.layout.activity_shift_record_list_item, shiftRecords);

		this.context = context;
		ShiftRecordArrayAdapter.shiftRecords = shiftRecords;
	}

	private class ViewHolder {
		TextView shiftIdTextView;
		TextView shiftStartTextView;
		TextView shiftEndTextView;
	}

	@Override
	public int getCount() {
		return shiftRecords.size();
	}

	@Override
	public ShiftRecord getItem(int position) {
		return shiftRecords.get(position);
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
			convertView = inflater.inflate(R.layout.activity_shift_record_list_item, null);

			holder = new ViewHolder();
			holder.shiftIdTextView = (TextView) convertView.findViewById(R.id.shiftRecordIdTextView);
			holder.shiftStartTextView = (TextView) convertView.findViewById(R.id.shiftRecordStartTextView);
			holder.shiftEndTextView = (TextView) convertView.findViewById(R.id.shiftRecordEndTextView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ShiftRecord shiftRecord = (ShiftRecord) getItem(position);
		holder.shiftIdTextView.setText(String.valueOf(shiftRecord.getId()));
		holder.shiftStartTextView.setText(ShiftTable.COLUMN_START_FORMATTER.format(shiftRecord.getStart()));
		holder.shiftEndTextView.setText(ShiftTable.COLUMN_END_FORMATTER.format(shiftRecord.getEnd()));

		return convertView;
	}

	@Override
	public void add(ShiftRecord shiftRecord) {
		shiftRecords.add(shiftRecord);

		notifyDataSetChanged();

		super.add(shiftRecord);
	}

	@Override
	public void remove(ShiftRecord shiftRecord) {
		shiftRecords.remove(shiftRecord);

		notifyDataSetChanged();

		super.remove(shiftRecord);
	}
}
