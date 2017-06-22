package com.upshft.upshiftapp.signup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upshft.upshiftapp.R;

public class UpdateUserInfoActivity extends AppCompatActivity {

    EditText edit_text_name, edit_text_email_id, edit_text_phone, edit_text_gender, edit_text_age;
    Button button_update;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        myFirebaseRef = new Firebase("https://upshift-db2cf.firebaseio.com/users/");

        edit_text_name = (EditText) findViewById(R.id.edit_text_name);
        edit_text_email_id = (EditText) findViewById(R.id.edit_text_email_id);
        edit_text_phone = (EditText) findViewById(R.id.edit_text_phone);
        edit_text_gender = (EditText) findViewById(R.id.edit_text_gender);
        edit_text_age = (EditText) findViewById(R.id.edit_text_age);
        button_update = (Button) findViewById(R.id.button_update);

// get name value

        if (mFirebaseUser == null)
        {
            // Not logged in, launch the Log In activity
        }
        else
        {
            mUserId = mFirebaseUser.getUid();
            Log.e("FireBase_Email", "................." + mUserId);


            myFirebaseRef.child(mUserId).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    edit_text_name.setText(name);
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
                    edit_text_email_id.setText(email);
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
                    edit_text_phone.setText(phoneNumber);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


            // get email value

            myFirebaseRef.child(mUserId).child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String gender = dataSnapshot.getValue(String.class);
                    edit_text_gender.setText(gender);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


            // get email value

            myFirebaseRef.child(mUserId).child("age").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String age = dataSnapshot.getValue(String.class);
                    edit_text_age.setText(age);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }


        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFirebaseRef.child(mUserId).child("name").setValue(edit_text_name.getText().toString());
                myFirebaseRef.child(mUserId).child("email").setValue(edit_text_email_id.getText().toString());
                myFirebaseRef.child(mUserId).child("phoneNumber").setValue(edit_text_phone.getText().toString());
                myFirebaseRef.child(mUserId).child("gender").setValue(edit_text_gender.getText().toString());
                myFirebaseRef.child(mUserId).child("age").setValue(edit_text_age.getText().toString());
                Toast.makeText(UpdateUserInfoActivity.this, "User Data updated successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
