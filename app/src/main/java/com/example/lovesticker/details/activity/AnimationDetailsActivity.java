package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.databinding.ActivityAnimationDetailsBinding;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveData;
import com.example.lovesticker.util.room.SaveStickerData;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class AnimationDetailsActivity extends BaseActivity<BaseViewModel, ActivityAnimationDetailsBinding> {
    private String detailsImage;
    private AllAnimatedBean.Postcards postcards;
    private Gson gson = new Gson();
    private int rewardInterval = 0;
    private int rewardChange = 0;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        if (LSMKVUtil.getBoolean("AnimationInterstitialAd",false) &&
                LSMKVUtil.getBoolean("loadad",true)){
            MaxADManager.tryShowInterstitialDetailAd(this);
            LSMKVUtil.put("AnimationInterstitialAd",false);
        }

        if (LSMKVUtil.getBoolean("loadad", true)) {
            viewBinding.adContainer.setVisibility(View.VISIBLE);
            MaxADManager.loadBannerIntoView(this, viewBinding.adContainer);
        }else {
            viewBinding.adContainer.setVisibility(View.GONE);
        }


        detailsImage = getIntent().getStringExtra("detailsImage");

        postcards = (AllAnimatedBean.Postcards) getIntent().getSerializableExtra("postcards");

        if (detailsImage != null){
            Log.e("###", "detailsImage: " + detailsImage);
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + detailsImage)
                    .into(viewBinding.detailsImg);
        }

    }

    @Override
    protected void initClickListener() {
        if (!InvokesData.getInvokesData(AnimationDetailsActivity.this).querySaveStickerGson(postcards.getId())){
            viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
            viewBinding.collectedImage.setImageResource(R.drawable.collected);
        }else {
            viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
            viewBinding.collectedImage.setImageResource(R.drawable.not_collected);
        }


        viewBinding.isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewBinding.collectedImage.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.collected).getConstantState())){  //点击收藏变未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);

                    if (!InvokesData.getInvokesData(AnimationDetailsActivity.this).querySaveStickerGson(postcards.getId())){
                        InvokesData.getInvokesData(AnimationDetailsActivity.this).deleteSavePostcards(detailsImage);
                    }


                }else {  //点击未收藏变收藏

                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);

                    InvokesData.getInvokesData(AnimationDetailsActivity.this).insertStickerData(
                            new SaveStickerData(postcards.getId(),detailsImage));

//                    LoveStickerApp.getSaveData().setSavePostcardId(postcards.getId());
//                    LoveStickerApp.getSaveData().setSavePostcardsImg(detailsImage);
//                    InvokesData.getInvokesData(AnimationDetailsActivity.this).insertData(LoveStickerApp.getSaveData());

                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LSMKVUtil.getBoolean("loadad",true)){
                    rewardInterval = rewardInterval + 1;
                    showRewardDialog(rewardInterval);
                }else {
                    if (detailsImage != null){
                        showProgressDialog();
                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                    }

                }

            }
        });

        MaxADManager.loadInterstitialBackAd(this);
        LSMKVUtil.put("AnimationDetailsBackAd",true);
        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    private void showRewardDialog(int intent) {  //间隔出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            int rewarDinter = LSMKVUtil.getInt("rewardinter",1);

             if (intent == 1){
                    new AlertDialog.Builder(this)
                            .setMessage("Watch an AD to unblock the content?")
                            .setNegativeButton("Cancel", (dialog, which) -> {

                            }).setPositiveButton("Watch ", (dialog, which) -> {
                        try {
                            showProgressDialog();
                            MaxADManager.loadRewardAdAndShow(this, 15000, new MaxADManager.OnRewardListener() {
                                @Override
                                public void onRewardFail() {
                                    dismissProgressDialog();
                                }

                                @Override
                                public void onRewardShown() {
                                    dismissProgressDialog();
                                }

                                @Override
                                public void onRewarded() {
                                    if (detailsImage != null){
                                        showProgressDialog();
                                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                                    }
                                }

                                @Override
                                public void onTimeOut() {
                                    dismissProgressDialog();
                                    if (detailsImage != null){
                                        showProgressDialog();
                                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                                    }
                                }
                            });
                        } catch (Exception e) {

                        }

                    }).setCancelable(false).show();

                } else if (intent % (rewarDinter+ 1) != 1){ //不弹激励广告
                    if (detailsImage != null){
                        showProgressDialog();
                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                    }

                }else { //弹激励广告

                    new AlertDialog.Builder(this)
                            .setMessage("Watch an AD to unblock the content?")
                            .setNegativeButton("Cancel", (dialog, which) -> {

                            }).setPositiveButton("Watch ", (dialog, which) -> {
                        try {
                            showProgressDialog();
                            MaxADManager.loadRewardAdAndShow(this, 15000, new MaxADManager.OnRewardListener() {
                                @Override
                                public void onRewardFail() {
                                    dismissProgressDialog();
                                }

                                @Override
                                public void onRewardShown() {
                                    dismissProgressDialog();
                                }

                                @Override
                                public void onRewarded() {
                                    if (detailsImage != null){
                                        showProgressDialog();
                                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                                    }
                                }

                                @Override
                                public void onTimeOut() {
                                    dismissProgressDialog();
                                    if (detailsImage != null){
                                        showProgressDialog();
                                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                                    }
                                }
                            });
                        } catch (Exception e) {

                        }
                    }).setCancelable(false).show();
                }

        } catch (Exception e) {

        }
    }

    @Override
    protected void dataObserver() {

    }

    private void getImgCachePath(String imgUrl, Context context){
        Handler handler = new Handler(msg -> {
            if (msg.what == 0) {
                File file = (File) msg.obj;

                if (file != null){
                    shareAny(file.getAbsolutePath());
                }
                dismissProgressDialog();
            }
            return false;
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file =  Glide.with(context)
                            .load(imgUrl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();

                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = file;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void saveLocal(String imgAddress){

        new Thread(new Runnable() {
            @Override
            public void run() {
                File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
                if (!imgFile.exists()){
                    imgFile.mkdirs();
                }
                File file = new File(imgFile.getAbsolutePath() + File.separator + detailsImage);

                byte[] b = new byte[1024];
                try {
                    URL url = new URL(imgAddress);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    DataInputStream di = new DataInputStream(urlConnection.getInputStream());
                    // output
                    FileOutputStream fo = new FileOutputStream(file);
                    // copy the actual file
                    // (it would better to use a buffer bigger than this)
                    while (-1 != di.read(b, 0, 1))
                        fo.write(b, 0, 1);
                    di.close();
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("###", "e: " +e.getMessage());
                    System.exit(1);
                }

                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            shareAny(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker" + File.separator + detailsImage);
            dismissProgressDialog();

            RateController.getInstance().tryRateFinish(AnimationDetailsActivity.this, new RateDialog.RatingClickListener() {

                @Override
                public void onClickFiveStart() {

                }

                @Override
                public void onClick1To4Start() {

                }

                @Override
                public void onClickReject() {

                }

                @Override
                public void onClickCancel() {

                }
            });
        }
        return false;
    });


}