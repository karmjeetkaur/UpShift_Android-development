package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.modal.Grid_Modal;

import java.util.ArrayList;

import grid.ExpandableHeightGridView;

/**
 * Created by new on 12/13/2016.
 */
public class SignUpActivity extends AppCompatActivity {
    private ArrayList<String> mList_item = new ArrayList<String>();
    private ArrayList<Grid_Modal> mList = new ArrayList<Grid_Modal>();
    private ArrayList<Integer> image_mList = new ArrayList<Integer>();
    private TextView mTextView;
    ImageView grid_item_image;
    LinearLayout grid_main_item_layout;
    gridAdapter mAdapter;
    private Button bt_next;
    AdView mAdView;
    ExpandableHeightGridView mExpandableHeightGridView;
    String selected_mItem;
    EditText mLogin_name, mLogin_email, mLogin_pass, mLogin_ph;
    ArrayList<String> selectedItemList = new ArrayList<String>();
    ArrayList<Grid_Modal> modalArrayList = new ArrayList<Grid_Modal>();
    String status = "simpleLogin", idToken, user_id, email, name;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef;
    Bitmap bitmap;
    View view;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String PREF = "camera_pref";
    public static final int MY_PERMISSIONS_REQUEST = 100;
    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/users/");

//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest request = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
//                .build();
//        mAdView.loadAd(request);

        mLogin_name = (EditText) findViewById(R.id.mLogin_name);
        mLogin_email = (EditText) findViewById(R.id.mLogin_email);
        mLogin_pass = (EditText) findViewById(R.id.mLogin_pass);
        mLogin_ph = (EditText) findViewById(R.id.mLogin_ph);
        bt_next = (Button) findViewById(R.id.bt_next);

        Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
            name = intent.getExtras().getString("name");
            email = intent.getExtras().getString("email");
            user_id = intent.getExtras().getString("user_id");
            status = intent.getExtras().getString("status");
            idToken = intent.getExtras().getString("idToken");

            mLogin_name.setText(name);
            mLogin_name.setEnabled(false);
            mLogin_email.setText(email);
            mLogin_email.setEnabled(false);
            mLogin_pass.setVisibility(View.GONE);
        }
        mList_item.add("Uber");
        mList_item.add("Lyft");
        mList_item.add("Doordash");
        mList_item.add("Postmate");
        mList_item.add("Roadie");
        mList_item.add("Grab");
        mList_item.add("Rideshare");
