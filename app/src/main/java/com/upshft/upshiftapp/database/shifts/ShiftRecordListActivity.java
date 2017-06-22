package com.upshft.upshiftapp.database.shifts;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.upshft.upshiftapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ShiftRecordListActivity extends Activity {
    private ShiftRecordDAO shiftRecordDAO;
    private ArrayList<ShiftRecord> shiftRecords;
    private ListView shiftRecordListView;
    private ShiftRecordArrayAdapter shiftRecordArrayAdapter;
    private GetShiftRecordTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shift_record_list);

        this.shiftRecordDAO = new ShiftRecordDAO(this);
        this.shiftRecords = shiftRecordDAO.getRecords();

        shiftRecordListView = (ListView) findViewById(R.id.shiftRecordListView);
        shiftRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShiftRecord shiftRecord = (ShiftRecord) parent.getItemAtPosition(position);

                if (shiftRecord != null) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(ShiftRecordDialogFragment.ARGUMENT_SELECTED_RECORD, shiftRecord);

                    ShiftRecordDialogFragment shiftRecordDialogFragment = new ShiftRecordDialogFragment();
                    shiftRecordDialogFragment.setArguments(arguments);
                    shiftRecordDialogFragment.show(getFragmentManager(), ShiftRecordDialogFragment.FRAGMENT_ID);
                }
            }
        });

        shiftRecordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShiftRecord shiftRecord = (ShiftRecord) parent.getItemAtPosition(position);

                // Use AsyncTask to delete from database
                shiftRecordDAO.delete(shiftRecord);

                // **** this is an issue and I cannot get the actual list view to update correctly after removing db entry
                // RevListAdapter.remove(revenue);
                updateView();

                return true;
            }
        });

        updateView();
    }

    public class GetShiftRecordTask extends AsyncTask<Void, Void, ArrayList<ShiftRecord>> {
        private final WeakReference<Activity> activityWeakRef;

        public GetShiftRecordTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<ShiftRecord> doInBackground(Void... arg0) {
            return shiftRecordDAO.getRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<ShiftRecord> shiftRecordList) {
            if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
                shiftRecords = shiftRecordList;
                if (shiftRecordList != null) {
                    shiftRecordArrayAdapter = new ShiftRecordArrayAdapter(ShiftRecordListActivity.this, shiftRecordList);
                    shiftRecordListView.setAdapter(shiftRecordArrayAdapter);
                    if (shiftRecordList.size() == 0) {
                        Toast.makeText(ShiftRecordListActivity.this,
                                "no records found",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void updateView()
    {
        task = new GetShiftRecordTask(this);
        task.execute((Void) null);
    }
}
