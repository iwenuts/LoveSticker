package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityPackImageDetailsBinding;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class PackImageDetailsActivity extends BaseActivity<BaseViewModel, ActivityPackImageDetailsBinding> {
    private StickerPacks packDetails;
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();

    private int imagePosition;
    private int currentPosition; //当前图片所在位置
    private int stickerPackNumber; //图片总数
    private int minusPosition; //上一张图
    private int plusPosition; //下一张图


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        packDetails = (StickerPacks) getIntent().getSerializableExtra("packDetails_value");
        imagePosition = getIntent().getIntExtra("position", 0);
        stickerPackNumber = getIntent().getIntExtra("stickerPackNumber", 0);
        Log.e("###", "PackImageDetailsNumber: " + stickerPackNumber);

        if (packDetails != null && stickerPackNumber != 0) {
            currentPosition = imagePosition;

            Glide.with(this)
                    .load(LSConstant.image_uri + packDetails.getStickersList().get(imagePosition).getImage())
                    .into(viewBinding.detailsImg);

            viewBinding.stickerTitle.setText(packDetails.getTitle());


            if (LSMKVUtil.getBoolean("IsStickerDetailsClear",false)){
                sticker.clear();
            }

            for (int i = 0; i < stickerPackNumber; i++) {
                sticker.add(new Sticker(packDetails.getStickersList().get(i).getImage(), new ArrayList<>()));
            }

            LSMKVUtil.put("IsStickerDetailsClear", false);
        }
    }



    @Override
    protected void initClickListener() {

            viewBinding.previousPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPosition > 0){
                        minusPosition = currentPosition -1;

                        Glide.with(PackImageDetailsActivity.this)
                                .load(LSConstant.image_uri + packDetails.getStickersList().get(minusPosition).getImage())
                                .into(viewBinding.detailsImg);

                        currentPosition = minusPosition;
                    }else{
                        Glide.with(PackImageDetailsActivity.this)
                                .load(LSConstant.image_uri + packDetails.getStickersList().get(0).getImage())
                                .into(viewBinding.detailsImg);
                    }


                }
            });


            viewBinding.nextPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPosition < stickerPackNumber -1){
                        plusPosition = currentPosition +1;

                        Glide.with(PackImageDetailsActivity.this)
                                .load(LSConstant.image_uri + packDetails.getStickersList().get(plusPosition).getImage())
                                .into(viewBinding.detailsImg);

                        currentPosition = plusPosition;

                    }else {
                        Glide.with(PackImageDetailsActivity.this)
                                .load(LSConstant.image_uri + packDetails.getStickersList().get(stickerPackNumber -1).getImage())
                                .into(viewBinding.detailsImg);
                    }

                }
            });



        viewBinding.isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LSMKVUtil.getBoolean("collected",false)){  //收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);
                    LSMKVUtil.put("collected",true);



                }else {  //未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);
                    LSMKVUtil.put("collected",false);



                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stickerPack = new StickerPack(packDetails.getIdentifier(),packDetails.getTitle(),packDetails.getTrayImageFile(),
                        packDetails.getTrayImageFile(),"",packDetails.getPublisherWebsite(),packDetails.getPrivacyPolicyWebsite(),
                        packDetails.getLicenseAgreementWebsite(),"",false,false,sticker);

                Log.e("###", "PackImageDetailsStickerPack: " + stickerPack);
                Hawk.put("stickerPack",stickerPack);

                addStickerPackToWhatsApp(packDetails.getIdentifier(),packDetails.getTitle());
            }
        });



        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        viewBinding.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse(LSConstant.image_uri + packDetails.getStickersList().get(currentPosition).getImage());
//                shareAny(uri);
//            }
//        });




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