package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.upshft.upshiftapp.R;

public class ReferralActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
    AdView mAdView;
    private static final String TAG = ReferralActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("92ECEA0F1348E428F6E440E7AF3EE1EC")  // An example device ID
                .build();  //
        mAdView.loadAd(request);

        findViewById(R.id.invite_button).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink).setResultCallback(new ResultCallback<AppInviteInvitationResult>()
        {
            @Override
            public void onResult(AppInviteInvitationResult result) {
                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                if (result.getStatus().isSuccess()) {
                    // Extract information from the intent
                    Intent intent = result.getInvitationIntent();
                    String deepLink = AppInviteReferral.getDeepLink(intent);
                    String invitationId = AppInviteReferral.getInvitationId(intent);
                    // Because autoLaunchDeepLink = true we don't have to do anything
                    // here, but we could set that to false and manually choose
                    // an Activity to launch to handle the deep link here.
                    // ...
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage(getString(R.string.google_play_services_error));
    }

    private void showMessage(String msg) {
        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.invite_button) {
            onInviteClicked();
        }
    }

    private void onInviteClicked()
    {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
              //  .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE)
        {
            if (resultCode == RESULT_OK)
            {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids)
                {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                showMessage(getString(R.string.send_failed));
            }
        }
    }

}
