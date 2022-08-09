package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.util.FileUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackDetailsBinding;
import com.example.lovesticker.details.adapter.PackDetailsAdapter;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.StickerContentProvider;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;
import com.tencent.mmkv.MMKV;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static com.unity3d.services.core.properties.ClientProperties.getActivity;

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


//        if (!LSMKVUtil.getBoolean("isStickerClear",false)){
//            sticker.clear();
//        }

        if (stickerPackNumber != 0) {
            ArrayList<String> emoji = new ArrayList<>();
            emoji.add("");
            for (int i = 0; i < stickerPackNumber; i++) {
                sticker.add(new Sticker(stickerPacks.getStickersList().get(i).getImage(), emoji));
            }
//            LSMKVUtil.put("isStickerClear",false);
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
    protected void initClickListener() {

        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AddStickerPackActivity.addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());

//                if (LSMKVUtil.getBoolean("loadad", true)) {
//                    rewardInterval = rewardInterval + 1;
//                    showRewardDialog(rewardInterval);
//
//                } else {
//                    addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
//                }

                addStickerPackToWhatsApp(stickerPacks.getIdentifier(), stickerPacks.getTitle());
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
//        popupWindowImg.setImageResource(R.drawable.concenting);
//        popupWindowHeadline.setText("Concenting WhatsApp");
//        popupWindowSubtitle.setText("The pack in preparation…");
        for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
            stickersImg.add(LSConstant.image_uri + stickerPacks.getStickersList().get(i).getImage());
        }

        if (stickersImg != null) {

            new Thread(() -> {
                try {
                    String[] trayImageName = stickerPacks.getTrayImageFile().split("/");
                    String trayImage = LSConstant.image_uri + stickerPacks.getTrayImageFile();

//                    url2bitmap(trayImage, PackDetailsActivity.this, 1002, trayImageName[2]);
                    File myTrayr = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
                    if (!myTrayr.exists()) {
                        myTrayr.mkdirs();
                    }

                    fileTray = new File(myTrayr, trayImageName[2]);
//                    Log.e("###", "fileTraySize: " + getFileSize(fileTray));
                    fileStorage(fileTray, trayImage);

                    for (int i = 0; i < stickersImg.size()/4; i++) {
                        String imageName = stickerPacks.getStickersList().get(i).getImage();
                        String[] srs = imageName.split("/");


                        file = new File(myTrayr, srs[2]);
//                        Log.e("###", "stickerPacksSize: " + getFileSize(file));
                        fileStorage(file, stickersImg.get(i));

                    }


//                          File myDir = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
//                            if (!myDir.exists()) {
//                                myDir.mkdirs();
//                            }
//                            Log.e("###", "srs[0]: " + srs[0] + " " + "srs[1]" + srs[1] + "srs[2]" + srs[2]);
//                            file = new File(myDir.getAbsolutePath() + File.separator + srs[2]);

//                        url2bitmap(stickersImg.get(i), PackDetailsActivity.this, 1002, srs[2]);
//                        File myDir = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
//                        if (!myDir.exists()) {
//                            myDir.mkdirs();
//                        }
//                        Log.e("###", "file1: " + file);


//                        byte[] b = new byte[1024];
//                        String trayImage = LSConstant.image_uri + stickerPacks.getTrayImageFile();
//                        file = new File(myDir.getAbsolutePath() + File.separator + "trayImage");
//                        Log.e("###", "file: " + file);
//                        URL url = new URL(trayImage);
//                        URLConnection urlConnection = url.openConnection();
//                        urlConnection.connect();
//                        DataInputStream di = new DataInputStream(urlConnection.getInputStream());
//                        // output
//                        FileOutputStream fo = new FileOutputStream(file);
//                        // copy the actual file
//                        // (it would better to use a buffer bigger than this)
//                        while (-1 != di.read(b, 0, 1))
//                            fo.write(b, 0, 1);
//                        di.close();
//                        fo.close();
                } catch (Exception e) {
                    e.getMessage();
                }

                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }).start();
        }
    }

    private void fileStorage(File file, String data) {
//        if (file.exists() && getFileSize(file)>0) {
//            return;
//        }
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
            Log.e("###", "file: " + file);
            Log.e("###", "getFileSize: " + getFileSize(file));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("###", "e: " + e.getMessage());
            System.exit(1);
        }
    }


    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            String[] trayImage = stickerPacks.getTrayImageFile().split("/");
            Log.e("###", "trayImage :" + trayImage[2]);
            String pngImage = trayImage[2].replace(".webp", ".png");

            stickerPack = new StickerPack(stickerPacks.getIdentifier(), stickerPacks.getTitle(),
                    stickerPacks.getPublisher(), trayImage[2], "",
                    stickerPacks.getPublisherWebsite(), stickerPacks.getPrivacyPolicyWebsite(),
                    stickerPacks.getLicenseAgreementWebsite(), "1", false,
                    false, sticker);
