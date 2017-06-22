package com.upshft.upshiftapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/**
 * Created by adamwhitlock on 4/22/16.
 */
public class TutorialPagerAdapter extends FragmentStatePagerAdapter {
    private static final int COUNT = 6;

    // Images resources
    private static final int[] IMAGE_RES_IDS = {
            R.drawable.howto1, R.drawable.howto2, R.drawable.howto3, R.drawable.howto4, R.drawable.howto5, R.drawable.howto6 };

    // Text resources
    private static final int[] TITLES_RES_IDS = {
            R.string.howto1, R.string.howto2, R.string.howto3, R.string.howto4, R.string.howto5, R.string.howto6 };

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialScreenFragment.newInstance(IMAGE_RES_IDS[position], TITLES_RES_IDS[position]);
    }


    @Override
    public int getCount() {
        return COUNT;
    }
}
