package grid;

import java.util.ArrayList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import com.upshft.upshiftapp.R;

public class IconListPreferenceScreen extends ListPreference
{
    CustomListPreferenceAdapter customListPreferenceAdapter = null;
    Context mContext;
    private LayoutInflater mInflater;
    CharSequence[] entries;
    CharSequence[] entryValues;
    ArrayList<RadioButton> rButtonList;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public IconListPreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        rButtonList = new ArrayList<RadioButton>();
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = prefs.edit();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        customListPreferenceAdapter = new CustomListPreferenceAdapter(mContext);

        builder.setAdapter(customListPreferenceAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private class CustomListPreferenceAdapter extends BaseAdapter {
        public CustomListPreferenceAdapter(Context context) {

        }

        public int getCount() {
            return entries.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CustomHolder holder = null;

            if (row == null) {
                row = mInflater.inflate(R.layout.custom_list_preference_row, parent, false);
                holder = new CustomHolder(row, position);
                row.setTag(holder);

                // do whatever you need here, for me I wanted the last item to be greyed out and unclickable
                if (position != 3) {
                    row.setClickable(true);
                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            for (RadioButton rb : rButtonList) {
                                if (rb.getId() != position)
                                    rb.setChecked(false);
                            }

                            int index = position;
                            int value = Integer.valueOf((String) entryValues[index]);
                            editor.putInt("yourPref", value);

                            Dialog mDialog = getDialog();
                            mDialog.dismiss();
                        }
                    });
                }
            }
            return row;
        }

        class CustomHolder {
            private TextView text = null;
            private RadioButton rButton = null;

            CustomHolder(View row, int position) {
                text = (TextView) row.findViewById(R.id.custom_list_view_row_text_view);
                text.setText(entries[position]);
                rButton = (RadioButton) row.findViewById(R.id.custom_list_view_row_radio_button);
                rButton.setId(position);

                // again do whatever you need to, for me I wanted this item to be greyed out and unclickable
                if (position == 3) {
                    text.setTextColor(Color.LTGRAY);
                    rButton.setClickable(false);
                }

                // also need to do something to check your preference and set the right button as checked

                rButtonList.add(rButton);
                rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            for (RadioButton rb : rButtonList) {
                                if (rb != buttonView)
                                    rb.setChecked(false);
                            }

                            int index = buttonView.getId();
                            int value = Integer.valueOf((String) entryValues[index]);
                            editor.putInt("yourPref", value);

                            Dialog mDialog = getDialog();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}