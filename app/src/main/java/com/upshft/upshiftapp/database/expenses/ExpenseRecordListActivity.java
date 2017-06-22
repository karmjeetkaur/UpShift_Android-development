package com.upshft.upshiftapp.database.expenses;

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

public class ExpenseRecordListActivity extends Activity {
    private ExpenseRecordDAO expenseRecordDAO;
    private ArrayList<ExpenseRecord> expenseRecords;
    private ListView expenseRecordListView;
    private ExpenseRecordArrayAdapter expenseRecordArrayAdapter;
    private GetRevenueRecordTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense_record_list);

        this.expenseRecordDAO = new ExpenseRecordDAO(this);
        this.expenseRecords = expenseRecordDAO.getRecords();

        expenseRecordListView = (ListView) findViewById(R.id.list_exp);
        expenseRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenseRecord expenseRecord = (ExpenseRecord) parent.getItemAtPosition(position);

                if (expenseRecord != null) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(ExpenseRecordDialogFragment.ARGUMENT_SELECTED_RECORD, expenseRecord);

                    ExpenseRecordDialogFragment expenseRecordDialogFragment = new ExpenseRecordDialogFragment();
                    expenseRecordDialogFragment.setArguments(arguments);
                    expenseRecordDialogFragment.show(getFragmentManager(), ExpenseRecordDialogFragment.FRAGMENT_ID);
                }
            }
        });

        expenseRecordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenseRecord expenseRecord = (ExpenseRecord) parent.getItemAtPosition(position);

                // Use AsyncTask to delete from database
                expenseRecordDAO.delete(expenseRecord);

                // **** this is an issue and I cannot get the actual list view to update correctly after removing db entry
                // RevListAdapter.remove(revenue);
                updateView();

                return true;
            }
        });

        updateView();
    }

    public class GetRevenueRecordTask extends AsyncTask<Void, Void, ArrayList<ExpenseRecord>> {
        private final WeakReference<Activity> activityWeakRef;

        public GetRevenueRecordTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<ExpenseRecord> doInBackground(Void... arg0) {
            return expenseRecordDAO.getRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<ExpenseRecord> expenseRecordList) {
            if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
                expenseRecords = expenseRecordList;
                if (expenseRecordList != null) {
                    expenseRecordArrayAdapter = new ExpenseRecordArrayAdapter(ExpenseRecordListActivity.this, expenseRecordList);
                    expenseRecordListView.setAdapter(expenseRecordArrayAdapter);
                    if (expenseRecordList.size() == 0) {
                        Toast.makeText(ExpenseRecordListActivity.this,
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
