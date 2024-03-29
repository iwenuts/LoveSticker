package com.example.lovesticker.details.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.SPStaticUtils;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackDetailsBinding;
import com.example.lovesticker.details.adapter.PackDetailsAdapter;
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

public class PackDetailsActivity extends BaseActivity<BaseViewModel, ActivityPackDetailsBinding> {
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();
    private StickerPacks stickerPacks;
    private Boolean isSavedLayout;
    private Integer stickerPackNumber;
    private SelectPicPopupWindow selectPicPopupWindow;
    private Gson gson = new Gson();
    private int rewardInterval;

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;
    private AlertDialog alertDialog;
    private boolean stickerPackWhitelistedInWhatsAppConsumer = false;
    private boolean stickerPackWhitelistedInWhatsAppSmb = false;
    private File file;
    private File fileTray;
    private File myTray;
    private boolean isPopup = false;

    private ActivityPackDetailsBinding viewBinding;


    @Override
    protected void initViewBinding() {
        viewBinding = ActivityPackDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(viewBinding.getRoot());
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        MaxADManager.tryShowInterstitialDetailAd();


        isSavedLayout = getIntent().getBooleanExtra("isSaved", false);

        if (isSavedLayout) {
            stickerPacks = (StickerPacks) getIntent().getSerializableExtra("saveStickerPack");
            stickerPackNumber = getIntent().getIntExtra("saveStickerPackNumber", 0);  //图片总数

            viewBinding.deletePack.setVisibility(View.VISIBLE);

        } else {
            stickerPacks = (StickerPacks) getIntent().getSerializableExtra("stickerPack_value");
            stickerPackNumber = getIntent().getIntExtra("stickerPack_number", 0);  //图片总数

            viewBinding.deletePack.setVisibility(View.GONE);
        }


        if (stickerPacks != null && stickerPackNumber != 0) {
            LSEventUtil.logToClickPack(stickerPacks.getId(), stickerPacks.getTitle());

            viewBinding.packTitle.setText(stickerPacks.getTitle());

//            Log.e("###", "imageitem: " + stickerPacks.getStickersList().get(0).getImage());
            //Adapter
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            viewBinding.packImageDetails.setLayoutManager(manager);
            PackDetailsAdapter adapter = new PackDetailsAdapter(stickerPacks, this, stickerPackNumber);
            viewBinding.packImageDetails.setAdapter(adapter);
        }


        if (stickerPackNumber != 0) {  //添加sticker数据
            sticker.clear();
            ArrayList<String> emoji = new ArrayList<>();
            emoji.add("");
            for (int i = 0; i < stickerPackNumber; i++) {
                String name = stickerPacks.getStickersList().get(i).getImage();
                sticker.add(new Sticker(name.substring(name.lastIndexOf("/") + 1), emoji));
//                Log.e("###", "name: "+  name.substring(name.lastIndexOf("/")+1));
            }
        }

        if (!TextUtils.isEmpty(stickerPacks.getIdentifier())) {
            stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, stickerPacks.getIdentifier());
            stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, stickerPacks.getIdentifier());
        }

        if (!InvokesData.getInvokesData().querySavePackGson(stickerPacks.getId())
                && stickerPackWhitelistedInWhatsAppConsumer) {  //有数据
            viewBinding.sendText.setText(R.string.added_to_whatsApp);
            viewBinding.sendButton.setEnabled(false);
        } else {
            if (stickerPackWhitelistedInWhatsAppConsumer) {  //已经添加到whatsApp
                viewBinding.sendText.setText(R.string.added_to_whatsApp);
                viewBinding.sendButton.setEnabled(false);

                //添加进收藏
                InvokesData.getInvokesData().insertPackData(
                        new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));
            } else {
                viewBinding.sendText.setText(R.string.add_to_whatsapp);
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
                dismissProgressDialog();
                addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
            }

            @Override
            public void onTimeOut() {
                dismissProgressDialog();
                Toast.makeText(PackDetailsActivity.this, "Wow, No Need to watch video this time", Toast.LENGTH_SHORT).show();
                addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StickersManager.stopDownload();
    }

    @Override
    protected void initClickListener() {

        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LSEventUtil.logToAdd2WSP(stickerPacks.getId(), stickerPacks.getTitle());

                if (SPStaticUtils.getBoolean("isFinishScore", false)) {
                    if (LSMKVUtil.getBoolean("loadad", true)) {
                        showRewardDialog();

                    } else {
                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                    }

                } else {
                    addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                }

            }
        });

        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaxADManager.tryShowInterstitialBackAd(PackDetailsActivity.this);
                finish();
            }
        });

        viewBinding.deletePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgs();
            }
        });
    }

    private void showRewardDialog() {  //间隔出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            int rewarDinter = LSMKVUtil.getInt("rewardinter", 0); //间隔
            rewardInterval = LSMKVUtil.getInt("rewardInterval", 0);

            if (rewardInterval % (rewarDinter + 1) == 0) {
                new AlertDialog.Builder(this)
                        .setMessage("Watch a short video to unlock this content")
                        .setPositiveButton("Watch ", (dialog, which) -> {
                            try {
                                showProgressDialog();
                                MaxADManager.tryShowRewardAd(this);
                            } catch (Exception e) {
                            }

                        }).setNegativeButton("cancel", (dialog, which) -> {


                }).setCancelable(false).show();

            } else {  // 不弹激励广告
                addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void DownloadImages() {
        List<String> stickersImg = new ArrayList();
        popupWindowImg.setImageResource(R.drawable.poppup_window_rotating_wheel);
        popupWindowHeadline.setText("Connecting WhatsApp");
        popupWindowSubtitle.setText("The pack in preparation…");

        isPopup = true;

        StickersManager.downloadStickers(stickerPacks, new StickersCallBack() {
            @Override
            public void completed(int complete, int failed, int all) {
                Log.e("StickersManager", "downloadStickers 完成:" + complete + " 失败:" + failed + " 共:" + all);
                if (failed > 0) { //如果有下载失败弹出提示

                    return;
                }

                if (complete == all) {
                    //下载成功数据缓存
                    StickersManager.putStickers();
                    LSEventUtil.logToPackDownloadComplete(stickerPacks.getId(), stickerPacks.getTitle());
                } else {
                    LSEventUtil.logToPackDownloadFailed(stickerPacks.getId(), stickerPacks.getTitle());
                }

                Message msg = new Message();
                msg.what = 0;
                msg.obj = stickerPacks;
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
                }, 1000);

                if (stickerPackWhitelistedInWhatsAppConsumer) {
                    addSendPopupWindow.dismiss();
                }
                isPopup = false;


                if (SPStaticUtils.getBoolean("isFinishScore", false)) {
                    rewardInterval = rewardInterval + 1;
                    LSMKVUtil.put("rewardInterval", rewardInterval);
                }


//                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
//                    launchIntentToAddPackToChooser(stickerPacks.getIdentifier(), stickerPacks.getTitle());
//
//                } else
                if (!stickerPackWhitelistedInWhatsAppConsumer) {
                    launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
                } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
                } else {
                    Toast.makeText(this, R.string.not_whitelisted, Toast.LENGTH_LONG).show();
                }
            }
        }
        return false;
    });

    private void selectImgs() {
        //弹窗出现外部为阴影
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.5f;
        getWindow().setAttributes(attributes);

        selectPicPopupWindow = new SelectPicPopupWindow(PackDetailsActivity.this, itemsOnClick);
        //设置弹窗位置
        selectPicPopupWindow.showAtLocation(PackDetailsActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        //弹窗取消监听 取消之后恢复阴影
        selectPicPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 1;
                getWindow().setAttributes(attributes);
            }
        });
    }

    View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPicPopupWindow.dismiss();
            if (v.getId() == R.id.delete) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PackDetailsActivity.this)
                        .setTitle("Delete this pack？")
                        .setMessage("If you detele this sticker pack，it will also be deleted  from WhatsApp.")
                        .setCancelable(false)
                        .setPositiveButton("DELETE", (dialog, which) -> {

                            if (!InvokesData.getInvokesData().querySavePackGson(stickerPacks.getId())) {
                                InvokesData.getInvokesData().deleteSavePacks(gson.toJson(stickerPacks));
                            }
                            LSConstant.isDeletePack = true;
                            finish();
                        }).setNegativeButton("CANCEL", (dialog, which) -> {
                            dialog.dismiss();
                        });
                alertDialog.show();
            }
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
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
        addSendPopupWindow.showAtLocation(PackDetailsActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        if (isPopup) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit the progress?");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MaxADManager.tryShowInterstitialBackAd(PackDetailsActivity.this);
                    finish();
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            MaxADManager.tryShowInterstitialBackAd(PackDetailsActivity.this);
            finish();
        }
    }


    @Override
    protected void dataObserver() {

    }

    protected void addStickerPackToWhatsApp(String identifier, String stickerPackName) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(getPackageManager()) && !WhitelistCheck.isWhatsAppSmbAppInstalled(getPackageManager())) {
                Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
                return;
            }
            if (getFileSize(file) == 0 && getFileSize(fileTray) == 0) {
                AddSendStatus();
            }

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
        }, 1000);
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
        }, 1000);
        addSendPopupWindow.dismiss();

//        InvokesData.getInvokesData().insertPackData(
//                new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));

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
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK"); //跳转whatsapp
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LSConstant.ADD_PACK && resultCode == Activity.RESULT_OK) {
            //添加进收藏
            if (InvokesData.getInvokesData().querySavePackGson(stickerPacks.getId())) {
                InvokesData.getInvokesData().insertPackData(
                        new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));

            }

            LSEventUtil.logToPackAddSuccess(stickerPacks.getId(), stickerPacks.getTitle());
            viewBinding.sendText.setText(R.string.added_to_whatsApp);
            viewBinding.sendButton.setEnabled(false);

            RateController.getInstance().tryRateFinish(PackDetailsActivity.this, new RateDialog.RatingClickListener() {

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
            }); //评分

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {

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