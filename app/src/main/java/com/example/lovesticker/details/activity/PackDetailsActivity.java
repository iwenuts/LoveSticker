package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackDetailsBinding;
import com.example.lovesticker.details.adapter.PackDetailsAdapter;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class PackDetailsActivity extends BaseActivity<BaseViewModel, ActivityPackDetailsBinding> {
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();
    private StickerPacks stickerPacks;
    private Boolean isSavedLayout;
    private Integer stickerPackNumber;
    private SelectPicPopupWindow selectPicPopupWindow;
    private Gson gson = new Gson();

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();
        isSavedLayout = getIntent().getBooleanExtra("isSaved",false);

        if (isSavedLayout){
            stickerPacks = (StickerPacks) getIntent().getSerializableExtra("saveStickerPack");
            stickerPackNumber  = getIntent().getIntExtra("saveStickerPackNumber",0);  //图片总数

            viewBinding.deletePack.setVisibility(View.VISIBLE);

        }else {
            stickerPacks = (StickerPacks) getIntent().getSerializableExtra("stickerPack_value");
            stickerPackNumber  = getIntent().getIntExtra("stickerPack_number",0);  //图片总数

            viewBinding.deletePack.setVisibility(View.GONE);
        }



        if (stickerPacks != null && stickerPackNumber != 0){
            viewBinding.packTitle.setText(stickerPacks.getTitle());

            Log.e("###", "imageitem: " + stickerPacks.getStickersList().get(0).getImage());
            //Adapter
            GridLayoutManager manager = new GridLayoutManager(this,2);
            viewBinding.packImageDetails.setLayoutManager(manager);
            PackDetailsAdapter adapter = new PackDetailsAdapter(stickerPacks,this,stickerPackNumber);
            viewBinding.packImageDetails.setAdapter(adapter);
        }



//        if (!LSMKVUtil.getBoolean("isStickerClear",false)){
//            sticker.clear();
//        }

        if (stickerPackNumber != 0){
            for (int i = 0; i < stickerPackNumber; i++){
                sticker.add(new Sticker(stickerPacks.getStickersList().get(i).getImage(), new ArrayList<>()));
            }
//            LSMKVUtil.put("isStickerClear",false);
        }

    }

    @Override
    protected void initClickListener() {

        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AddStickerPackActivity.addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());
                RateController.getInstance().tryRateFinish(PackDetailsActivity.this, new RateDialog.RatingClickListener() {
                    @Override
                    public void onClickFiveStart() {
                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());
                    }

                    @Override
                    public void onClick1To4Start() {
                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());
                    }

                    @Override
                    public void onClickReject() {
                        addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());
                    }
                });


            }
        });


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

    private void DownloadImages() {
        List<String> stickersImg = new ArrayList();

        popupWindowImg.setImageResource(R.drawable.concenting);
        popupWindowHeadline.setText("Concenting WhatsApp");
        popupWindowSubtitle.setText("The pack in preparation…");

        for (int i = 0; i< stickerPacks.getStickersList().size();i++){
            stickersImg.add(LSConstant.image_uri + stickerPacks.getStickersList().get(i).getImage());
        }

        try {
            if (stickersImg != null){
                for (int i = 0; i< stickersImg.size();i++){
                    File myDir = new File(getFilesDir() + "/" + "stickers asset" + "/" + stickerPacks.getIdentifier());
                    if (!myDir.exists()){
                        myDir.mkdirs();
                    }
                    String imageName = stickerPacks.getStickersList().get(i).getImage();
                    File file = new File(myDir,imageName);
                    if (file.exists()) file.delete();

                    URL url = new URL(stickersImg.get(i));
                    InputStream in = url.openStream();
                    FileOutputStream fo = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int length = 0;
                    while ((length = in.read(buf, 0, buf.length)) != -1) {
                        fo.write(buf, 0, length);
                    }
                    in.close();
                    fo.close();
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

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
    private void AddSendStatus(){
        //弹窗出现外部为阴影
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.5f;
        getWindow().setAttributes(attributes);

        //PopupWindow
        addSendPopupWindow = new PopupWindow();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuview = inflater.inflate(R.layout.item_send_status,null);
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




    View.OnClickListener itemsOnClick =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPicPopupWindow.dismiss();
            if (v.getId() == R.id.delete){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PackDetailsActivity.this)
                        .setTitle("Delete this pack？")
                        .setMessage("If you detele this sticker pack，it will also be deleted  from WhatsApp.")
                        .setCancelable(false)
                        .setPositiveButton("DELETE", (dialog, which) -> {

                            if (!InvokesData.getInvokesData(PackDetailsActivity.this).querySavePackGson(stickerPacks.getId())){
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

    public void backgroundAlpha(float bgAlpha)
    {
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
            final boolean stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier);
            final boolean stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier);
            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                //ask users which app to add the pack to.
                new Handler(Looper.getMainLooper()).postDelayed(this::AddSendStatus, 1000);

                DownloadImages();

                stickerPack = new StickerPack(stickerPacks.getIdentifier(),stickerPacks.getTitle(),stickerPacks.getTrayImageFile(),
                        stickerPacks.getTrayImageFile(),"",stickerPacks.getPublisherWebsite(),stickerPacks.getPrivacyPolicyWebsite(),
                        stickerPacks.getLicenseAgreementWebsite(),"",false,false,sticker);
                Log.e("###", "stickerPack: " + stickerPack);

                stickerPack.setStickers(sticker);

                Hawk.put("stickerPack",stickerPack);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    popupWindowImg.setImageResource(R.drawable.connection);
                    popupWindowHeadline.setText("Connection Succeeded");
                    popupWindowSubtitle.setText("Almost completed…");
                }, 1000);


                launchIntentToAddPackToChooser(identifier, stickerPackName);
            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
            } else {
                Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("###", "error adding sticker pack to WhatsApp", e);
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }

    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);
        try {
            startActivityForResult(intent, LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private void launchIntentToAddPackToChooser(String identifier, String stickerPackName) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            popupWindowImg.setImageResource(R.drawable.finish_add);
            popupWindowHeadline.setText("Add to WhatsApp");
            popupWindowSubtitle.setText("Done！");
        }, 1000);

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        addSendPopupWindow.dismiss();
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