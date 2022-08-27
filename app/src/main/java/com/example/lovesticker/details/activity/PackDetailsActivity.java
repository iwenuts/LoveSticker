package com.example.lovesticker.details.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackDetailsBinding;
import com.example.lovesticker.details.adapter.PackDetailsAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
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
import com.orhanobut.hawk.Hawk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
    private int rewardInterval = 0;

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;
    private AlertDialog alertDialog;
    private boolean stickerPackWhitelistedInWhatsAppConsumer;
    private boolean stickerPackWhitelistedInWhatsAppSmb;
    private File file;
    private File fileTray;
    private File myTray;

    @Override
    protected void initView() {
        Log.e("###", "PackDetails onCreate: ");
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        MaxADManager.loadInterstitialDetailAd(this);
        LSMKVUtil.put("PackDetailsInterstitialAd", true);


        if (LSMKVUtil.getBoolean("PackInterstitialAd", false) &&
                LSMKVUtil.getBoolean("loadad", true)) {
            MaxADManager.tryShowInterstitialDetailAd(this);
            LSMKVUtil.put("PackInterstitialAd", false);
        }


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
                sticker.add(new Sticker(name.substring(name.lastIndexOf("/")+1), emoji));
//                Log.e("###", "name: "+  name.substring(name.lastIndexOf("/")+1));
            }
        }

        stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, stickerPacks.getIdentifier());
        stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, stickerPacks.getIdentifier());

        if (!InvokesData.getInvokesData(PackDetailsActivity.this).querySavePackGson(stickerPacks.getId())
                && stickerPackWhitelistedInWhatsAppConsumer) {  //有数据
            viewBinding.sendText.setText(R.string.added_to_whatsApp);
            viewBinding.sendButton.setEnabled(false);
        }else {
            if (stickerPackWhitelistedInWhatsAppConsumer){  //已经添加到whatsApp
                viewBinding.sendText.setText(R.string.add_to_whatsapp);
                viewBinding.sendButton.setEnabled(true);

                //添加进收藏
                InvokesData.getInvokesData(PackDetailsActivity.this).insertPackData(
                        new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));
            }else {
                viewBinding.sendText.setText(R.string.add_to_whatsapp);
                viewBinding.sendButton.setEnabled(true);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("PackImageDetailsBackAd", false) &&
                LSMKVUtil.getBoolean("loadad", true)) {
            MaxADManager.tryShowInterstitialBackAd(this);
            LSMKVUtil.put("PackImageDetailsBackAd", false);
        }
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

                if (LSMKVUtil.getBoolean("loadad", true)) {
                    rewardInterval = rewardInterval + 1;
                    showRewardDialog(rewardInterval);

                } else {
                    addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                }

                viewBinding.sendText.setText(R.string.added_to_whatsApp);
                viewBinding.sendButton.setEnabled(false);

            }
        });

        MaxADManager.loadInterstitialBackAd(this);
        LSMKVUtil.put("PackDetailsBackAd", true);
        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void showRewardDialog(int intent) {  //间隔出现激励弹窗 ex:第一次出现，第二次不出现......
        try {
            int rewarDinter = LSMKVUtil.getInt("rewardinter", 1);
//            Log.e("###", "rewardinter: " + rewarDinter);

            if (intent == 1) {
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
                                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                                    }

                                    @Override
                                    public void onTimeOut() {
                                        dismissProgressDialog();
                                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                                    }
                                });
                            } catch (Exception e) {
                            }

                        }).setCancelable(false).show();

            } else if (intent % (rewarDinter + 1) != 1) { // 不弹激励广告
                addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());

            } else { // 弹激励广告

                alertDialog = new AlertDialog.Builder(this)
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
                                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                                    }

                                    @Override
                                    public void onTimeOut() {
                                        dismissProgressDialog();
                                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }).setCancelable(false).show();
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

        StickersManager.downloadStickers(stickerPacks, new StickersCallBack() {
            @Override
            public void completed(int complete, int failed, int all) {
                Log.e("StickersManager", "downloadStickers 完成:" + complete + " 失败:" + failed + " 共:" + all);
                if (failed > 0){ //如果有下载失败弹出提示

                    return;
                }

                if (complete == all) {
                    //下载成功数据缓存
                    StickersManager.putStickers();
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
            popupWindowSubtitle.setText("The pack in preparation… "+msg.arg1+"/"+msg.arg2);

            if (msg.arg1 == msg.arg2){//如果下载完成
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    popupWindowImg.setImageResource(R.drawable.connection);
                    popupWindowHeadline.setText("Connection Succeeded");
                    popupWindowSubtitle.setText("Almost completed…");
                }, 1000);

                if (stickerPackWhitelistedInWhatsAppConsumer){
                    addSendPopupWindow.dismiss();
                }

                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToChooser(stickerPacks.getIdentifier(), stickerPacks.getTitle());

                } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
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

                            if (!InvokesData.getInvokesData(PackDetailsActivity.this).querySavePackGson(stickerPacks.getId())) {
//                        Log.e("###", "delete" );
                                InvokesData.getInvokesData(PackDetailsActivity.this).deleteSavePacks(gson.toJson(stickerPacks));
                            }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to return?");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("true", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.show();
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
            if (getFileSize(file) == 0 && getFileSize(fileTray) == 0){
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
 InvokesData.getInvokesData(PackDetailsActivity.this).insertPackData(
                    new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));

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
        if (requestCode == LSConstant.ADD_PACK) {
            //添加进收藏
            if (InvokesData.getInvokesData(PackDetailsActivity.this).querySavePackGson(stickerPacks.getId())) {
                InvokesData.getInvokesData(PackDetailsActivity.this).insertPackData(
                        new SaveData(stickerPacks.getId(), gson.toJson(stickerPacks)));
            }

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
            }); //评分

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
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
                    }); //评分

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