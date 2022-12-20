package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SPStaticUtils;
import com.bumptech.glide.Glide;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackImageDetailsBinding;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveData;

import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.StickersCallBack;
import com.example.lovesticker.util.stickers.StickersManager;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackImageDetailsActivity extends BaseActivity<BaseViewModel, ActivityPackImageDetailsBinding> {
    private StickerPacks packDetails;
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();
    private int rewardInterval;

    private int imagePosition;
    private int currentPosition; //当前图片所在位置
    private int stickerPackNumber; //图片总数
    private int minusPosition; //上一张图
    private int plusPosition; //下一张图
    private Gson gson = new Gson();

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;

    private boolean stickerPackWhitelistedInWhatsAppConsumer;
    private boolean stickerPackWhitelistedInWhatsAppSmb;

    private ActivityPackImageDetailsBinding viewBinding;

    private File fileTray;
    private File file;

    @Override
    protected void initViewBinding() {
        viewBinding = ActivityPackImageDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(viewBinding.getRoot());
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        LSEventUtil.logToViewSticker();

        MaxADManager.tryShowInterstitialDetailAd(this);

        if (LSMKVUtil.getBoolean("loadad", true)) {
            viewBinding.adContainer.setVisibility(View.VISIBLE);
            MaxADManager.loadBannerIntoView(this, viewBinding.adContainer);
        } else {
            viewBinding.adContainer.setVisibility(View.GONE);
        }


        packDetails = (StickerPacks) getIntent().getSerializableExtra("packDetails_value");
        imagePosition = getIntent().getIntExtra("position", 0);
        stickerPackNumber = getIntent().getIntExtra("stickerPackNumber", 0);


        if (packDetails != null && stickerPackNumber != 0) {
            currentPosition = imagePosition;

            Glide.with(this)
                    .load(LSConstant.image_uri + packDetails.getStickersList().get(imagePosition).getImage())
                    .into(viewBinding.detailsImg);

            viewBinding.stickerTitle.setText(packDetails.getTitle());


            if (LSMKVUtil.getBoolean("IsStickerDetailsClear", false)) {
                sticker.clear();
            }

            for (int i = 0; i < stickerPackNumber; i++) {
                sticker.add(new Sticker(packDetails.getStickersList().get(i).getImage(), new ArrayList<>()));
            }

            LSMKVUtil.put("IsStickerDetailsClear", false);
        }


        stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, packDetails.getIdentifier());
        stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, packDetails.getIdentifier());

        if (!InvokesData.getInvokesData().querySavePackGson(packDetails.getId())
                && stickerPackWhitelistedInWhatsAppConsumer) {  //有数据
            viewBinding.textView2.setText(R.string.added_to_whatsApp);
            viewBinding.sendButton.setEnabled(false);
        } else {
            if (stickerPackWhitelistedInWhatsAppConsumer) {  //已经添加到whatsApp
                viewBinding.textView2.setText(R.string.added_to_whatsApp);
                viewBinding.sendButton.setEnabled(false);

                //添加进收藏
                InvokesData.getInvokesData().insertPackData(
                        new SaveData(packDetails.getId(), gson.toJson(packDetails)));
            } else {
                viewBinding.textView2.setText(R.string.add_to_whatsapp);
                viewBinding.sendButton.setEnabled(true);
            }

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
                addStickerPackToWhatsApp(packDetails.getIdentifier(), packDetails.getTitle());
            }

            @Override
            public void onTimeOut() {
                dismissProgressDialog();
                Toast.makeText(PackImageDetailsActivity.this,"Wow, No Need to watch video this time",Toast.LENGTH_SHORT).show();
                addStickerPackToWhatsApp(packDetails.getIdentifier(), packDetails.getTitle());
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StickersManager.stopDownload();
    }

    @Override
    protected void initClickListener() {

        viewBinding.previousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition > 0) {
                    minusPosition = currentPosition - 1;

                    Glide.with(PackImageDetailsActivity.this)
                            .load(LSConstant.image_uri + packDetails.getStickersList().get(minusPosition).getImage())
                            .into(viewBinding.detailsImg);

                    currentPosition = minusPosition;
                } else {
                    Glide.with(PackImageDetailsActivity.this)
                            .load(LSConstant.image_uri + packDetails.getStickersList().get(0).getImage())
                            .into(viewBinding.detailsImg);
                }


            }
        });


        viewBinding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition < stickerPackNumber - 1) {
                    plusPosition = currentPosition + 1;

                    Glide.with(PackImageDetailsActivity.this)
                            .load(LSConstant.image_uri + packDetails.getStickersList().get(plusPosition).getImage())
                            .into(viewBinding.detailsImg);

                    currentPosition = plusPosition;

                } else {
                    Glide.with(PackImageDetailsActivity.this)
                            .load(LSConstant.image_uri + packDetails.getStickersList().get(stickerPackNumber - 1).getImage())
                            .into(viewBinding.detailsImg);
                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LSEventUtil.logToAdd2WSP(packDetails.getId(), packDetails.getTitle());

                if (SPStaticUtils.getBoolean("isFinishScore", false)) {
                    if (LSMKVUtil.getBoolean("loadad", true)) {
                        showRewardDialog();

                    } else {
                        addStickerPackToWhatsApp(packDetails.getIdentifier(), packDetails.getTitle());
                    }
                } else {
                    addStickerPackToWhatsApp(packDetails.getIdentifier(), packDetails.getTitle());
                }

            }
        });

        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LSConstant.PackImageDetailsBack = true;
                finish();
            }
        });
    }

    private void showRewardDialog() {  //间隔一次出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            int rewarDinter = LSMKVUtil.getInt("rewardinter", 0);
            rewardInterval = LSMKVUtil.getInt("rewardInterval", 0);

            if (rewardInterval % (rewarDinter + 1) == 0) {
                new AlertDialog.Builder(this)
                        .setMessage("Watch a short video to unlock this content")
                        .setPositiveButton("Watch ", (dialog, which) -> {

                            showProgressDialog();
                            MaxADManager.tryShowRewardAd(this);


                        }).setNegativeButton("cancel", (dialog, which) -> {

                }).setCancelable(false).show();

            } else {  // 不弹激励广告
                addStickerPackToWhatsApp(packDetails.getIdentifier(), packDetails.getTitle());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void dataObserver() {
    }

    private void AddSendStatus() {
        //弹窗出现外部为阴影
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.5f;
        getWindow().setAttributes(attributes);

        //PopupWindow
        addSendPopupWindow = new PopupWindow();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuview = inflater.inflate(R.layout.item_send_status, null);
        popupWindowImg = menuview.findViewById(R.id.send_logo);
        popupWindowHeadline = menuview.findViewById(R.id.status_text);
        popupWindowSubtitle = menuview.findViewById(R.id.subtitle);

        popupWindowImg.setImageResource(R.drawable.preparing);
        popupWindowHeadline.setText("Preparing Pack");
        popupWindowSubtitle.setText("Pack is ready soon…");

        addSendPopupWindow.setContentView(menuview);
        addSendPopupWindow.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        addSendPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        addSendPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindows_bg));

        //设置弹窗位置
        addSendPopupWindow.showAtLocation(PackImageDetailsActivity.this.findViewById(R.id.pack_image_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //弹窗取消监听 取消之后恢复阴影
        addSendPopupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams attributes1 = getWindow().getAttributes();
            attributes1.alpha = 1;
            getWindow().setAttributes(attributes1);
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //拦截弹窗外部点击事件
        if (addSendPopupWindow != null && addSendPopupWindow.isShowing()) {
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void DownloadImages() {
        List<String> stickersImg = new ArrayList();

        popupWindowImg.setImageResource(R.drawable.poppup_window_rotating_wheel);
        popupWindowHeadline.setText("Connecting WhatsApp");
        popupWindowSubtitle.setText("The pack in preparation…");

        StickersManager.downloadStickers(packDetails, new StickersCallBack() {
            @Override
            public void completed(int complete, int failed, int all) {
                Log.e("StickersManager", "downloadStickers 完成:" + complete + " 失败:" + failed + " 共:" + all);
                if (failed > 0) { //如果有下载失败弹出提示

                    return;
                }

                if (complete == all) {
                    //下载成功数据缓存
                    StickersManager.putStickers();
                    LSEventUtil.logToPackDownloadComplete(packDetails.getId(), packDetails.getTitle());
                }else {
                    LSEventUtil.logToPackDownloadFailed(packDetails.getId(), packDetails.getTitle());
                }

                Message msg = new Message();
                msg.what = 0;
                msg.obj = packDetails;
                msg.arg1 = complete;
                msg.arg2 = all;
                handler.sendMessage(msg);
            }
        });


    }


    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            popupWindowSubtitle.setText("The pack in preparation… " + msg.arg1 + "/" + msg.arg2);

            if (msg.arg1 == msg.arg2) {//如果下载完成
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    popupWindowImg.setImageResource(R.drawable.connection);
                    popupWindowHeadline.setText("Connection Succeeded");
                    popupWindowSubtitle.setText("Almost completed…");
                }, 2000);

                if (stickerPackWhitelistedInWhatsAppConsumer) {
                    addSendPopupWindow.dismiss();
                }

                if (SPStaticUtils.getBoolean("isFinishScore", false)) {
                    rewardInterval = rewardInterval + 1;
                    LSMKVUtil.put("rewardInterval", rewardInterval);
                }


//                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
//                    launchIntentToAddPackToChooser(packDetails.getIdentifier(), packDetails.getTitle());
//
//                } else
                if (!stickerPackWhitelistedInWhatsAppConsumer) {
                    launchIntentToAddPackToSpecificPackage(packDetails.getIdentifier(), packDetails.getTitle(), WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
                } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToSpecificPackage(packDetails.getIdentifier(), packDetails.getTitle(), WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
                } else {
                    Toast.makeText(this, R.string.not_whitelisted, Toast.LENGTH_LONG).show();
                }
            }
        }
        return false;
    });


    protected void addStickerPackToWhatsApp(String identifier, String stickerPackName) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(getPackageManager()) && !WhitelistCheck.isWhatsAppSmbAppInstalled(getPackageManager())) {
                Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
                return;
            }
            stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier);
            stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier);
