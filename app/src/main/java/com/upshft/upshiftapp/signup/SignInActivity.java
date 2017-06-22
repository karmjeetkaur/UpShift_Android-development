package com.upshft.upshiftapp.signup;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upshft.upshiftapp.DashboardActivity;
import com.upshft.upshiftapp.HashUtils;
import com.upshft.upshiftapp.HttpRequest;
import com.upshft.upshiftapp.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecord;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecordDAO;
import com.upshft.upshiftapp.database.expenses.ExpenseRecord;
import com.upshft.upshiftapp.database.expenses.ExpenseRecordDAO;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import com.upshft.upshiftapp.database.revenues.RevenueRecordDAO;
import com.upshft.upshiftapp.database.shifts.ShiftRecord;
import com.upshft.upshiftapp.database.shifts.ShiftRecordDAO;
import com.upshft.upshiftapp.modal.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "Android";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public User user;
    private EditText email;
    private EditText password;
    private ProgressDialog mProgressDialog;
    //FaceBook callbackManager
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    public static GoogleApiClient mGoogleApiClient;
    private boolean signedIn;
    private SharedPreferences sharedPreferences;
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_ACTION_REVOKE_ACCESS = "revoke_access";
    Button forget_button;
    ImageView google_signIn, facebook_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        signedIn = false;

        forget_button = (Button) findViewById(R.id.forget_button);
        forget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPswrd_ViewDialog alert = new ForgetPswrd_ViewDialog();
                alert.showDialog(SignInActivity.this, "Error");
            }
        });

        google_signIn = (ImageView) findViewById(R.id.google_signIn);
        facebook_signIn = (ImageView) findViewById(R.id.facebook_signIn);
        google_signIn.setOnClickListener(this);
        facebook_signIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //  updateUI(mUser);
            }
        };

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                        /* make the API calls */
                showProgressDialog();
                AccessToken token = loginResult.getAccessToken();
                final String fbToken = token.getToken();
                Bundle myBundle = new Bundle();
                myBundle.putString("fields", "id,email,name,picture,friends{id,name,picture}");
                new GraphRequest(loginResult.getAccessToken(), "/me", myBundle, HttpMethod.GET, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                        /* handle the result */
                        if (response.getError() != null) {
                            // handle error
                            System.out.println("ERROR");
                        } else {
                            System.out.println("Success");
                            Log.d("Success", "==============Success=============" + response);
                            JSONObject object = response.getJSONObject();
                            Log.d("Success", "==============object=============" + object);
                            try {
                                if (object != null) {
                                    hideProgressDialog();
                                    final String email = object.getString("email");
                                    final String id = object.getString("id");
                                    final String name = object.getString("name");
                                    String profile_pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Log.v("LoginActivity", id + ".....email........" + email + ".......name....." + name + "profile_pic" + profile_pic);

                                    Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(email);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                hideProgressDialog();
                                                signInWithFacebook(fbToken);
                                            } else {
                                                Intent mIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                                                mIntent.putExtra("user_id", id);
                                                mIntent.putExtra("status", "FacebookLogin");
                                                mIntent.putExtra("name", name);
                                                mIntent.putExtra("email", email);
                                                mIntent.putExtra("idToken", fbToken);
                                                startActivity(mIntent);
                                                hideProgressDialog();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        email = (EditText) findViewById(R.id.edit_text_email_id);
        password = (EditText) findViewById(R.id.edit_text_password);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = result.getSignInAccount();
                //firebaseAuthWithGoogle(account);
                Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(account.getEmail());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            firebaseAuthWithGoogle(account.getIdToken());
                            //  Toast.makeText(getApplicationContext(), "Already exits", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent mIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                            mIntent.putExtra("user_id", account.getId());
                            mIntent.putExtra("status", "GoogleLogin");
                            mIntent.putExtra("name", account.getDisplayName());
                            mIntent.putExtra("email", account.getEmail());
                            mIntent.putExtra("idToken", account.getIdToken());
                            startActivity(mIntent);
                            // finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void setUpUser() {
        user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    public void onSignUpClicked(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        //finish();
    }

    public void onLoginClicked(View view) {
        setUpUser();
        signIn(email.getText().toString(), password.getText().toString());
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail", task.getException());
                    Toast.makeText(SignInActivity.this, "Please Enter Valid Email or Password.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    String uid = mAuth.getCurrentUser().getUid();
                    intent.putExtra("user_id", uid);
                    startActivity(intent);
                    finish();
                    String email_id = mAuth.getCurrentUser().getEmail();
                    String name = mAuth.getCurrentUser().getDisplayName();
                    handleSignInResult(uid, email_id, null, null);
                }
                hideProgressDialog();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String userEmail = email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String userPassword = password.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

    private void signInWithFacebook(String fbToken) {
        Log.d(TAG, "signInWithFacebook:" + fbToken);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(fbToken);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SignInActivity.this, "Please Enter Valid Email or Password.", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = task.getResult().getUser().getUid();
                    String email = task.getResult().getUser().getEmail();

                    handleSignInResult(uid, email, null, null);
                    Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google_signIn) {
            signIn();
        } else if (i == R.id.facebook_signIn) {
            LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "email"));
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private void handleSignInResult(String id, String email, String idToken, String displayName) {
        // Signed in successfully, show authenticated UI.
        signedIn = true;
        sharedPreferences.edit().putBoolean("pref_sign_in", signedIn).commit();

//        String action = getIntent().getStringExtra(EXTRA_ACTION);
//        if (action != null && action.equals(EXTRA_ACTION_REVOKE_ACCESS))
//        {
//            revokeAccess();
//        }
//        else
//        {
        // Toast.makeText(SignInActivity.this, "Email---" + email, Toast.LENGTH_LONG).show();
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

    private void revokeAccess() {
        if (signedIn) {
            String id = sharedPreferences.getString("SIGN_IN_ID", "NULL");
            signedIn = false;
            sharedPreferences.edit().putBoolean("pref_sign_in", signedIn).commit();

            // delete user data
            DeadheadRecordDAO dDAO = new DeadheadRecordDAO(SignInActivity.this);
            for (DeadheadRecord record : dDAO.getRecords()) {
                dDAO.delete(record);
            }
            ExpenseRecordDAO eDAO = new ExpenseRecordDAO(SignInActivity.this);
            for (ExpenseRecord record : eDAO.getRecords()) {
                eDAO.delete(record);
            }
            RevenueRecordDAO rDAO = new RevenueRecordDAO(SignInActivity.this);
            for (RevenueRecord record : rDAO.getRecords()) {
                rDAO.delete(record);
            }
            ShiftRecordDAO sDAO = new ShiftRecordDAO(SignInActivity.this);
            for (ShiftRecord record : sDAO.getRecords()) {
                sDAO.delete(record);
            }
            // reset prefs to default
            PreferenceManager.getDefaultSharedPreferences(SignInActivity.this).edit().clear().commit();
            PreferenceManager.setDefaultValues(SignInActivity.this, R.xml.preferences, true);
            PreferenceManager.getDefaultSharedPreferences(SignInActivity.this).edit().putBoolean("pref_first_run", false).commit(); // don't retrigger the tutorial

            // send revoke access request
            String url = "https://pennybuilt.com/upshiftapp/api/v1/revoke.php";
            HashMap<String, String> query = new HashMap<>();
            String checksum = "";
            if (id != null) {
                query.put("id", id);
                checksum = checksum + id;
            }
            checksum = checksum + "veuum2elpaw2w31p";
            String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (device != null) {
                query.put("device", device);
                checksum = device + checksum;
            }
            if (checksum != null) {
                checksum = HashUtils.getSHA256(checksum);
                checksum = Base64.encodeToString(checksum.getBytes(), Base64.DEFAULT);
                query.put("checksum", checksum);
            }

            SendPostRequestTask task = new SendPostRequestTask(url, query);
            task.execute((Void) null);

            Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            Toast.makeText(SignInActivity.this, "revoke access unsuccessful", Toast.LENGTH_LONG).show();
        }
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

    @Override
    protected void onResume() {
        signOut();
        super.onResume();

    }

    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.e("Google_status", " ..............sign out........");
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(String idToken)
    {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = task.getResult().getUser().getUid();
                    String email = task.getResult().getUser().getEmail();

                    handleSignInResult(uid, email, null, null);
                    Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
            }
        });
    }

    public class ForgetPswrd_ViewDialog {
        EditText ed_email;
        TextView txt_header;
        Button btn_cancel, btn_submit;
        Dialog dialog;

        public void showDialog(Activity activity, String msg) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.forget_dialog);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ed_email = (EditText) dialog.findViewById(R.id.ed_email);
            txt_header = (TextView) dialog.findViewById(R.id.txt_header);
            btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
            btn_submit = (Button) dialog.findViewById(R.id.btn_submit);

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ed_email.getText().toString().length() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Please fill Your Email", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        final String Email = ed_email.getText().toString();
                        dialog.dismiss();
                        showProgressDialog();
                        Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(Email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    Log.e("dataSnapshot", "==================" + dataSnapshot.getValue());
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        User post = postSnapshot.getValue(User.class);
                                        String password = post.getPassword();
                                        Log.e("dataSnapshot", "=======startDate_val===========" + password);
                                        BackgroundMail.newBuilder(SignInActivity.this)
                                                .withUsername("karamjeet.salentro@gmail.com")
                                                .withPassword("android@123")
                                                .withMailto(Email)
                                                .withType(BackgroundMail.TYPE_PLAIN)
                                                .withSubject("Forget Password")
                                                .withBody("this is your Up Shift Password : " + password)
                                                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        //do some magic
                                                    //    Toast.makeText(getApplicationContext(),"Please check your mail for password",Toast.LENGTH_SHORT).show();
                                                        hideProgressDialog();
                                                    }
                                                })
                                                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                                    @Override
                                                    public void onFail() {
                                                        //do some magic
                                                        hideProgressDialog();
                                                    }
                                                })
                                                .send();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }
            });

            dialog.show();
        }
    }

}



