package com.upshft.upshiftapp.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upshft.upshiftapp.R;
import com.upshft.upshiftapp.adapter.ScreenShotAdapter;
import com.upshft.upshiftapp.modal.ImageModel;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ScreenShotActivity extends AppCompatActivity {
    Button btn_take_pic;
    RecyclerView recycleView;
    ArrayList<Bitmap> ss_arrayList = new ArrayList<Bitmap>();
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private DatabaseReference mDatabase;
    String Doc_Value;
    ScreenShotAdapter adapter;
    private ProgressDialog mProgressDialog;
    public static int limit = 4;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        Doc_Value = getIntent().getStringExtra("Doc_Value");

        header = (TextView) findViewById(R.id.header);
        header.setText("Document Of " + Doc_Value);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
        } else {
            mUserId = mFirebaseUser.getUid();
        }

        previewStoredFirebaseImage();

        btn_take_pic = (Button) findViewById(R.id.btn_take_pic);
        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
        recycleView = (RecyclerView) findViewById(R.id.recycleView);
        LinearLayoutManager add_morelayoutManager = new LinearLayoutManager(ScreenShotActivity.this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(add_morelayoutManager);
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.e("imageBitmap", "................" + imageBitmap);
            if (ss_arrayList.size() >= limit) {
                Toast.makeText(getApplicationContext(), "Your Storage limit crossed", Toast.LENGTH_SHORT).show();
            } else {
                encodeBitmapAndSaveToFirebase(imageBitmap);
            }
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReference("https://upshift-db2cf.firebaseio.com/").child("RegisterDoc").child(mUserId)
//                .child("imageUrl");
//        ref.setValue(imageEncoded);
        ImageModel imageModel = new ImageModel(imageEncoded);
        if (Doc_Value.equals("Registration")) {
            mDatabase.child("RegisterDoc").child(mUserId).child("images").push().setValue(imageModel);
            previewStoredFirebaseImage();
        } else if (Doc_Value.equals("License")) {
            mDatabase.child("LicenseDoc").child(mUserId).child("images").push().setValue(imageModel);
            previewStoredFirebaseImage();
        } else if (Doc_Value.equals("Insurance")) {
            mDatabase.child("InsuranceDoc").child(mUserId).child("images").push().setValue(imageModel);
            previewStoredFirebaseImage();
        } else {
            mDatabase.child("DriversLicenseDoc").child(mUserId).child("images").push().setValue(imageModel);
            previewStoredFirebaseImage();

        }
    }

    private void previewStoredFirebaseImage()
    {
        String getDoc = "";
        if (Doc_Value.equals("Registration"))
        {
            getDoc = "RegisterDoc";

        }
        else if(Doc_Value.equals("License"))
        {
            getDoc = "LicenseDoc";
        }
        else if(Doc_Value.equals("Insurance"))
        {
            getDoc = "InsuranceDoc";
        }
        else
        {
            getDoc = "DriversLicenseDoc";
        }
        final ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
        mDatabase.child(getDoc).child(mUserId).child("images").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.getChildrenCount() == 0) {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"No more Document",Toast.LENGTH_SHORT).show();
                } else {
                    String base64Image = (String) dataSnapshot.child("image_path").getValue();
                    byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                    Bitmap mThumbnailPreview = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    Log.e("mThumbnailPreview", "Downloaded image with length: " + mThumbnailPreview);
                    arrayList.add(mThumbnailPreview);
                    ss_arrayList = arrayList;
                    adapter = new ScreenShotAdapter(arrayList, ScreenShotActivity.this);
                    recycleView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
}