//            if (getFileSize(file) == 0 && getFileSize(fileTray) == 0){
            AddSendStatus();
//            }

            DownloadImages();

        } catch (Exception e) {
            Log.e("###", "error adding sticker pack to WhatsApp" + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            popupWindowImg.setImageResource(R.drawable.finish_add);
            popupWindowHeadline.setText("Add to WhatsApp");
            popupWindowSubtitle.setText("Done！");
        }, 2000);
        addSendPopupWindow.dismiss();

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);
        try {
            startActivityForResult(intent, LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_add_package, Toast.LENGTH_LONG).show();
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private void launchIntentToAddPackToChooser(String identifier, String stickerPackName) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            popupWindowImg.setImageResource(R.drawable.finish_add);
            popupWindowHeadline.setText("Add to WhatsApp");
            popupWindowSubtitle.setText("Done！");
        }, 2000);
        addSendPopupWindow.dismiss();

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_add_package_selector, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LSConstant.ADD_PACK && resultCode == Activity.RESULT_OK) {
            InvokesData.getInvokesData().insertPackData(
                    new SaveData(packDetails.getId(), gson.toJson(packDetails)));

            viewBinding.textView2.setText(R.string.added_to_whatsApp);
            viewBinding.sendButton.setEnabled(false);

            LSEventUtil.logToPackAddSuccess(packDetails.getId(), packDetails.getTitle());

            RateController.getInstance().tryRateFinish(PackImageDetailsActivity.this, new RateDialog.RatingClickListener() {

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

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    viewBinding.textView2.setText(R.string.add_to_whatsapp);
                    viewBinding.sendButton.setEnabled(true);

                    final String validationError = data.getStringExtra("validation_error");
                    if (validationError != null) {
                        if (BuildConfig.DEBUG) {
                            //validation error should be shown to developer only, not users.
                            MessageDialogFragment.newInstance(R.string.title_validation_error, validationError).show(getSupportFragmentManager(), "validation error");
                        }
                        Log.e("###", "Validation failed:" + validationError);
                    }
                } else {
                    new AddStickerPackActivity.StickerPackNotAddedMessageFragment().show(getSupportFragmentManager(), "sticker_pack_not_added");
                }
            }
        }
    }


}