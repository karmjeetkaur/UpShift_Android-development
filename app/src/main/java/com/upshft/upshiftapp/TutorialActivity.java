package com.upshft.upshiftapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.viewpagerindicator.CirclePageIndicator;
/**
 * Created by adamwhitlock on 4/22/16.
 */
public class TutorialActivity extends AppCompatActivity {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_tutorial);
        // Assign buttons behavior

        Button login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("pref_first_run", false).commit();
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        //Set the pager with an adapter
        mPager = (ViewPager) findViewById(R.id.tutorial_pager);
        mPager.setOffscreenPageLimit(3); // Helps to keep fragment alive, otherwise I will have to load again images
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        titleIndicator.setViewPager(mPager);
    }
}
