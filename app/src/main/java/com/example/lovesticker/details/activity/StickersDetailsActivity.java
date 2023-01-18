package com.example.lovesticker.details.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivitySingleAnimatedDetailsBinding;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveStickerData;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.StickersManager;
import com.gyf.immersionbar.ImmersionBar;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class StickersDetailsActivity extends BaseActivity<BaseViewModel, ActivitySingleAnimatedDetailsBinding> {
    private static final int REQUEST_Single_CODE = 2;
    private String mImage;
    private int mId;
    private int rewardInterval;

    private Uri saveUri;
    private Boolean isLoadAD = false;
    private Boolean isDownload = false;
    private Boolean isNoAd = false;
    private int type = 0;
    private ActivitySingleAnimatedDetailsBinding viewBinding;

    @Override
    protected void initViewBinding() {
        viewBinding = ActivitySingleAnimatedDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(viewBinding.getRoot());
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        MaxADManager.tryShowInterstitialDetailAd();

        if (LSMKVUtil.getBoolean("loadad", true)) {
            viewBinding.adContainer.setVisibility(View.VISIBLE);
            MaxADManager.loadBannerIntoView(this, viewBinding.adContainer);
        } else {
            viewBinding.adContainer.setVisibility(View.GONE);
        }


        mImage = getIntent().getStringExtra("image");
        mId = getIntent().getIntExtra("id", -1);

//        Log.e("###", "mId: " + mId);

        LSEventUtil.logToClickSticker(mId);


        if (mImage != null) {
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + mImage)
                    .error(R.drawable.image_failed)
                    .into(viewBinding.singleDetailsImg);
        }

        MaxADManager.rewardListener(new MaxADManager.OnRewardListener() {
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
                dismissProgressDialog();
                saveLocal(LSConstant.image_gif_uri + mImage);
//                                        isLoadAD = true;
//                                        if (isLoadAD && isDownload) {
//                                            shareAny(saveUri);
//                                        }
            }

            @Override
            public void onTimeOut() {
                Toast.makeText(StickersDetailsActivity.this,"Wow, No Need to watch video this time",Toast.LENGTH_SHORT).show();

                saveLocal(LSConstant.image_gif_uri + mImage);
            }


        });

    }

    @Override
    protected void initClickListener() {

        if (!InvokesData.getInvokesData().querySaveStickerGson(mId)) {
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

                    if (!InvokesData.getInvokesData().querySaveStickerGson(mId)) {
                        InvokesData.getInvokesData().deleteSavePostcards(mId);
                    }
                } else { //点击未收藏变收藏

                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);

                    InvokesData.getInvokesData().insertStickerData(
                            new SaveStickerData(mId, mImage));

                    LSEventUtil.logToFavSticker(mId);

                }

            }
        });


        viewBinding.sendButton.setOnClickListener(v -> {
            LSEventUtil.logToSendSticker(mId);

            if (SPStaticUtils.getBoolean("isFinishScore", false)) {
                if (LSMKVUtil.getBoolean("loadad", true)) {
                    showRewardDialog();

                } else {
//                    isNoAd = true;
//                    isLoadAD = false;
                    if (mImage != null) {
//                        showProgressDialog();
                        saveLocal(LSConstant.image_gif_uri + mImage);
                    }
                }
            } else {
//                isNoAd = true;
//                isLoadAD = false;
                if (mImage != null) {
//                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + mImage);
                }
            }

        });

        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaxADManager.tryShowInterstitialBackAd(StickersDetailsActivity.this);
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

    @Override
    public void onBackPressed() {
        MaxADManager.tryShowInterstitialBackAd(StickersDetailsActivity.this);
        finish();
    }

    private void showRewardDialog() {  //间隔一次出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            int rewarDinter = LSMKVUtil.getInt("rewardinter", 0);
            rewardInterval = LSMKVUtil.getInt("hitsNumber", 0);

            if (rewardInterval % (rewarDinter + 1) == 0) {
//                isNoAd = false;
                new AlertDialog.Builder(this)
                        .setMessage("Watch a short video to unlock this content")
                        .setPositiveButton("Watch ", (dialog, which) -> {

                            showProgressDialog();
                            MaxADManager.tryShowRewardAd(this);


                        }).setNegativeButton("cancel", (dialog, which) -> {

                }).setCancelable(false).show();

            } else {  // 不弹激励广告
//                isNoAd = true;
//                isLoadAD = false;
                if (mImage != null) {
//                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + mImage);
                }
            }

        } catch (Exception e) {

        }
    }


    @Override
    protected void dataObserver() {

    }


    private void saveLocal(String imgAddress) {
        showProgressDialog();
        new Thread(() -> {
//            File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
            File imgFile = new File(getExternalFilesDir(null), "sticker");
            if (!imgFile.exists()) {
                imgFile.mkdirs();
            }
            File file = new File(imgFile.getAbsolutePath() + File.separator + mImage);

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

            if (StickersManager.getFileSize(file) > 0){
                LSEventUtil.logToDownloadStickerComplete(mId);
            }else {
                LSEventUtil.logToDownloadStickerFailed(mId);
            }



            saveUri = UriUtils.file2Uri(file);

            shareAny(saveUri);
            dismissProgressDialog();

//            if (isNoAd) {
//                shareAny(saveUri);
//                dismissProgressDialog();
//            } else if (isDownload && isLoadAD) {
//                shareAny(saveUri);
//            }
        }
        return false;
    });

    protected void shareAny(Uri uri) {
        if (SPStaticUtils.getBoolean("isFinishScore", false)) {
            rewardInterval = rewardInterval + 1;
            LSMKVUtil.put("hitsNumber", rewardInterval);
        }


        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);//add image path
        startActivityForResult(Intent.createChooser(whatsappIntent, "Share image using"), REQUEST_Single_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_Single_CODE && resultCode == Activity.RESULT_OK) {
            RateController.getInstance().tryRateFinish(StickersDetailsActivity.this, new RateDialog.RatingClickListener() {

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

                @Override
                public void onFinishScore() {
                    SPStaticUtils.put("isFinishScore", true);
                }
            });
        }
    }
}