//            Log.e("###", "stickerPack: " + stickerPack);

            List<StickerPack> packs = new ArrayList<>();
            packs.add(stickerPack);
//            stickerPack.setStickers(sticker);

            Hawk.put("stickerPack", stickerPack);

            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                //ask users which app to add the pack to.
//                new Handler(Looper.getMainLooper()).postDelayed(this::AddSendStatus, 1000);


//                new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                    popupWindowImg.setImageResource(R.drawable.connection);
//                    popupWindowHeadline.setText("Connection Succeeded");
//                    popupWindowSubtitle.setText("Almost completed…");
//                }, 1000);
                launchIntentToAddPackToChooser(stickerPacks.getIdentifier(), stickerPacks.getTitle());

            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
            } else {
                Toast.makeText(this, R.string.not_whitelisted, Toast.LENGTH_LONG).show();
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
        addSendPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
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

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
            stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier);
            stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier);


            DownloadImages();

        } catch (Exception e) {
            Log.e("###", "error adding sticker pack to WhatsApp", e);
            Toast.makeText(this, R.string.error_adding, Toast.LENGTH_LONG).show();
        }

    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            popupWindowImg.setImageResource(R.drawable.finish_add);
//            popupWindowHeadline.setText("Add to WhatsApp");
//            popupWindowSubtitle.setText("Done！");
//        }, 1000);

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
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            popupWindowImg.setImageResource(R.drawable.finish_add);
//            popupWindowHeadline.setText("Add to WhatsApp");
//            popupWindowSubtitle.setText("Done！");
//        }, 1000);

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_add_package_selector, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
//        addSendPopupWindow.dismiss();
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

//    public void url2bitmap(String imgPath, Context context, int savePathType, String fileName) {
//
//        HttpURLConnection conn = null;
//        InputStream is = null;
//        try {
//            URL url = new URL(imgPath);
//            //开启连接
//            conn = (HttpURLConnection) url.openConnection();
//            //设置连接超时
//            conn.setConnectTimeout(5000);
//            //设置请求方式
//            conn.setRequestMethod("GET");
//            //conn.connect();
//            if (conn.getResponseCode() == 200) {
//                is = conn.getInputStream();
//                Bitmap b = BitmapFactory.decodeStream(is);
//                if (b != null) {
//                    //saveImageToGallery(context, b);
//                    if (savePathType == 1001) {
////                        addBitmapToAlbum(context, b, fileName, "png", Bitmap.CompressFormat.PNG);
//                        saveImageToTrayImage(context, b, fileName);
//                    } else if (savePathType == 1002) {
//                        saveImageToGallery(context, b, fileName);
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                //用完记得关闭
//                is.close();
//                conn.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public static void addBitmapToAlbum(Context context, Bitmap bitmap, String displayName, String mimeType, Bitmap.CompressFormat compressFormat) {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
//        } else {
//            values.put(MediaStore.MediaColumns.DATA, Environment.getExternalStorageDirectory().getPath() + "/"
//                    + Environment.DIRECTORY_DCIM + "/" + displayName);
//        }
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        if (uri != null) {
//            OutputStream outputStream = null;
//            try {
//                outputStream = resolver.openOutputStream(uri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (outputStream != null) {
//                bitmap.compress(compressFormat, 100, outputStream);
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void saveImageToGallery(Context context, Bitmap bmp, String fileName) {
//        // 首先保存图片
//        File appDir = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }
//
////        file = new File(appDir, fileName);
//
////        Log.e("###", "isFileExists: "+ !FileUtils.isFileExists(file));
//
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.WEBP, 90, fos);
//            fos.flush();
//            fos.close();
//            Log.e("###", "getFileSize: " + getFileSize(file));
//            Log.e("###", "fileTrayImage: " + file.getAbsolutePath());
//        } catch (Exception e) {
//            Log.e("###", "saveImageToGallery: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void saveImageToTrayImage(Context context, Bitmap bmp, String fileName) {
//        File appDir = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }
//        fileTrayImage = new File(appDir, fileName);
//
//        Log.e("###", "fileTrayImage: " + fileTrayImage.getAbsolutePath());
//
//        try {
//            FileOutputStream fos = new FileOutputStream(fileTrayImage);
//            bmp.compress(Bitmap.CompressFormat.WEBP, 90, fos);
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            Log.e("###", "saveImageToTrayImage: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
                Log.d("###", "获取文件大小不存在!");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return size;
    }

}