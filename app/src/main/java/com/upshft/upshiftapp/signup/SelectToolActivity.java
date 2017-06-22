package com.upshft.upshiftapp.signup;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.upshft.upshiftapp.DashboardActivity;
import com.upshft.upshiftapp.HashUtils;
import com.upshft.upshiftapp.HttpRequest;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.adapter.FeatureListAdapter;
import com.upshft.upshiftapp.adapter.PaymentSpinnerAdapter;
import com.upshft.upshiftapp.modal.Grid_Modal;
import com.upshft.upshiftapp.modal.PaymentDetails;
import com.upshft.upshiftapp.modal.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import grid.ExpandableHeightGridView;

public class SelectToolActivity extends AppCompatActivity
{
    private ArrayList<String> mList_item = new ArrayList<String>();
    private ArrayList<Integer> image_mList = new ArrayList<Integer>();
    private ArrayList<Grid_Modal> mList = new ArrayList<Grid_Modal>();
    gridAdapter mAdapter;
    private TextView mTextView;
    ImageView grid_item_image;
    LinearLayout grid_main_item_layout;
    AdView mAdView;
    private Button bt_next;
    ExpandableHeightGridView mExpandableHeightGridView;
    TextView txt_upgrade;
    RadioButton male, fe_male;
    EditText ed_age;
    EditText mCar, mYear, mMake, mModal, mMiles;
    ArrayList<Grid_Modal> platform_arraylist = new ArrayList<Grid_Modal>();
    String name, email, password, phone, age, car, car_year, car_make, car_model, car_miles;
    RadioButton soleprop_radio, soleprop_llc, soleprop_individual;
    ArrayList<String> selected_tool = new ArrayList<String>();
    ArrayList<String> platform_tool = new ArrayList<String>();
    String optional = "";
    String gender = "Male";
    private static final String TAG = "Android";
    private Firebase mRef = new Firebase("https://upshift-db2cf.firebaseio.com/");
    private User user;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private boolean signedIn;
    private SharedPreferences sharedPreferences;
    String status, idToken, user_id;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef, mypaymentFirebaseRef;
    //    Bitmap bitmap;
    View view;
    Button btn_send;
    String paymentstatus = "";
    String payment_type = "";
    // String payment_price = "";
    ArrayList<String> paymentTypeArrayList = new ArrayList<String>();

    TextView txt_male, txt_female;
    RelativeLayout Individual_layout, Llc_layout, SoleProp_layout;
    TextView txt_SoleProp, txt_Llc, txt_Individual;
    ImageView icon_Individual, icon_Llc, icon_SoleProp;

