package com.upshft.upshiftapp.database.revenues;

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

public class RevenueRecordListActivity extends Activity {
    private RevenueRecordDAO revenueRecordDAO;
    private ArrayList<RevenueRecord> revenueRecords;
    private ListView revenueRecordListView;
    private RevenueRecordArrayAdapter revenueRecordArrayAdapter;
    private GetRevenueRecordTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_revenue_record_list);

        this.revenueRecordDAO = new RevenueRecordDAO(this);
        this.revenueRecords = revenueRecordDAO.getRecords();

        revenueRecordListView = (ListView) findViewById(R.id.list_rev);
        revenueRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RevenueRecord revenueRecord = (RevenueRecord) parent.getItemAtPosition(position);

                if (revenueRecord != null) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RevenueRecordDialogFragment.ARGUMENT_SELECTED_RECORD, revenueRecord);

                    RevenueRecordDialogFragment revenueRecordDialogFragment = new RevenueRecordDialogFragment();
                    revenueRecordDialogFragment.setArguments(arguments);
                    revenueRecordDialogFragment.show(getFragmentManager(), RevenueRecordDialogFragment.FRAGMENT_ID);
                }
            }
        });

        revenueRecordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RevenueRecord revenueRecord = (RevenueRecord) parent.getItemAtPosition(position);

                // Use AsyncTask to delete from database
                revenueRecordDAO.delete(revenueRecord);

                // **** this is an issue and I cannot get the actual list view to update correctly after removing db entry
                // RevListAdapter.remove(revenue);
                updateView();

                return true;
            }
        });

        updateView();
    }

    public class GetRevenueRecordTask extends AsyncTask<Void, Void, ArrayList<RevenueRecord>> {
        private final WeakReference<Activity> activityWeakRef;

        public GetRevenueRecordTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<RevenueRecord> doInBackground(Void... arg0) {
            return revenueRecordDAO.getRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<RevenueRecord> revenueRecordList) {
            if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
                revenueRecords = revenueRecordList;
                if (revenueRecordList != null) {
                    revenueRecordArrayAdapter = new RevenueRecordArrayAdapter(RevenueRecordListActivity.this, revenueRecordList);
                    revenueRecordListView.setAdapter(revenueRecordArrayAdapter);
                    if (revenueRecordList.size() == 0) {
                        Toast.makeText(RevenueRecordListActivity.this,
                                "no records found",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void updateView()
    {
        task = new GetRevenueRecordTask(this);
        task.execute((Void) null);
    }
}
