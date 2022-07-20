package com.example.lovesticker.mine.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivitySetUpBinding;
import com.gyf.immersionbar.ImmersionBar;

public class SetUpActivity extends BaseActivity<BaseViewModel, ActivitySetUpBinding> {


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();
    }

    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

        viewBinding.rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotGooglePlayStore(SetUpActivity.this,getPackageName());
            }
        });

        viewBinding.shareUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewBinding.categoriesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void gotGooglePlayStore(Context context,String packageName){
        try {
            try {
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.setAction("com.android.vending");
                launchIntent.setData(Uri.parse("market://details?id=$packageName"));
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent launchIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    );
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(launchIntent);
                } catch (Throwable ee) {
                    ee.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}