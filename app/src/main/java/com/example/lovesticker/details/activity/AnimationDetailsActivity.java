package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import static com.blankj.utilcode.util.ImageUtils.save;


public class AnimationDetailsActivity extends BaseActivity<BaseViewModel, ActivityAnimationDetailsBinding> {
    private String detailsImage;
    private AllAnimatedBean.Postcards postcards;
    private Gson gson = new Gson();
    private int rewardInterval = 0;
    private int rewardChange = 0;
    public static int REQUEST_Animation_CODE = 1;
    private Uri saveUri;
    private Boolean isLoadAD = false;
    private Boolean isDownload = false;
    private Boolean isNoAd = false;


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        if (LSMKVUtil.getBoolean("AnimationInterstitialAd", false) &&
                LSMKVUtil.getBoolean("loadad", true)) {
            MaxADManager.tryShowInterstitialDetailAd(this);
            LSMKVUtil.put("AnimationInterstitialAd", false);
        }

        if (LSMKVUtil.getBoolean("loadad", true)) {
            viewBinding.adContainer.setVisibility(View.VISIBLE);
            MaxADManager.loadBannerIntoView(this, viewBinding.adContainer);
        } else {
            viewBinding.adContainer.setVisibility(View.GONE);
        }


        detailsImage = getIntent().getStringExtra("detailsImage");

        postcards = (AllAnimatedBean.Postcards) getIntent().getSerializableExtra("postcards");

        if (detailsImage != null) {
            Log.e("###", "detailsImage: " + detailsImage);
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + detailsImage)
                    .error(R.drawable.image_failed)
                    .into(viewBinding.detailsImg);
        }

    }

    @Override
    protected void initClickListener() {
        if (!InvokesData.getInvokesData(AnimationDetailsActivity.this).querySaveStickerGson(postcards.getId())) {
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
                        (getResources().getDrawable(R.drawable.collected).getConstantState())) {  //点击收藏变未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);

                    if (!InvokesData.getInvokesData(AnimationDetailsActivity.this).querySaveStickerGson(postcards.getId())) {
                        InvokesData.getInvokesData(AnimationDetailsActivity.this).deleteSavePostcards(detailsImage);
                    }


                } else {  //点击未收藏变收藏

                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);

                    InvokesData.getInvokesData(AnimationDetailsActivity.this).insertStickerData(
                            new SaveStickerData(postcards.getId(), detailsImage));

//                    LoveStickerApp.getSaveData().setSavePostcardId(postcards.getId());
//                    LoveStickerApp.getSaveData().setSavePostcardsImg(detailsImage);
//                    InvokesData.getInvokesData(AnimationDetailsActivity.this).insertData(LoveStickerApp.getSaveData());

                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LSMKVUtil.getBoolean("loadad", true)) {
                    rewardInterval = rewardInterval + 1;
                    showRewardDialog(rewardInterval);
                    saveLocal(LSConstant.image_gif_uri + detailsImage);
                } else {
                    isNoAd = true;
                    isLoadAD = false;
                    if (detailsImage != null) {
                        showProgressDialog();
                        saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                    }

                }
            }
        });

        MaxADManager.loadInterstitialBackAd(this);
        LSMKVUtil.put("AnimationDetailsBackAd", true);
        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showRewardDialog(int intent) {  //间隔出现激励弹窗 ex:第一次出现，第二次不出现......
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
                if (detailsImage != null) {
                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + detailsImage);

//                     getImgCachePath(LSConstant.image_gif_uri + detailsImage,AnimationDetailsActivity.this);
                }

            } else { //弹激励广告
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
            File file = new File(imgFile.getAbsolutePath() + File.separator + detailsImage);

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

    protected void shareAny(Uri uri) {
        if (uri != null) {
            Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
            whatsappIntent.setType("image/*");
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);//add image path
            startActivityForResult(Intent.createChooser(whatsappIntent, "Share image using"), REQUEST_Animation_CODE);
        } else {
            Toast.makeText(AnimationDetailsActivity.this, "fail in send", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_Animation_CODE) {
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
    }
}