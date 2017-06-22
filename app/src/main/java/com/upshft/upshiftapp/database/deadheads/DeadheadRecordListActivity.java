package com.upshft.upshiftapp.database.deadheads;

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

public class DeadheadRecordListActivity extends Activity
{
    private DeadheadRecordDAO deadheadRecordDAO;
    private ArrayList<DeadheadRecord> deadheadRecords;
    private ListView deadheadRecordListView;
    private DeadheadRecordArrayAdapter deadheadRecordArrayAdapter;
    private GetDeadheadRecordTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deadhead_record_list);

        this.deadheadRecordDAO = new DeadheadRecordDAO(this);
        this.deadheadRecords = deadheadRecordDAO.getRecords();

        deadheadRecordListView = (ListView) findViewById(R.id.deadheadRecordListView);
        deadheadRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeadheadRecord deadheadRecord = (DeadheadRecord) parent.getItemAtPosition(position);

                if (deadheadRecord != null) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(DeadheadRecordDialogFragment.ARGUMENT_SELECTED_RECORD, deadheadRecord);

                    DeadheadRecordDialogFragment deadheadRecorDialogFragment = new DeadheadRecordDialogFragment();
                    deadheadRecorDialogFragment.setArguments(arguments);
                    deadheadRecorDialogFragment.show(getFragmentManager(), DeadheadRecordDialogFragment.FRAGMENT_ID);
                }
            }
        });

        deadheadRecordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DeadheadRecord deadheadRecord = (DeadheadRecord) parent.getItemAtPosition(position);

                // Use AsyncTask to delete from database
                deadheadRecordDAO.delete(deadheadRecord);

                // **** this is an issue and I cannot get the actual list view to update correctly after removing db entry
                // RevListAdapter.remove(revenue);
                updateView();

                return true;
            }
        });

        updateView();
    }

    public class GetDeadheadRecordTask extends AsyncTask<Void, Void, ArrayList<DeadheadRecord>> {
        private final WeakReference<Activity> activityWeakRef;

        public GetDeadheadRecordTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<DeadheadRecord> doInBackground(Void... arg0) {
            return deadheadRecordDAO.getRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<DeadheadRecord> deadheadRecordList) {
            if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
                deadheadRecords = deadheadRecordList;
                if (deadheadRecordList != null) {
                    deadheadRecordArrayAdapter = new DeadheadRecordArrayAdapter(DeadheadRecordListActivity.this, deadheadRecordList);
                    deadheadRecordListView.setAdapter(deadheadRecordArrayAdapter);
                    if (deadheadRecordList.size() == 0) {
                        Toast.makeText(DeadheadRecordListActivity.this,
                                "no records found",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void updateView()
    {
        task = new GetDeadheadRecordTask(this);
        task.execute((Void) null);
    }
}
