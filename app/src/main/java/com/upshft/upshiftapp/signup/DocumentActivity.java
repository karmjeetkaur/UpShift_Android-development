package com.upshft.upshiftapp.signup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.adapter.DocumentListAdapter;
import java.util.ArrayList;

public class DocumentActivity extends AppCompatActivity
{
    RecyclerView recycleView;
    ArrayList<String> arrayList;
    Button txt_upgrade;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String PREF = "camera_pref";
    public static final int MY_PERMISSIONS_REQUEST = 100;
    // SAMPLE APP CONSTANTS
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
 //   private static final String PRODUCT_ID = "com.upshft.upshiftapp.monthlysubscription";
    private static final String SUBSCRIPTION_ID = "com.upshft.upshiftapp.monthlysubscription";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi89Xb+dHeC3jKnIPCgFu63UhWn+SGHrwotC1o1fhTTCaj7OZRWFJvs8akSaTToo6CFX4yEj/QWy6ZHpIQs+C3s9aMorO+9RMUVk/F+QghdVPbkoiqJXjMqA5Gifv+2iqrQ6Hq+m5LRU3NAqO6Zn2HycezdT7+5lfSPv+8h6zpwAl7EgP9hptSl8BRvBO3KzUneBkfDJx49BgMRJyu/+1otYPccMEZpgLGHVjAhVvGRplZNXCTUk50yz/EVXWG4hrf7nzqp4viX1xW0vFj2doZbdC013lDZGjkjGqGbSA5C1tsl8xbGEr8nTGOcL1Hgs5t1D4MaYwNV5jhVzezZXZtQIDAQAB";   // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        add_permission();

        recycleView = (RecyclerView) findViewById(R.id.recycleView);
        txt_upgrade = (Button) findViewById(R.id.txt_upgrade);

        LinearLayoutManager add_morelayoutManager = new LinearLayoutManager(DocumentActivity.this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(add_morelayoutManager);

        arrayList = new ArrayList<String>();
        arrayList.add("Registration");
        arrayList.add("License");
        arrayList.add("Insurance");
        arrayList.add("Drivers License");

        DocumentListAdapter mAdapter = new DocumentListAdapter(arrayList, DocumentActivity.this);
        recycleView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        txt_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void showDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder
                .setMessage("Do you Agree to In-App Purchases to Upgrade and add more Storage!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        if (!readyToPurchase) {
                            //   showToast("Billing not initialized.");
                            return;
                        }
                        bp.subscribe(DocumentActivity.this,SUBSCRIPTION_ID);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextViews()
    {

        // Toast.makeText(getApplicationContext(), String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"),Toast.LENGTH_SHORT).show();
        Log.e("Payment_status",String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
    }


    @Override
    public void onDestroy()
    {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    public void add_permission() {
        if (ContextCompat.checkSelfPermission(DocumentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(DocumentActivity.this, ALLOW_KEY)) {
                //  permissionToDrawOverlays();
                showSettingsAlert();
            } else if (ContextCompat.checkSelfPermission(DocumentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(DocumentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(DocumentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
                }
            }
        } else {

        }
    }

    private void showSettingsAlert() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(DocumentActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Read external storage.");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "DON'T ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(DocumentActivity.this);

                    }
                });
        alertDialog.show();
    }

    private void showAlert() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(DocumentActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Read external storage.");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "DON'T ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(DocumentActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST);
                    }
                });
        alertDialog.show();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }


    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            saveToPreferences(DocumentActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

}
