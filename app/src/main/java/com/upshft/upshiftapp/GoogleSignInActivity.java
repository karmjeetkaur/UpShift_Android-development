package com.upshft.upshiftapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecord;
import com.upshft.upshiftapp.database.deadheads.DeadheadRecordDAO;
import com.upshft.upshiftapp.database.expenses.ExpenseRecord;
import com.upshft.upshiftapp.database.expenses.ExpenseRecordDAO;
import com.upshft.upshiftapp.database.revenues.RevenueRecord;
import com.upshft.upshiftapp.database.revenues.RevenueRecordDAO;
import com.upshft.upshiftapp.database.shifts.ShiftRecord;
import com.upshft.upshiftapp.database.shifts.ShiftRecordDAO;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Set;

public class GoogleSignInActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_CODE_SIGN_IN = 5;
    private GoogleApiClient mGoogleApiClient;
    public static final String EXTRA_ACTION_SIGN_IN = "sign_in";
    private boolean signedIn;
    private SharedPreferences sharedPreferences;
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_ACTION_REVOKE_ACCESS = "revoke_access";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getStringExtra(EXTRA_ACTION);
        if(action != null) {
            setContentView(R.layout.activity_loading);
        } else {
            setContentView(R.layout.activity_google_sign_in);
            // set the screen orientation to portrait at all times so that is never rotates to landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
                    findViewById(R.id.getRolling).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                    signIn();
                }
            });
        }

        // Configure sign-in to request the user's ID, email address, and basic
       //  profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        // Build a GoogleApiClient with access to the Google Sign-In API and the
//        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        signedIn = false;

        if(action != null) {
            signIn();
        }
    }

    private void signIn() {
        if(!signedIn) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }
    }

    private void revokeAccess()
    {
        if(signedIn)
        {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>()
            {
                @Override
                public void onResult(@NonNull Status status)
                {
                    if(status.isSuccess()) {
                        String id = sharedPreferences.getString("SIGN_IN_ID", "NULL");

                        signedIn = false;
                        sharedPreferences.edit().putBoolean("pref_sign_in", signedIn).commit();

                        // delete user data
                        DeadheadRecordDAO dDAO = new DeadheadRecordDAO(GoogleSignInActivity.this);
                        for(DeadheadRecord record : dDAO.getRecords()) {
                            dDAO.delete(record);
                        }
                        ExpenseRecordDAO eDAO = new ExpenseRecordDAO(GoogleSignInActivity.this);
                        for(ExpenseRecord record : eDAO.getRecords()) {
                            eDAO.delete(record);
                        }
                        RevenueRecordDAO rDAO = new RevenueRecordDAO(GoogleSignInActivity.this);
                        for(RevenueRecord record : rDAO.getRecords()) {
                            rDAO.delete(record);
                        }
                        ShiftRecordDAO sDAO = new ShiftRecordDAO(GoogleSignInActivity.this);
                        for(ShiftRecord record : sDAO.getRecords()) {
                            sDAO.delete(record);
                        }

                        // reset prefs to default
                        PreferenceManager.getDefaultSharedPreferences(GoogleSignInActivity.this).edit().clear().commit();
                        PreferenceManager.setDefaultValues(GoogleSignInActivity.this, R.xml.preferences, true);
                        PreferenceManager.getDefaultSharedPreferences(GoogleSignInActivity.this).edit().putBoolean("pref_first_run", false).commit(); // don't retrigger the tutorial

                        // send revoke access request
                        String url = "https://pennybuilt.com/upshiftapp/api/v1/revoke.php";

                        HashMap<String, String> query = new HashMap<>();

                        String checksum = "";
                        if(id != null) {
                            query.put("id", id);
                            checksum = checksum + id;
                        }
                        checksum = checksum + "veuum2elpaw2w31p";
                        String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        if(device != null) {
                            query.put("device", device);
                            checksum = device + checksum;
                        }
                        if(checksum != null) {
                            checksum = HashUtils.getSHA256(checksum);
                            checksum = Base64.encodeToString(checksum.getBytes(), Base64.DEFAULT);

                            query.put("checksum", checksum);
                        }

                        SendPostRequestTask task = new SendPostRequestTask(url, query);
                        task.execute((Void)null);

                        Intent intent = new Intent(GoogleSignInActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        Toast.makeText(GoogleSignInActivity.this, "revoke access unsuccessful", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            signedIn = true;
            sharedPreferences.edit().putBoolean("pref_sign_in", signedIn).commit();

            String action = getIntent().getStringExtra(EXTRA_ACTION);
            if(action != null && action.equals(EXTRA_ACTION_REVOKE_ACCESS)) {
                revokeAccess();
            } else {
                sharedPreferences.edit().putBoolean("pref_signed_in", true).commit();

                GoogleSignInAccount account = result.getSignInAccount();
                String id = account.getId();
                String idToken = account.getIdToken();
                String displayName = account.getDisplayName();
                String email = account.getEmail();
                Set<Scope> scopes = account.getGrantedScopes();

                Toast.makeText(GoogleSignInActivity.this, "Email---"+ email, Toast.LENGTH_LONG).show();

                sharedPreferences.edit().putString("SIGN_IN_ID", id).commit();
                sharedPreferences.edit().putString("SIGN_IN_EMAIL", email).commit();

                String url = "https://pennybuilt.com/upshiftapp/api/v1/add.php";

                HashMap<String, String> query = new HashMap<>();

                String checksum = "";
                if(id != null) {
                    query.put("id", id);
                    checksum = checksum + id;
                }
                if(idToken != null) {
                    query.put("idToken", idToken);
                    checksum = idToken + checksum;
                }
                checksum = checksum + "wdtq3bhldadqkv0i";
                if(displayName != null) {
                    query.put("displayName", displayName);
                    checksum = checksum + displayName;
                }
                if(email != null) {
                    query.put("email", email);
                    checksum = checksum + email;
                }
                String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if(device != null) {
                    query.put("device", device);
                    checksum = device + checksum;
                }
                String model = Build.MODEL;
                if(model != null) {
                    query.put("model", model);
                    checksum = checksum + model;
                }
                if(checksum != null) {
                    checksum = HashUtils.getSHA256(checksum);
                    checksum = Base64.encodeToString(checksum.getBytes(), Base64.DEFAULT);

                    query.put("checksum", checksum);
                }

                SendPostRequestTask task = new SendPostRequestTask(url, query);
                task.execute((Void)null);

                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } else {
            // Signed out, show unauthenticated UI.
         //   findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
            findViewById(R.id.getRolling).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            Toast.makeText(this, "sign in unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Connection Failed")
                .setMessage("An unknown connection error has occurred. Please try again later.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GoogleSignInActivity.this.finish();
                    }
                })
                .show();
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

//            String print = "";
//            if(result != null) {
//                print = result.toString();
//            }
//
//            AlertDialog dialog = new AlertDialog.Builder(GoogleSignInActivity.this)
//                    .setTitle("JSON")
//                    .setMessage(print)
//                    .setNeutralButton("OK", null)
//                    .show();

            if(result != null) {
                try {
                    JSONObject error = result.getJSONObject("error");

                    // mark this task as incomplete, an error occurred, retry later
                    sharedPreferences.edit().putBoolean("ERROR_SENDING_SIGN_IN_DATA", true).commit();
                } catch (JSONException e) {
                }
            }
        }
    }
}
