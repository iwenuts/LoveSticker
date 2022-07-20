package com.example.lovesticker.details.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityAnimationDetailsBinding;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;

import java.net.URI;

public class AnimationDetailsActivity extends BaseActivity<BaseViewModel, ActivityAnimationDetailsBinding> {
    private String detailsImage;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        detailsImage = getIntent().getStringExtra("detailsImage");

        if (detailsImage != null){
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + detailsImage)
                    .into(viewBinding.detailsImg);
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
            public void onClick(View v) {  //todo 网上找的分享到whatsapp

                Uri uri = Uri.parse(LSConstant.image_gif_uri + detailsImage);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.whatsapp");
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                shareIntent.setType("image/*");
                startActivity(shareIntent);
            }
        });




    }

    @Override
    protected void dataObserver() {

    }


}