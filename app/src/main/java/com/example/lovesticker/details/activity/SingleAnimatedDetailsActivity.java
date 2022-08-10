package com.example.lovesticker.details.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.UriUtils;
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
    private static final int REQUEST_Single_CODE = 2;
    private String singleAnimatedDetailsImage;
    private SingleAnimatedCategoriesBean.Postcards postcards;
    private int rewardInterval = 0;

    private Uri saveUri;
    private Boolean isLoadAD = false;
    private Boolean isDownload = false;
    private Boolean isNoAd = false;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        if (LSMKVUtil.getBoolean("SingleAnimatedInterstitialAd", false) &&
                LSMKVUtil.getBoolean("loadad", true)) {
            MaxADManager.tryShowInterstitialDetailAd(this);
            LSMKVUtil.put(" SingleAnimatedInterstitialAd", false);
        }

        if (LSMKVUtil.getBoolean("loadad", true)) {
            viewBinding.adContainer.setVisibility(View.VISIBLE);
            MaxADManager.loadBannerIntoView(this, viewBinding.adContainer);
        }else {
            viewBinding.adContainer.setVisibility(View.GONE);
        }


        singleAnimatedDetailsImage = getIntent().getStringExtra("singleAnimatedDetailsImage");
        postcards = (SingleAnimatedCategoriesBean.Postcards) getIntent().getSerializableExtra("singlePostcards");

        if (singleAnimatedDetailsImage != null) {
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + singleAnimatedDetailsImage)
                    .into(viewBinding.singleDetailsImg);
        }

    }

    @Override
    protected void initClickListener() {

        if (!InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).querySaveStickerGson(postcards.getId())) {
            viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
            viewBinding.collectedImage.setImageResource(R.drawable.collected);
        } else {
            viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
            viewBinding.collectedImage.setImageResource(R.drawable.not_collected);
        }

        viewBinding.isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewBinding.collectedImage.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.collected).getConstantState())) { //点击收藏变未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);

                    if (!InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).querySaveStickerGson(postcards.getId())) {
                        InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).deleteSavePostcards(singleAnimatedDetailsImage);
                    }
                } else { //点击未收藏变收藏

                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);

                    InvokesData.getInvokesData(SingleAnimatedDetailsActivity.this).insertStickerData(
                            new SaveStickerData(postcards.getId(), singleAnimatedDetailsImage));

                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LSMKVUtil.getBoolean("loadad", true)) {
                    rewardInterval = rewardInterval + 1;
                    showRewardDialog(rewardInterval);
                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                } else {
                    isNoAd = true;
                    isLoadAD = false;
                    if (singleAnimatedDetailsImage != null) {
                        showProgressDialog();
                        saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                    }
                }

            }
        });

        MaxADManager.loadInterstitialBackAd(this);
        LSMKVUtil.put(" SingleAnimatedBackAd", true);
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
            int rewarDinter = LSMKVUtil.getInt("rewardinter", 1);

            if (intent == 1) {
                isNoAd = false;
                new AlertDialog.Builder(this)
                        .setMessage("Watch an AD to unblock the content?")
                        .setPositiveButton("Watch ", (dialog, which) -> {
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
                                isLoadAD = true;
                                if (isLoadAD && isDownload) {
                                    shareAny(saveUri);
                                }
                            }

                            @Override
                            public void onTimeOut() {
                                dismissProgressDialog();
                                isLoadAD = true;
                                if (isLoadAD && isDownload) {
                                    shareAny(saveUri);
                                }
                            }
                        });
                    } catch (Exception e) {

                    }

                }).setCancelable(false).show();

            } else if (intent % (rewarDinter + 1) != 1) { //不弹激励广告
                isNoAd = true;
                isLoadAD = false;
                if (singleAnimatedDetailsImage != null) {
                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
                }

            } else { //  弹激励广告
                isNoAd = false;
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
                                isLoadAD = true;
                                if (isLoadAD && isDownload) {
                                    shareAny(saveUri);
                                }

                            }

                            @Override
                            public void onTimeOut() {
                                dismissProgressDialog();
                                isLoadAD = true;
                                if (isLoadAD && isDownload) {
                                    shareAny(saveUri);
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


    private void saveLocal(String imgAddress) {

        new Thread(() -> {
            File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }
            File file = new File(imgFile.getAbsolutePath() + File.separator + singleAnimatedDetailsImage);

            fileStorage(file, imgAddress);

            Message msg = new Message();
            msg.what = 0;
            msg.obj = file;
            handler.sendMessage(msg);
        }).start();

    }

    private void fileStorage(File file, String data) {
        if (file.exists() && getFileSize(file) > 0) {
            return;
        }
        byte[] b = new byte[1024];
        try {
            URL url = new URL(data);
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
            Log.e("###", "e: " + e.getMessage());
            System.exit(1);
        }
    }

    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            isDownload = true;
            File file = (File) msg.obj;
            saveUri = UriUtils.file2Uri(file);

            if (isNoAd) {
                shareAny(saveUri);
                dismissProgressDialog();
            } else if (isDownload && isLoadAD) {
                shareAny(saveUri);
            }
        }
        return false;
    });

    protected void shareAny(Uri uri){
        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);//add image path
        startActivityForResult(Intent.createChooser(whatsappIntent, "Share image using"),REQUEST_Single_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_Single_CODE){
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
        }
    }
}