package com.example.lovesticker.details.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivitySingleAnimatedDetailsBinding;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.gyf.immersionbar.ImmersionBar;

public class SingleAnimatedDetailsActivity extends BaseActivity<BaseViewModel, ActivitySingleAnimatedDetailsBinding> {
    private String singleAnimatedDetailsImage;


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        singleAnimatedDetailsImage = getIntent().getStringExtra("singleAnimatedDetailsImage");
        
        if (singleAnimatedDetailsImage != null){
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + singleAnimatedDetailsImage)
                    .into(viewBinding.singleDetailsImg);
        }




    }

    @Override
    protected void initClickListener() {

        viewBinding.isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LSMKVUtil.getBoolean("animationDetailsCollected",false)){  //收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);
                    LSMKVUtil.put("animationDetailsCollected",true);


                }else {  //未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);
                    LSMKVUtil.put("animationDetailsCollected",false);

                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewBinding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                shareAny(uri);
            }
        });


    }

    @Override
    protected void dataObserver() {

    }

}