package com.upshft.upshiftapp.signup;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.upshft.upshiftapp.R;

public class CouponListActivity extends AppCompatActivity
{
    AdView adView1,adView2,adView3,adView4,adView5,adView6;
    private ProgressBar pbHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);

        pbHeaderProgress = (ProgressBar)findViewById(R.id.pbHeaderProgress);


        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                pbHeaderProgress.setVisibility(View.GONE);
              //  handler.postDelayed(CouponListActivity.this, 1000);
            }
        };
        handler.postDelayed(r, 1000);

        adView1 = (AdView) findViewById(R.id.adView1);
        adView2 = (AdView) findViewById(R.id.adView2);
        adView3 = (AdView) findViewById(R.id.adView3);
        adView4 = (AdView) findViewById(R.id.adView4);
        adView5 = (AdView) findViewById(R.id.adView5);
        adView6 = (AdView) findViewById(R.id.adView6);

        AdRequest request1 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView1.loadAd(request1);

        AdRequest request2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView2.loadAd(request2);

        AdRequest request3 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView3.loadAd(request3);

        AdRequest request4 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView4.loadAd(request4);

        AdRequest request5 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView5.loadAd(request5);

        AdRequest request6 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        adView6.loadAd(request6);

    }
}
