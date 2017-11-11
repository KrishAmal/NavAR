package com.univ.team12.navar.onboarding;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.univ.team12.navar.R;

/**
 * Created by Amal Krishnan on 25-05-2017.
 */

public class DefaultIntro extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide1_title)
                ,getResources().getString(R.string.slide1_desc)
                ,R.drawable.slide1
                ,getResources().getColor(R.color.white)
                ,getResources().getColor(R.color.colorPrimary)
                ,getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide2_title)
                ,getResources().getString(R.string.slide2_desc)
                ,R.drawable.slide2
                ,getResources().getColor(R.color.white)
                ,getResources().getColor(R.color.colorPrimary)
                ,getResources().getColor(R.color.colorPrimaryDark)));

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.slide3_title)
                ,getResources().getString(R.string.slide3_desc)
                ,R.drawable.slide3
                ,getResources().getColor(R.color.white)
                ,getResources().getColor(R.color.colorPrimary)
                ,getResources().getColor(R.color.colorPrimaryDark)));

        setBarColor(getResources().getColor(R.color.colorAccent));
        showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

}
