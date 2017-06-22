package com.upshft.upshiftapp.signup;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upshft.upshiftapp.DashboardActivity;
import com.upshft.upshiftapp.R;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by new on 12/13/2016.
 */
public class SplashActivity2 extends AppCompatActivity
{
    ImageView mImageView;
    private Intent mIntent;
    private static final String TAG = "Android";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash_page);
        initalizeView();

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.upshft.upshiftapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

            Log.e("PackageManager:",e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.e("PackageManager:",e.getMessage());
        }

    }
    private void initalizeView()
    {

        mImageView=(ImageView)findViewById(R.id.iv_gear);
        Animation a = AnimationUtils.loadAnimation(SplashActivity2.this, R.anim.rutatinggearanimation);
        a.setDuration(900);
        mImageView.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                FirebaseUser mUser = mAuth.getCurrentUser();
                if (mUser != null)
                {
                    // User is signed in
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    String uid = mAuth.getCurrentUser().getUid();
                    intent.putExtra("user_id", uid);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                }
                else
                {
                    mIntent= new Intent(SplashActivity2.this, SignInActivity.class);
                    startActivity(mIntent);
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
