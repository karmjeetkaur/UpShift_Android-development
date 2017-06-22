package com.upshft.upshiftapp.database.deadheads;

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

public class DeadheadRecordDialogFragment extends DialogFragment
{
	// UI references
	private EditText deadheadRecordStartEditText;
	private EditText deadheadRecordEndEditText;
	private EditText deadheadRecordDistanceEditText;
	private DeadheadRecord deadheadRecord;
	private DeadheadRecordDAO deadheadRecordDAO;
	public static final String ARGUMENT_SELECTED_RECORD = "selectedDeadheadRecord";
	public static final String FRAGMENT_ID = "deadhead_record_dialog_fragment";

	public DeadheadRecordDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		deadheadRecordDAO = new DeadheadRecordDAO(getActivity());

		Bundle bundle = getArguments();
		deadheadRecord = (DeadheadRecord)bundle.getParcelable(ARGUMENT_SELECTED_RECORD);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogLayout = inflater.inflate(R.layout.fragment_deadhead_record_dialog, null);
		builder.setView(dialogLayout);

		deadheadRecordStartEditText = (EditText) dialogLayout.findViewById(R.id.deadheadRecordStartEditText);
		deadheadRecordEndEditText = (EditText) dialogLayout.findViewById(R.id.deadheadRecordEndEditText);
		deadheadRecordDistanceEditText = (EditText) dialogLayout.findViewById(R.id.deadheadRecordDistanceEditView);

		setUiValues();

		builder.setTitle(R.string.update);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.update,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							deadheadRecord.setStart(DeadheadTable.COLUMN_START_FORMATTER.parse(deadheadRecordStartEditText.getText().toString()));
						} catch (ParseException e) {
							Toast.makeText(getActivity(),
									"Invalid date format!",
									Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							deadheadRecord.setEnd(DeadheadTable.COLUMN_END_FORMATTER.parse(deadheadRecordEndEditText.getText().toString()));
						} catch (ParseException e) {
							Toast.makeText(getActivity(),
									"Invalid date format!",
									Toast.LENGTH_SHORT).show();
							return;
						}
						try
						{
							deadheadRecord.setDistance(Double.parseDouble(deadheadRecordDistanceEditText.getText().toString()));
						}
						catch(NumberFormatException e)
						{
							Toast.makeText(getActivity(),
									"Invalid distance format!",
									Toast.LENGTH_SHORT).show();
							return;
						}

						long result = deadheadRecordDAO.update(deadheadRecord);
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
		if (deadheadRecord != null) {
			deadheadRecordStartEditText.setText(DeadheadTable.COLUMN_START_FORMATTER.format(deadheadRecord.getStart()));
			deadheadRecordEndEditText.setText(DeadheadTable.COLUMN_END_FORMATTER.format(deadheadRecord.getEnd()));
			deadheadRecordDistanceEditText.setText(String.valueOf(deadheadRecord.getDistance()));
		}
	}
}