package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.upshft.upshiftapp.R;

public class DeepLinkActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DeepLinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
        findViewById(R.id.button_ok).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (AppInviteReferral.hasReferral(intent)) {
            processReferralIntent(intent);
        }
    }

    private void processReferralIntent(Intent intent) {

        String invitationId = AppInviteReferral.getInvitationId(intent);
        String deepLink = AppInviteReferral.getDeepLink(intent);

        Log.d(TAG, "Found Referral: " + invitationId + ":" + deepLink);
        ((TextView) findViewById(R.id.deep_link_text)).setText(getString(R.string.deep_link_fmt, deepLink));
        ((TextView) findViewById(R.id.invitation_id_text)).setText(getString(R.string.invitation_id_fmt, invitationId));

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_ok) {
            finish();
        }
    }
}