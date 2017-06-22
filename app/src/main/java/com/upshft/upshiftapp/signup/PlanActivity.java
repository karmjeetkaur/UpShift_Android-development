package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.DashboardActivity;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.modal.PaymentDetails;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PlanActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txt_upgade_starter, txt_upgade_lite, txt_upgade_plus, txt_upgade_pro, txt_stay_free;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef;
    private DatabaseReference mDatabase;
    String payment_Status = "";

    String paymentstatus = "";
    private Firebase mRef = new Firebase("https://upshift-db2cf.firebaseio.com/");

    // SAMPLE APP CONSTANTS
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "com.upshft.upshiftapp.monthlysubscription";
    private static final String SUBSCRIPTION_ID = "com.upshft.upshiftapp.monthlysubscription";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi89Xb+dHeC3jKnIPCgFu63UhWn+SGHrwotC1o1fhTTCaj7OZRWFJvs8akSaTToo6CFX4yEj/QWy6ZHpIQs+C3s9aMorO+9RMUVk/F+QghdVPbkoiqJXjMqA5Gifv+2iqrQ6Hq+m5LRU3NAqO6Zn2HycezdT7+5lfSPv+8h6zpwAl7EgP9hptSl8BRvBO3KzUneBkfDJx49BgMRJyu/+1otYPccMEZpgLGHVjAhVvGRplZNXCTUk50yz/EVXWG4hrf7nzqp4viX1xW0vFj2doZbdC013lDZGjkjGqGbSA5C1tsl8xbGEr8nTGOcL1Hgs5t1D4MaYwNV5jhVzezZXZtQIDAQAB";   // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                showToast("onProductPurchased: " + productId);
                paymentstatus = "Succesfull";
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
             //   showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/PaymentDetails");

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            getDateDetails(mUserId);
        }

        txt_upgade_starter = (TextView) findViewById(R.id.txt_upgade_starter);
        txt_upgade_lite = (TextView) findViewById(R.id.txt_upgade_lite);
        txt_upgade_plus = (TextView) findViewById(R.id.txt_upgade_plus);
        txt_upgade_pro = (TextView) findViewById(R.id.txt_upgade_pro);
        txt_stay_free = (TextView) findViewById(R.id.txt_stay_free);

        txt_upgade_starter.setOnClickListener(this);
        txt_upgade_lite.setOnClickListener(this);
        txt_upgade_plus.setOnClickListener(this);
        txt_upgade_pro.setOnClickListener(this);
        txt_stay_free.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_upgade_starter:
                //  tryLaunchApp("UpShift", "com.upshft.upshiftapp");
                if (!payment_Status.equals("")) {
                    Intent intent = new Intent(PlanActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (!readyToPurchase) {
                        //   showToast("Billing not initialized.");
                        return;
                    }

                    bp.subscribe(PlanActivity.this, SUBSCRIPTION_ID);

                }

                break;

            case R.id.txt_upgade_lite:
                tryLaunchApp("UpShift Lite", "com.upshft.upshiftapplite");
                break;

            case R.id.txt_upgade_plus:
                tryLaunchApp("UpShift Plus", "com.upshft.upshiftapplite");
                break;

            case R.id.txt_upgade_pro:
                tryLaunchApp("UpShift Pro", "com.upshft.upshiftapplite");
                break;

            case R.id.txt_stay_free:
                Intent main_intent = new Intent(PlanActivity.this, DashboardActivity.class);
                main_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(main_intent);
                finish();
                break;
        }
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


    public void getDateDetails(String mUserId) {
        myFirebaseRef.child(mUserId).child("paymentStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String paymentStatus = dataSnapshot.getValue(String.class);
                    payment_Status = paymentStatus;
                    Log.e("paymentStatus", "=========paymentStatus===========" + paymentStatus);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);

    }
    public void checkTime(String userId) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String startDate = sdf.format(c.getTime());

        String[] separated = startDate.split("/");
        int year = Integer.parseInt(separated[0]);
        int month = Integer.parseInt(separated[1]);
        int day = Integer.parseInt(separated[2]);

        Calendar calendar = new GregorianCalendar(year, month - 1, day);
        System.out.println("current Date : " + sdf.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, 1);
        String endDate = sdf.format(calendar.getTime());
        PaymentDetails paymentDetailsModel = new PaymentDetails(mUserId, paymentstatus, startDate, endDate);
        //  mDatabase.child("PaymentDetails").child(userId).child("items").push().setValue(paymentDetailsModel);
        mRef.child("PaymentDetails").child(userId).setValue(paymentDetailsModel);
    }

    private void updateTextViews() {
        // Toast.makeText(getApplicationContext(), String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"),Toast.LENGTH_SHORT).show();
        Log.e("Payment_status", String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
        if (!paymentstatus.equals("")) {
            checkTime(mUserId);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