    // SAMPLE APP CONSTANTS
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
    //   private static final String PRODUCT_ID = "com.upshft.upshiftapp.monthlysubscription";
    private static final String SUBSCRIPTION_ID = "com.upshft.upshiftapp.monthlysubscription";
     // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi89Xb+dHeC3jKnIPCgFu63UhWn+SGHrwotC1o1fhTTCaj7OZRWFJvs8akSaTToo6CFX4yEj/QWy6ZHpIQs+C3s9aMorO+9RMUVk/F+QghdVPbkoiqJXjMqA5Gifv+2iqrQ6Hq+m5LRU3NAqO6Zn2HycezdT7+5lfSPv+8h6zpwAl7EgP9hptSl8BRvBO3KzUneBkfDJx49BgMRJyu/+1otYPccMEZpgLGHVjAhVvGRplZNXCTUk50yz/EVXWG4hrf7nzqp4viX1xW0vFj2doZbdC013lDZGjkjGqGbSA5C1tsl8xbGEr8nTGOcL1Hgs5t1D4MaYwNV5jhVzezZXZtQIDAQAB
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi89Xb+dHeC3jKnIPCgFu63UhWn+SGHrwotC1o1fhTTCaj7OZRWFJvs8akSaTToo6CFX4yEj/QWy6ZHpIQs+C3s9aMorO+9RMUVk/F+QghdVPbkoiqJXjMqA5Gifv+2iqrQ6Hq+m5LRU3NAqO6Zn2HycezdT7+5lfSPv+8h6zpwAl7EgP9hptSl8BRvBO3KzUneBkfDJx49BgMRJyu/+1otYPccMEZpgLGHVjAhVvGRplZNXCTUk50yz/EVXWG4hrf7nzqp4viX1xW0vFj2doZbdC013lDZGjkjGqGbSA5C1tsl8xbGEr8nTGOcL1Hgs5t1D4MaYwNV5jhVzezZXZtQIDAQAB";   // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slecte_tool);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mypaymentFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/PaymentDetails");
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/users/");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        signedIn = false;

        mAuth = FirebaseAuth.getInstance();


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                showToast("onProductPurchased: " + productId);
                paymentstatus = "Succesfull";
                public_login();
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                //    showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                //    showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        email = intent.getExtras().getString("email");
        password = intent.getExtras().getString("password");
        phone = intent.getExtras().getString("phone");
        user_id = intent.getExtras().getString("user_id");
        status = intent.getExtras().getString("status");
        idToken = intent.getExtras().getString("idToken");

        Bundle extras = getIntent().getExtras();
        platform_arraylist = (ArrayList<Grid_Modal>) extras.getSerializable("arraylist");

        if (platform_arraylist.size() != 0) {
            for (int i = 0; i < platform_arraylist.size(); i++) {
                platform_tool.add(platform_arraylist.get(i).getName());
            }
        }

        paymentTypeArrayList.add("");
        paymentTypeArrayList.add("UpShift Starter : $1.99 USD - Monthly");
        paymentTypeArrayList.add("UpShift Lite : $6.99 USD - Monthly");
        paymentTypeArrayList.add("UpShift Plus : $11.99 USD - Monthly");
        paymentTypeArrayList.add("UpShift Pro : $15.99 USD - Monthly");

        mList_item.add("Square Reader");
        mList_item.add("Waze");
        mList_item.add("Google Maps");
        mList_item.add("Spotify");
        mList_item.add("Pandora");
        mList_item.add("Surge Chaser");
        mList_item.add("Rideshare PAX");
        mList_item.add("Square Cash");
        mList_item.add("Mystro");

        image_mList.add(R.drawable.uber);
        image_mList.add(R.drawable.lyft);
        image_mList.add(R.drawable.door);
        image_mList.add(R.drawable.postmates);
        image_mList.add(R.drawable.ic_roadies);
        image_mList.add(R.drawable.ic_grav);
        image_mList.add(R.drawable.ic_ride_pax);
        image_mList.add(R.drawable.ic_uzurv_icon);
        image_mList.add(R.drawable.ic_fare_icon);

        for (int i = 0; i < mList_item.size(); i++) {
            Grid_Modal actor = new Grid_Modal();
            actor.setName(mList_item.get(i));
            actor.setmFalg(false);
            mList.add(actor);
        }

        mAdapter = new gridAdapter();
        initailizeGridView();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
            myFirebaseRef.child(mUserId).child("age").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    ed_age.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("car").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mCar.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("car_year").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mYear.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("car_make").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mMake.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("car_model").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mModal.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            myFirebaseRef.child(mUserId).child("car_miles").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mMiles.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gender = dataSnapshot.getValue(String.class);
                    if (gender.equals("Male")) {
                        male.setChecked(true);
                        txt_male.setTextColor(Color.parseColor("#34495e"));
                        txt_female.setTextColor(Color.parseColor("#CDCDCD"));
                    } else {
                        fe_male.setChecked(true);
                        txt_male.setTextColor(Color.parseColor("#CDCDCD"));
                        txt_female.setTextColor(Color.parseColor("#34495e"));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("optional").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    optional = dataSnapshot.getValue(String.class);
                    if (optional.equals("Soleprop")) {
                        icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                        icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        soleprop_radio.setChecked(true);
                    } else if (optional.equals("Llc")) {
                        icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                        icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        soleprop_llc.setChecked(true);
                    } else {
                        icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                        icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                        soleprop_individual.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("tools_arrayList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        txt_upgrade.setVisibility(View.GONE);
                        mExpandableHeightGridView.setVisibility(View.VISIBLE);
                        ArrayList<String> tools_arrayList = (ArrayList<String>) dataSnapshot.getValue();
                        Log.d("tools_arrayList", "........." + dataSnapshot.getValue() + platform_arraylist.size());
                        if (tools_arrayList.size() != 0) {
                            for (int i = 0; i < tools_arrayList.size(); i++) {
                                String platform = tools_arrayList.get(i);
                                Log.d("platform_arraylist", "....item...." + tools_arrayList.get(i));
                                for (int k = 0; k < mList.size(); k++) {
                                    if (platform.equals(mList.get(k).getName())) {
                                        mList.get(k).setmFalg(true);
                                        selected_tool.add(platform);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            mypaymentFirebaseRef.child(mUserId).child("paymentStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String paymentStatus = dataSnapshot.getValue(String.class);
                        Log.e("paymentStatus", "=========paymentStatus===========" + paymentStatus);
                        if (!paymentStatus.equals(null)) {
                            txt_upgrade.setVisibility(View.GONE);
                            mExpandableHeightGridView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        male = (RadioButton) findViewById(R.id.male);
        fe_male = (RadioButton) findViewById(R.id.fe_male);

        txt_male = (TextView) findViewById(R.id.txt_male);
        txt_female = (TextView) findViewById(R.id.txt_female);

        Individual_layout = (RelativeLayout) findViewById(R.id.Individual_layout);
        Llc_layout = (RelativeLayout) findViewById(R.id.Llc_layout);
        SoleProp_layout = (RelativeLayout) findViewById(R.id.SoleProp_layout);

        txt_SoleProp = (TextView) findViewById(R.id.txt_SoleProp);
        txt_Llc = (TextView) findViewById(R.id.txt_Llc);
        txt_Individual = (TextView) findViewById(R.id.txt_Individual);
        icon_Individual = (ImageView) findViewById(R.id.icon_Individual);
        icon_Llc = (ImageView) findViewById(R.id.icon_Llc);
        icon_SoleProp = (ImageView) findViewById(R.id.icon_SoleProp);

        ed_age = (EditText) findViewById(R.id.ed_age);
        mCar = (EditText) findViewById(R.id.mCar);
        mYear = (EditText) findViewById(R.id.mYear);
        mMake = (EditText) findViewById(R.id.mMake);
        mModal = (EditText) findViewById(R.id.mModal);
        mMiles = (EditText) findViewById(R.id.mMiles);

        soleprop_radio = (RadioButton) findViewById(R.id.soleprop_radio);
        soleprop_llc = (RadioButton) findViewById(R.id.soleprop_llc);
        soleprop_individual = (RadioButton) findViewById(R.id.soleprop_individual);
        bt_next = (Button) findViewById(R.id.bt_next);
        btn_send = (Button) findViewById(R.id.btn_send);

        txt_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
                txt_male.setTextColor(Color.parseColor("#34495e"));
                txt_female.setTextColor(Color.parseColor("#CDCDCD"));
                Log.e("gender", "======Male========" + gender);
            }
        });

        txt_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Fe-male";
                txt_male.setTextColor(Color.parseColor("#CDCDCD"));
                txt_female.setTextColor(Color.parseColor("#34495e"));
                Log.e("gender", "======Fe-male========" + gender);
            }
        });

        Individual_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optional = "Soleprop";
                txt_SoleProp.setTextColor(Color.parseColor("#CDCDCD"));
                txt_Llc.setTextColor(Color.parseColor("#CDCDCD"));
                txt_Individual.setTextColor(Color.parseColor("#34495e"));
                icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
            }
        });

        Llc_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optional = "Llc";
                txt_SoleProp.setTextColor(Color.parseColor("#CDCDCD"));
                txt_Llc.setTextColor(Color.parseColor("#34495e"));
                txt_Individual.setTextColor(Color.parseColor("#CDCDCD"));
                icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
            }
        });

        SoleProp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optional = "Individual";
                txt_SoleProp.setTextColor(Color.parseColor("#34495e"));
                txt_Llc.setTextColor(Color.parseColor("#CDCDCD"));
                txt_Individual.setTextColor(Color.parseColor("#CDCDCD"));
                icon_SoleProp.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorDark));
                icon_Individual.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
                icon_Llc.setColorFilter(getApplicationContext().getResources().getColor(R.color.text_color_light));
            }
        });

        RadioGroup rg_gender = (RadioGroup) findViewById(R.id.myRadioGroup);
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        // do operations specific to this selection
                        gender = "Male";
                        break;
                    case R.id.fe_male:
                        // do operations specific to this selection
                        gender = "Fe-male";
                        break;
                }
            }
        });

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup_optional);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.soleprop_radio:
                        // do operations specific to this selection
                        optional = "Soleprop";
                        break;
                    case R.id.soleprop_llc:
                        // do operations specific to this selection
                        optional = "Llc";
                        break;
                    case R.id.soleprop_individual:
                        // do operations specific to this selection
                        optional = "Individual";
                        break;
                }
            }
        });

        if (status.equals("Update")) {
            bt_next.setText("Edit");
            btn_send.setVisibility(View.VISIBLE);
        } else {
            bt_next.setText("Next");
            btn_send.setVisibility(View.GONE);
        }
        bt_next.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!male.isChecked() && !fe_male.isChecked())
                {
                    Toast.makeText(SelectToolActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else if (ed_age.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                } else if (mCar.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                } else if (mYear.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                } else if (mMake.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                } else if (mModal.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                } else if (mMiles.getText().toString().length() == 0) {
                    Toast.makeText(SelectToolActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    age = ed_age.getText().toString();
                    car = mCar.getText().toString();
                    car_year = mYear.getText().toString();
                    car_make = mMake.getText().toString();
                    car_model = mModal.getText().toString();
                    car_miles = mMiles.getText().toString();

                    if (status.equals("GoogleLogin"))
                    {
                        showSubscribeDialog(SelectToolActivity.this, "hvhj");
                       // firebaseAuthWithGoogle(idToken);
                    }
                    else if (status.equals("FacebookLogin"))
                    {
                        showSubscribeDialog(SelectToolActivity.this, "hvhj");
                    //    signInWithFacebook(idToken);
                    }
                    else if (status.equals("Update"))
                    {
                        Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_LONG).show();
                        User user = new User(mUserId, name, phone, email, password, gender, age, car, car_year, car_make, car_model, car_miles, optional
                                , platform_tool, selected_tool);
                        myFirebaseRef.child(mUserId).setValue(user);
                        Intent intent = new Intent(SelectToolActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        showSubscribeDialog(SelectToolActivity.this, "hvhj");
                      //  onSignUpClicked();
                    }
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String platform = "";
                String selected = "";
                for (int i = 0; i < platform_tool.size(); i++) {
                    platform = platform + platform_tool.get(i) + ",";
                    Log.e("platform", "......platform......." + platform);
                }

                for (int i = 0; i < selected_tool.size(); i++) {
                    selected = selected + selected_tool.get(i) + ",";
                    Log.e("selected", "......selected......." + selected);
                }

                age = ed_age.getText().toString();
                car = mCar.getText().toString();
                car_year = mYear.getText().toString();
                car_make = mMake.getText().toString();
                car_model = mModal.getText().toString();
                car_miles = mMiles.getText().toString();

                String MailData = "UpShift User Profile Information:-  \n" + "Name: " + name + ",\n" + "Email: " + email + ",\n" + "Phone: " + phone + ",\n" + "Password: " + password + ",\n" + "Gender: " + gender + ",\n"
                        + "Age: " + age + ",\n" + "Car: " + car + ",\n" + "Car Year: " + car_year + ",\n" + "Car Make: " + car_make +
                        ",\n" + "Car Model: " + car_model + ",\n" + "Car Miles: " + car_miles + ",\n" + "Business/Entity Type: " + optional
                        + ",\n" + "Platform Tools: " + platform + ",\n" + "Selected Tools: " + selected;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "User Details");
                i.putExtra(Intent.EXTRA_TEXT, MailData);
                try {

                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SelectToolActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void setUpUser() {
        user = new User();
        user.setName(name);
        user.setPhoneNumber(phone);
        user.setEmail(email);
        user.setPassword(password);
    }

    public void onSignUpClicked()
    {
        createNewAccount(email, password);
        showProgressDialog();
    }

    private void createNewAccount(String email, String password) {
        Log.d(TAG, "createNewAccount:" + email);
//        if (!validateForm()) {
//            retu
// rn;
//        }
        setUpUser();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Toast.makeText(SelectToolActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthenticationSucess(task.getResult().getUser());
                        }
                    }
                });
    }

    private void onAuthenticationSucess(FirebaseUser mUser) {
        saveNewUser(mUser.getUid(), user.getName(), user.getPhoneNumber(), user.getEmail(), user.getPassword());
        //  signOut();
        handleSignInResult(mUser.getUid(), mUser.getEmail(), null, null);
        Intent intent = new Intent(SelectToolActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        mAuth.signOut();
    }

    private void saveNewUser(String userId, String name, String phone, String email, String password) {
        User user = new User(userId, name, phone, email, password, gender, age, car, car_year, car_make, car_model, car_miles, optional
                , platform_tool, selected_tool);
        mRef.child("users").child(userId).setValue(user);

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void initailizeGridView() {

        txt_upgrade = (TextView) findViewById(R.id.txt_upgrade);
        mExpandableHeightGridView = (ExpandableHeightGridView) findViewById(R.id.grid);
        mExpandableHeightGridView.setAdapter(mAdapter);
        mExpandableHeightGridView.setExpanded(true);

        txt_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog(SelectToolActivity.this, "jgijhgoi");
            }
        });

        mExpandableHeightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int mPosition = position;
                String mItem = mList.get(mPosition).getName();
                // Toast.makeText(SelectToolActivity.this, "Select= " + mItem, Toast.LENGTH_SHORT).show();
                boolean mFlag = mList.get(mPosition).ismFalg();
                if (mFlag == true) {
                    for (int k = 0; k < selected_tool.size(); k++) {
                        if (mItem.equals(selected_tool.get(k))) {
                            selected_tool.remove(k);
                        }
                    }
                    mList.get(mPosition).setmFalg(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    selected_tool.add(mItem);
                    mList.get(mPosition).setmFalg(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public class gridAdapter extends BaseAdapter {
        boolean mFlag = false;

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Grid_Modal actor = mList.get(position);
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View mView = mLayoutInflater.inflate(R.layout.grid_single, null);

            mTextView = (TextView) mView.findViewById(R.id.tv_textbox);
            mTextView.setText(actor.getName());
            mTextView.setTextSize(13);
            grid_item_image = (ImageView) mView.findViewById(R.id.grid_item_image);
            grid_item_image.setImageResource(image_mList.get(position));
            grid_main_item_layout = (LinearLayout) mView.findViewById(R.id.grid_main_item_layout);

            boolean mFlag = actor.ismFalg();

            if (mFlag == true) {
                grid_main_item_layout.setBackgroundResource(R.drawable.select_checkbox);
                // mTextView.setBackgroundResource(R.drawable.select_checkbox);
                mTextView.setTextColor(Color.parseColor("#70000000"));
            } else {
                grid_main_item_layout.setBackgroundResource(R.drawable.check_box_back);
                //  mTextView.setBackgroundResource(R.drawable.check_box_back);
                mTextView.setTextColor(Color.parseColor("#70000000"));
            }
            return mView;
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    private void handleSignInResult(String id, String email, String idToken, String displayName) {
        // Signed in successfully, show authenticated UI.
        signedIn = true;
        sharedPreferences.edit().putBoolean("pref_sign_in", signedIn).commit();

        sharedPreferences.edit().putBoolean("pref_signed_in", true).commit();
        sharedPreferences.edit().putString("SIGN_IN_ID", id).commit();
        sharedPreferences.edit().putString("SIGN_IN_EMAIL", email).commit();

        String url = "https://pennybuilt.com/upshiftapp/api/v1/add.php";
        HashMap<String, String> query = new HashMap<>();
        String checksum = "";
        if (id != null) {
            query.put("id", id);
            checksum = checksum + id;
        }
        if (idToken != null) {
            query.put("idToken", idToken);
            checksum = idToken + checksum;
        }
        checksum = checksum + "wdtq3bhldadqkv0i";
        if (displayName != null) {
            query.put("displayName", displayName);
            checksum = checksum + displayName;
        }
        if (email != null) {
            query.put("email", email);
            checksum = checksum + email;
        }
        String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (device != null) {
            query.put("device", device);
            checksum = device + checksum;
        }
        String model = Build.MODEL;
        if (model != null) {
            query.put("model", model);
            checksum = checksum + model;
        }
        if (checksum != null) {
            checksum = HashUtils.getSHA256(checksum);
            checksum = Base64.encodeToString(checksum.getBytes(), Base64.DEFAULT);

            query.put("checksum", checksum);
        }
        SendPostRequestTask task = new SendPostRequestTask(url, query);
        task.execute((Void) null);
        return;
//        }
    }

    private class SendPostRequestTask extends AsyncTask<Void, Void, JSONObject> {
        private String url;
        private HashMap<String, String> query;

        public SendPostRequestTask(String url, HashMap<String, String> query) {
            this.url = url;
            this.query = query;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpRequest request = null;
            JSONObject response = null;
            try {
                request = new HttpRequest(url);
                response = request.preparePost().withData(query).sendAndReadJSON();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            } catch (JSONException e) {
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject error = result.getJSONObject("error");
                    // mark this task as incomplete, an error occurred, retry later
                    sharedPreferences.edit().putBoolean("ERROR_SENDING_SIGN_IN_DATA", true).commit();
                } catch (JSONException e) {
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + idToken);
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SelectToolActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = task.getResult().getUser().getUid();
                    Log.e(TAG, ".......google uid" + uid + "....mAuth id..." + mAuth.getCurrentUser());
                    String name = task.getResult().getUser().getDisplayName();
                    String email = task.getResult().getUser().getEmail();

                    saveNewUser(uid, name, phone, email, "");
//                    signOut();
                    handleSignInResult(uid, email, null, null);
                    Intent intent = new Intent(SelectToolActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
    }

    private void signInWithFacebook(String token) {
        Log.d(TAG, "signInWithFacebook:" + token);
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SelectToolActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = task.getResult().getUser().getUid();
                    Log.e(TAG, "...........uid............" + uid + "...mAuth......" + mAuth.getCurrentUser());
                    String name = task.getResult().getUser().getDisplayName();
                    String email = task.getResult().getUser().getEmail();
                    //  String image = task.getResult().getUser().getPhotoUrl().toString();

                    saveNewUser(uid, name, phone, email, "");
//                    signOut();
                    handleSignInResult(uid, email, null, null);
                    Intent intent = new Intent(SelectToolActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
    }

    public void checkTime(String userId) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String startDate = sdf.format(c.getTime());
        Log.e("startDate", ".......checkTime.............." + startDate);

        String[] separated = startDate.split("/");
        int year = Integer.parseInt(separated[0]);
        int month = Integer.parseInt(separated[1]);
        int day = Integer.parseInt(separated[2]);

        Calendar calendar = new GregorianCalendar(year, month - 1, day);
        System.out.println("current Date : " + sdf.format(calendar.getTime()));

        calendar.add(Calendar.MONTH, 1);
        String endDate = sdf.format(calendar.getTime());
        PaymentDetails paymentDetailsModel = new PaymentDetails(mUserId, paymentstatus, startDate, endDate);
        // mDatabase.child("PaymentDetails").child(userId).child("items").push().setValue(paymentDetailsModel);
        mRef.child("PaymentDetails").child(userId).setValue(paymentDetailsModel);
    }

    public void showPaymentDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment_dialog_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Spinner payment_spinner = (Spinner) dialog.findViewById(R.id.payment_spinner);
        Button btn_subscribe = (Button) dialog.findViewById(R.id.btn_subscribe);

        ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Resources res = getResources();
        PaymentSpinnerAdapter adapter = new PaymentSpinnerAdapter(activity, R.layout.spinner_rows, paymentTypeArrayList, res);
        payment_spinner.setAdapter(adapter);

        btn_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payment_type.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select any value", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (payment_type.equals("UpShift Starter : $1.99 USD - Monthly"))
                    {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        stringArrayList.add("Add-in-One Dashboard");
                        stringArrayList.add("Run multi-platforms in one dash");
                        stringArrayList.add("Platform Widget Bar & Tool Widget Bar ");
                        stringArrayList.add("Document Storage");
                        stringArrayList.add("Coupons & Referrals");
                        stringArrayList.add("Instant Dashboard Data");
                        stringArrayList.add("Shift tools, timer, toggle switch, & summary");
                        stringArrayList.add("Deadhead and Total mile toggle switches");
                        stringArrayList.add("SMS Friend Referral");

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stringArrayList", stringArrayList);
                        Intent mIntent = new Intent(SelectToolActivity.this, FeatureListActivity.class);
                        mIntent.putExtra("title", "UpShift Starter");
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        dialog.dismiss();

                    }
                    else if (payment_type.equals("UpShift Lite : $6.99 USD - Monthly"))
                    {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        stringArrayList.add("Editable Widget Bar");
                        stringArrayList.add("Automated Text Responding");
                        stringArrayList.add("+/- Revenue & Expenses");
                        stringArrayList.add("More Doc Storage");
                        stringArrayList.add("Platform Ins. Docs");
                        stringArrayList.add("Custom Driver Tools");
                        stringArrayList.add("Task Automations");
                        stringArrayList.add("Passenger Timer");
                        stringArrayList.add("Add Commissions");
                        stringArrayList.add("Surge Notifier");

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stringArrayList", stringArrayList);
                        Intent mIntent = new Intent(SelectToolActivity.this, FeatureListActivity.class);
                        mIntent.putExtra("title", "UpShift Lite");
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        dialog.dismiss();
                    }
                    else if (payment_type.equals("UpShift Plus : $11.99 USD - Monthly"))
                    {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        stringArrayList.add("Upgraded Ul/UX");
                        stringArrayList.add("Platform Request On/Off Automation");
                        stringArrayList.add("Advanced Financials Summaries");
                        stringArrayList.add("Pre-Filled Tax Forms");
                        stringArrayList.add("Workshift Automations");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stringArrayList", stringArrayList);
                        Intent mIntent = new Intent(SelectToolActivity.this, FeatureListActivity.class);
                        mIntent.putExtra("title", "UpShift Plus");
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        dialog.dismiss();

                    } else if (payment_type.equals("UpShift Pro : $15.99 USD - Monthly")) {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        stringArrayList.add("Web App");
                        stringArrayList.add("Feature Automation");
                        stringArrayList.add("Independent Driver Platform for scheduling and booking");
                        stringArrayList.add("InstantPay — Same day funds");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stringArrayList", stringArrayList);
                        Intent mIntent = new Intent(SelectToolActivity.this, FeatureListActivity.class);
                        mIntent.putExtra("title", "UpShift Pro");
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        dialog.dismiss();
                    }
                }
            }
        });

        payment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    payment_type = "";
                } else {
                    payment_type = paymentTypeArrayList.get(position);
                    Log.e("payment_type", "......................." + payment_type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        dialog.show();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextViews() {

        // Toast.makeText(getApplicationContext(), String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"),Toast.LENGTH_SHORT).show();
        Log.e("Payment_status", String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
        if (!paymentstatus.equals("")) {
            checkTime(mUserId);
        }
    }

    public void showSubscribeDialog(Activity activity, String msg)
    {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment_dialog_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Spinner payment_spinner = (Spinner) dialog.findViewById(R.id.payment_spinner);
        Button btn_subscribe = (Button) dialog.findViewById(R.id.btn_subscribe);
        btn_subscribe.setText("Subscribe");

        paymentTypeArrayList.clear();
        paymentTypeArrayList.add("");
        paymentTypeArrayList.add("UpShift Starter : $1.99 USD - Monthly");
        paymentTypeArrayList.add("Stay Free");
        Resources res = getResources();
        PaymentSpinnerAdapter adapter = new PaymentSpinnerAdapter(activity, R.layout.spinner_rows, paymentTypeArrayList, res);
        payment_spinner.setAdapter(adapter);

        payment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    payment_type = "";
                }
                else
                {
                    payment_type = paymentTypeArrayList.get(position);
                    Log.e("payment_type", "......................." + payment_type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_subscribe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (payment_type.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select any value", Toast.LENGTH_SHORT).show();
                }
                else if (payment_type.equals("UpShift Starter : $1.99 USD - Monthly"))
                {
                    dialog.dismiss();
                    if (!readyToPurchase)
                    {
                        //   showToast("Billing not initialized.");
                        return;
                    }
                    bp.subscribe(SelectToolActivity.this, SUBSCRIPTION_ID);
                }
                else
                {
                    dialog.dismiss();
                    public_login();

                }
            }
        });
        dialog.show();
    }

    public void public_login()
    {
        if (status.equals("GoogleLogin"))
        {
            firebaseAuthWithGoogle(idToken);
        }
        else if (status.equals("FacebookLogin"))
        {
            signInWithFacebook(idToken);
        }
        else
        {
            onSignUpClicked();
        }
    }

}