//        mList_item.add("AmazonFlex");  //
        mList_item.add("Uzurv");
        mList_item.add("Fare");

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
            status = "Update";
            mUserId = mFirebaseUser.getUid();
            mLogin_pass.setVisibility(View.GONE);
            Log.e("FireBase_Email", "................." + mUserId);
            myFirebaseRef.child(mUserId).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    mLogin_name.setText(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            myFirebaseRef.child(mUserId).child("password").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String password = dataSnapshot.getValue(String.class);
                    mLogin_pass.setText(password);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // get email value
            myFirebaseRef.child(mUserId).child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.getValue(String.class);
                    mLogin_email.setText(email);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // get email value
            myFirebaseRef.child(mUserId).child("phoneNumber").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String phoneNumber = dataSnapshot.getValue(String.class);
                    mLogin_ph.setText(phoneNumber);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            ArrayList<String> platform_arraylist = new ArrayList<String>();
            myFirebaseRef.child(mUserId).child("platform_arrayList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.getValue() != null) {
                        ArrayList<String> platform_arraylist = (ArrayList<String>) dataSnapshot.getValue();
                        if (platform_arraylist.size() != 0) {
                            for (int i = 0; i < platform_arraylist.size(); i++) {
                                String platform = platform_arraylist.get(i);
                                Log.d("platform_arraylist", "....item...." + platform_arraylist.get(i));
                                for (int k = 0; k < mList.size(); k++) {
                                    if (platform.equals(mList.get(k).getName())) {
                                        mList.get(k).setmFalg(true);
                                        selectedItemList.add(platform);
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
        }
    }

    private void initailizeGridView() {
        mExpandableHeightGridView = (ExpandableHeightGridView) findViewById(R.id.grid);
        mExpandableHeightGridView.setAdapter(mAdapter);
        mExpandableHeightGridView.setExpanded(true);
        mExpandableHeightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_mItem = mList.get(position).getName();
                //   Toast.makeText(SignUpActivity.this, "Select = " + selected_mItem, Toast.LENGTH_SHORT).show();
                boolean mFlag = mList.get(position).ismFalg();
                if (mFlag == true) {
                    if (selectedItemList.size() != 0) {
                        for (int k = 0; k < selectedItemList.size(); k++) {
                            if (selected_mItem.equals(selectedItemList.get(k))) {
                                selectedItemList.remove(k);
                            }
                        }
                    }
                    mList.get(position).setmFalg(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    selectedItemList.add(selected_mItem);
                    mList.get(position).setmFalg(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View OnclickView) {
                if (!status.equals("simpleLogin")) {
                    email = mLogin_email.getText().toString();
                    if (mLogin_name.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_email.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (!email.matches(emailPattern)) {
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_ph.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (selectedItemList.size() == 0) {
                        Toast.makeText(SignUpActivity.this, "Please select Platforms", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!status.equals("Update")) {
                            view = OnclickView.getRootView();
                            view.setDrawingCacheEnabled(true);
                            bitmap = view.getDrawingCache();
                            Log.e("bitmap", "......bitmap........." + bitmap);
                        }
                        if (selectedItemList.size() != 0) {
                            for (int k = 0; k < selectedItemList.size(); k++) {
                                Grid_Modal modal = new Grid_Modal();
                                modal.setName(selectedItemList.get(k));
                                modalArrayList.add(modal);
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("arraylist", modalArrayList);
                        Intent mIntent = new Intent(SignUpActivity.this, SelectToolActivity.class);
                        mIntent.putExtra("name", mLogin_name.getText().toString());
                        mIntent.putExtra("email", mLogin_email.getText().toString());
                        mIntent.putExtra("password", mLogin_pass.getText().toString());
                        mIntent.putExtra("phone", mLogin_ph.getText().toString());
                        mIntent.putExtra("user_id", user_id);
                        mIntent.putExtra("idToken", idToken);
                        mIntent.putExtra("status", status);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                    }
                } else {
                    email = mLogin_email.getText().toString();
                    if (mLogin_name.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_email.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (!email.matches(emailPattern)) {
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_pass.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_pass.getText().toString().length() < 8) {
                        Toast.makeText(SignUpActivity.this, "Password length at least 8 characters and also maximum of 8", Toast.LENGTH_SHORT).show();
                    } else if (mLogin_ph.getText().toString().length() == 0) {
                        Toast.makeText(SignUpActivity.this, "All field required", Toast.LENGTH_SHORT).show();
                    } else if (selectedItemList.size() == 0) {
                        Toast.makeText(SignUpActivity.this, "Please select Platforms", Toast.LENGTH_SHORT).show();
                    } else {
                        if (selectedItemList.size() != 0) {
                            for (int k = 0; k < selectedItemList.size(); k++) {
                                Grid_Modal modal = new Grid_Modal();
                                modal.setName(selectedItemList.get(k));
                                modalArrayList.add(modal);
                            }
                        }
                        view = OnclickView.getRootView();
                        view.setDrawingCacheEnabled(true);
                        bitmap = view.getDrawingCache();
                        Log.e("bitmap", "......bitmap........." + bitmap);

//                        saveImage(bitmap);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("arraylist", modalArrayList);
                        Intent mIntent = new Intent(SignUpActivity.this, SelectToolActivity.class);
                        mIntent.putExtra("name", mLogin_name.getText().toString());
                        mIntent.putExtra("email", mLogin_email.getText().toString());
                        mIntent.putExtra("password", mLogin_pass.getText().toString());
                        mIntent.putExtra("phone", mLogin_ph.getText().toString());
                        mIntent.putExtra("user_id", "");
                        mIntent.putExtra("idToken", "");
                        mIntent.putExtra("status", status);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                    }
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
                mTextView.setTextColor(Color.parseColor("#70000000"));
            } else {
                grid_main_item_layout.setBackgroundResource(R.drawable.check_box_back);
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
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

}
