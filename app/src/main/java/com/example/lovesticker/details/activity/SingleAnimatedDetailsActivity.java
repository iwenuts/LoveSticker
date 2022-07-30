package com.example.lovesticker.details.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.databinding.ActivitySingleAnimatedDetailsBinding;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveData;
import com.example.lovesticker.util.room.SaveStickerData;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class SingleAnimatedDetailsActivity extends BaseActivity<BaseViewModel, ActivitySingleAnimatedDetailsBinding> {
    private String singleAnimatedDetailsImage;
    private SingleAnimatedCategoriesBean.Postcards postcards;
    private int rewardInterval = 0;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        if (LSMKVUtil.getBoolean("SingleAnimatedInterstitialAd",false)){
            MaxADManager.tryShowInterstitialDetailAd();
            LSMKVUtil.put(" SingleAnimatedInterstitialAd",false);
        }


        singleAnimatedDetailsImage = getIntent().getStringExtra("singleAnimatedDetailsImage");
        postcards = (SingleAnimatedCategoriesBean.Postcards) getIntent().getSerializableExtra("singlePostcards");

        if (singleAnimatedDetailsImage != null){
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + singleAnimatedDetailsImage)
                    .into(viewBinding.singleDetailsImg);
        }

    }

    @Override
    protected void initClickListener() {

        if (!InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).querySaveStickerGson(postcards.getId())){
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
                        (getResources().getDrawable(R.drawable.collected).getConstantState())){ //点击收藏变未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);

                    if (!InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).querySaveStickerGson(postcards.getId())){
                        InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).deleteSavePostcards(singleAnimatedDetailsImage);
                    }
                }else { //点击未收藏变收藏
                    RateController.getInstance().tryRateFinish(SingleAnimatedDetailsActivity.this, new RateDialog.RatingClickListener() {
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

                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);

                    InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).insertStickerData(
                            new SaveStickerData(postcards.getId(),singleAnimatedDetailsImage));


                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateController.getInstance().tryRateFinish(SingleAnimatedDetailsActivity.this, new RateDialog.RatingClickListener() {
                    @Override
                    public void onClickFiveStart() {
                        if (singleAnimatedDetailsImage != null){
                            showProgressDialog();
                            saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                        }

                    }

                    @Override
                    public void onClick1To4Start() {
                        if (singleAnimatedDetailsImage != null){
                            showProgressDialog();
                            saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                        }

                    }

                    @Override
                    public void onClickReject() {
                        rewardInterval = rewardInterval + 1;
                        showRewardDialog(rewardInterval);

                    }

                    @Override
                    public void onClickCancel() {
                        if (singleAnimatedDetailsImage != null){
                            showProgressDialog();
                            saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                        }

                    }
                });

            }
        });

        MaxADManager.loadInterstitialBackAd();
        LSMKVUtil.put(" SingleAnimatedBackAd",true);
        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

//        viewBinding.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
//                shareAny(uri);
//            }
//        });
    }

    private void showRewardDialog(int intent) {  //间隔一次出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            if (intent % 2 == 0){ //偶数 不弹激励广告
                if (singleAnimatedDetailsImage != null){
                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                }

            }else { // 基数 弹激励广告

                new AlertDialog.Builder(this)
                        .setMessage("Watch an AD to unblock the content?")
                        .setNegativeButton("Cancle", (dialog, which) -> {

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
                                if (singleAnimatedDetailsImage != null){
                                    showProgressDialog();
                                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                                }
                            }

                            @Override
                            public void onTimeOut() {
                                dismissProgressDialog();
                                if (singleAnimatedDetailsImage != null){
                                    showProgressDialog();
                                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
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


    private void saveLocal(String imgAddress){

        new Thread(new Runnable() {
            @Override
            public void run() {
                File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
                if (!imgFile.exists()){
                    imgFile.mkdirs();
                }
                File file = new File(imgFile.getAbsolutePath() + File.separator + singleAnimatedDetailsImage);

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
            shareAny(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker" + File.separator + singleAnimatedDetailsImage);
            dismissProgressDialog();
        }
        return false;
    });






}