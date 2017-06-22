//package com.upshft.upshift;
//
//import android.content.Intent;
//import android.provider.SyncStateContract;
//import android.view.Menu;
//import android.view.MenuInflater;
//
//import com.daimajia.androidanimations.library.Techniques;
//import com.viksaa.sssplash.lib.activity.AwesomeSplash;
//import com.viksaa.sssplash.lib.cnst.Flags;
//import com.viksaa.sssplash.lib.model.ConfigSplash;
//
///**
// * Created by adamwhitlock on 4/26/16.
// */
//public class SplashActivity extends AwesomeSplash {
//
//
//    @Override
//    public void initSplash(ConfigSplash configSplash) {
//
//            /* you don't have to override every property */
//
//        //Customize Circular Reveal
//        configSplash.setBackgroundColor(R.color.colorDark); //any color you want form colors.xml
//        configSplash.setAnimCircularRevealDuration(800); //int ms
//        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);  //or Flags.REVEAL_LEFT
//        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP
//
//        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default
//
//        //Customize Logo
//        configSplash.setLogoSplash(R.drawable.splash_logo); //or any other drawable
//        configSplash.setAnimLogoSplashDuration(1000); //int ms
//        configSplash.setAnimLogoSplashTechnique(Techniques.FadeInDown); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)
//
//
//        //Customize Title
//        configSplash.setTitleSplash("UpShift");
//        configSplash.setTitleTextColor(R.color.colorPrimary);
//        configSplash.setTitleTextSize(28f); //float value
//        configSplash.setAnimTitleDuration(800);
//        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
//
//    }
//
//    @Override
//    public void animationsFinished() {
//        Intent Spintent = new Intent(SplashActivity.this, DashboardActivity.class);
//        startActivity(Spintent);
//        finish();
//        return;
//        //transit to another activity here
//        //or do whatever you want
//    }
//}
