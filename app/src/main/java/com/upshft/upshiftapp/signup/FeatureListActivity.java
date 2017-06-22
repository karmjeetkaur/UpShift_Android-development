package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.adapter.FeatureListAdapter;
import java.util.ArrayList;

public class FeatureListActivity extends AppCompatActivity
{
    RecyclerView feature_view;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    Button upgrade_button;
    TextView header;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_list);

        upgrade_button = (Button) findViewById(R.id.upgrade_button);
        header = (TextView) findViewById(R.id.header);
        feature_view = (RecyclerView) findViewById(R.id.feature_view);

        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        header.setText(title);
        Bundle extras = getIntent().getExtras();
        stringArrayList = (ArrayList<String>) extras.getSerializable("stringArrayList");

        if (title.equals("UpShift Starter")) {
            upgrade_button.setVisibility(View.GONE);
        } else if (title.equals("UpShift Lite")) {
            upgrade_button.setVisibility(View.VISIBLE);
        } else if (title.equals("UpShift Plus")) {
            upgrade_button.setVisibility(View.VISIBLE);
        } else if (title.equals("UpShift Pro")) {
            upgrade_button.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(FeatureListActivity.this, LinearLayoutManager.VERTICAL, false);
        feature_view.setLayoutManager(layoutManager);

        FeatureListAdapter mAdapter = new FeatureListAdapter(stringArrayList, FeatureListActivity.this);
        feature_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        upgrade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.equals("UpShift Starter")) {
                    tryLaunchApp("Upshift Rideshare Dash FREE", "com.upshiftapp");
                } else if (title.equals("UpShift Lite")) {
                    tryLaunchApp("Upshift RideShare Dash LITE", "com.upshft.upshiftapplite");
                } else if (title.equals("UpShift Plus")) {
                    tryLaunchApp("UpShift Plus", "com.upshft.upshiftapplite");
                } else if (title.equals("UpShift Pro")) {
                    tryLaunchApp("UpShift Pro", "com.upshft.upshiftapplite");
                }
            }
        });
    }

    private void tryLaunchApp(String appName, String packageName) {
        PackageManager manager = getPackageManager();
        try {
            Intent intentStartApp = manager.getLaunchIntentForPackage(packageName);
            if (intentStartApp != null) {
                intentStartApp.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intentStartApp);
                Toast.makeText(getApplicationContext(), "Switching to " + appName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), appName + " is not installed", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Please download it from the Play Store", Toast.LENGTH_SHORT).show();

                Intent intentLaunchPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                intentLaunchPlayStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(intentLaunchPlayStore);
            }
        } catch (Exception e) {
        }
    }
}
