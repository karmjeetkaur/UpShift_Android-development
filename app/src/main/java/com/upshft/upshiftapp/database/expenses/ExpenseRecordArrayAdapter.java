package com.upshft.upshiftapp.database.expenses;

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
public class ExpenseRecordArrayAdapter extends ArrayAdapter<ExpenseRecord> {
	private Context context;
	private static List<ExpenseRecord> expenseRecords;

	public ExpenseRecordArrayAdapter(Context context, List<ExpenseRecord> expenseRecords) {
		super(context, R.layout.activity_expense_record_list_item, expenseRecords);

		this.context = context;
		ExpenseRecordArrayAdapter.expenseRecords = expenseRecords;
	}

	private class ViewHolder {
		TextView expenseIdTextView;
		TextView expenseDateTextView;
		TextView expenseCategoryTextView;
		TextView expenseAmountTextView;
		TextView expenseDescriptionTextView;
	}

	@Override
	public int getCount() {
		return expenseRecords.size();
	}

	@Override
	public ExpenseRecord getItem(int position) {
		return expenseRecords.get(position);
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
			convertView = inflater.inflate(R.layout.activity_expense_record_list_item, null);

			holder = new ViewHolder();
			holder.expenseIdTextView = (TextView) convertView.findViewById(R.id.txt_exp_id);
			holder.expenseDateTextView = (TextView) convertView.findViewById(R.id.txt_exp_date);
			holder.expenseCategoryTextView = (TextView) convertView.findViewById(R.id.txt_category);
			holder.expenseAmountTextView = (TextView) convertView.findViewById(R.id.txt_exp_amt);
			holder.expenseDescriptionTextView = (TextView) convertView.findViewById(R.id.txt_desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ExpenseRecord expenseRecord = (ExpenseRecord) getItem(position);
		holder.expenseIdTextView.setText(String.valueOf(expenseRecord.getId()));
		holder.expenseDateTextView.setText(ExpenseTable.COLUMN_DATE_FORMATTER.format(expenseRecord.getDate()));
		holder.expenseCategoryTextView.setText(expenseRecord.getCategory());
		holder.expenseAmountTextView.setText(String.valueOf(expenseRecord.getAmount()));
		holder.expenseDescriptionTextView.setText(expenseRecord.getDescription());

		return convertView;
	}

	@Override
	public void add(ExpenseRecord expenseRecord) {
		expenseRecords.add(expenseRecord);

		notifyDataSetChanged();

		super.add(expenseRecord);
	}

	@Override
	public void remove(ExpenseRecord expenseRecord) {
		expenseRecords.remove(expenseRecord);

		notifyDataSetChanged();

		super.remove(expenseRecord);
	}
}
