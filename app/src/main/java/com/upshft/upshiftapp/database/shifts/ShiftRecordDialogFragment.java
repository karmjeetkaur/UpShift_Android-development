package com.upshft.upshiftapp.database.shifts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.upshft.upshiftapp.R;

import java.text.ParseException;

public class ShiftRecordDialogFragment extends DialogFragment {
	// UI references
	private EditText shiftRecordStartEditText;
	private EditText shiftRecordEndEditText;
	private ShiftRecord shiftRecord;
	private ShiftRecordDAO shiftRecordDAO;
	public static final String ARGUMENT_SELECTED_RECORD = "seletedShiftRecord";
	public static final String FRAGMENT_ID = "shift_record_dialog_fragment";

	public ShiftRecordDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		shiftRecordDAO = new ShiftRecordDAO(getActivity());

		Bundle bundle = getArguments();
		shiftRecord = (ShiftRecord)bundle.getParcelable(ARGUMENT_SELECTED_RECORD);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogLayout = inflater.inflate(R.layout.fragment_shift_record_dialog, null);
		builder.setView(dialogLayout);

		shiftRecordStartEditText = (EditText) dialogLayout.findViewById(R.id.shiftRecordStartEditText);
		shiftRecordEndEditText = (EditText) dialogLayout.findViewById(R.id.shiftRecordEndEditText);

		setUiValues();

		builder.setTitle(R.string.update);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.update,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							shiftRecord.setStart(ShiftTable.COLUMN_START_FORMATTER.parse(shiftRecordStartEditText.getText().toString()));
						} catch (ParseException e) {
							Toast.makeText(getActivity(),
									"Invalid date format!",
									Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							shiftRecord.setEnd(ShiftTable.COLUMN_END_FORMATTER.parse(shiftRecordEndEditText.getText().toString()));
						} catch (ParseException e) {
							Toast.makeText(getActivity(),
									"Invalid date format!",
									Toast.LENGTH_SHORT).show();
							return;
						}

						long result = shiftRecordDAO.update(shiftRecord);
						if (result > 0) {
							// update successful
							// update ListActivity list view probably via onSaveInstanceState of ListActivity
							// also update DashboardActivity totals probably via onSaveInstanceState of DashboardActivity
						} else {
							Toast.makeText(getActivity(),
									"Unable to update record",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		return builder.create();
	}


	private void setUiValues() {
		if (shiftRecord != null) {
			shiftRecordStartEditText.setText(ShiftTable.COLUMN_START_FORMATTER.format(shiftRecord.getStart()));
			shiftRecordEndEditText.setText(ShiftTable.COLUMN_END_FORMATTER.format(shiftRecord.getEnd()));
		}
	}
}