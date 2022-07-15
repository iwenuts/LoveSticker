package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.StickerContentProvider;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

public class PackDetailsActivity extends BaseActivity<BaseViewModel, ActivityPackDetailsBinding> {
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();
    private StickerPacks stickerPacks;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        stickerPacks = (StickerPacks) getIntent().getSerializableExtra("stickerPack_value");

        Integer stickerPackNumber  = getIntent().getIntExtra("stickerPack_number",0);  //图片总数

        if (stickerPacks != null && stickerPackNumber != 0){
            viewBinding.packTitle.setText(stickerPacks.getTitle());

            Log.e("###", "imageitem: " + stickerPacks.getStickersList().get(0).getImage());
            //Adapter
            GridLayoutManager manager = new GridLayoutManager(this,2);
            viewBinding.packImageDetails.setLayoutManager(manager);
            PackDetailsAdapter adapter = new PackDetailsAdapter(stickerPacks,this,stickerPackNumber);
            viewBinding.packImageDetails.setAdapter(adapter);
        }

        if (!LSMKVUtil.getBoolean("isStickerClear",false)){
            sticker.clear();
        }

        if (stickerPackNumber != 0){
            for (int i = 0; i < stickerPackNumber; i++){
                sticker.add(new Sticker(stickerPacks.getStickersList().get(i).getImage(), new ArrayList<>()));
            }

            LSMKVUtil.put("isStickerClear",false);
        }


    }

    @Override
    protected void initClickListener() {

        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stickerPack = new StickerPack(stickerPacks.getIdentifier(),stickerPacks.getTitle(),stickerPacks.getTrayImageFile(),
                        stickerPacks.getTrayImageFile(),"",stickerPacks.getPublisherWebsite(),stickerPacks.getPrivacyPolicyWebsite(),
                        stickerPacks.getLicenseAgreementWebsite(),"",false,false,sticker);
                Log.e("###", "stickerPack: " + stickerPack);
                Hawk.put("stickerPack",stickerPack);

                addStickerPackToWhatsApp(stickerPacks.getIdentifier(),stickerPacks.getTitle());


            }
        });


        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
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