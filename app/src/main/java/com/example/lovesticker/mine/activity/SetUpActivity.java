package com.example.lovesticker.mine.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.lovesticker.R;
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
                share(SetUpActivity.this,"Install this app to download coolest stickers:",
                        "https://play.google.com/store/apps/details?id=wasticker.love.stickers.whatsapp.apps","");
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
                launchIntent.setData(Uri.parse("market://details?id=" + packageName));
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent launchIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
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


    public static void share(Context context, String shareText, String url, String parameter) {
        Intent shareIntent = new Intent();
        if (!(context instanceof Activity)) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        String shareTips;
        String htmlTips;
        if (url!=null) {
            shareTips = String.format("%s\n\n%s", shareText, url);
            htmlTips = String.format("%s\n\nUse my <a href= \"%s\">referrer link</a >", shareText, url);
        }
        else {
            shareTips = String.format("%s\n\nhttps://play.google.com/store/apps/details?id=%s%s", shareText, context.getPackageName(), parameter);
            htmlTips = String.format("%s\n\n Use my <a href=\"https://play.google.com/store/apps/details?id=%s%s\">referrer link</a >", shareText, context.getPackageName(), parameter);
        }


        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTips);
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, htmlTips);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");


        Intent receiver = new Intent(context, SetUpActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
        //切记需要使用Intent.createChooser，否则会出现别样的应用选择框，您可以试试
        shareIntent = Intent.createChooser(shareIntent, context.getString(R.string.app_name), pendingIntent.getIntentSender());
        context.startActivity(shareIntent);
    }
}