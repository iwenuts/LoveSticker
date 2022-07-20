package com.example.lovesticker.details.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityAnimationDetailsBinding;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.hawk.Hawk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class AnimationDetailsActivity extends BaseActivity<BaseViewModel, ActivityAnimationDetailsBinding> {
    private String detailsImage;
    private HandlerThread mHandlerThread = new HandlerThread("mHandlerThread");
    private Handler mHandlerInHandlerThread;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        detailsImage = getIntent().getStringExtra("detailsImage");

        if (detailsImage != null){
            Log.e("###", "detailsImage: " + detailsImage);
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + detailsImage)
                    .into(viewBinding.detailsImg);
        }

    }

    @Override
    protected void initClickListener() {

        viewBinding.isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LSMKVUtil.getBoolean("animationDetailsCollected",false)){  //收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.collected);
                    LSMKVUtil.put("animationDetailsCollected",true);


                }else {  //未收藏
                    viewBinding.isCollected.setBackgroundResource(R.drawable.not_collected_bg);
                    viewBinding.collectedImage.setImageResource(R.drawable.not_collected);
                    LSMKVUtil.put("animationDetailsCollected",false);


                }

            }
        });


        viewBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailsImage != null){
                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + detailsImage);

                }


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
//            public void onClick(View v) {  //todo 网上找的分享到whatsapp
//
//                Uri uri = Uri.parse(LSConstant.image_gif_uri + detailsImage);
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.setPackage("com.whatsapp");
//                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
//                shareIntent.setType("image/*");
//                startActivity(shareIntent);
//            }
//        });



    }

    @Override
    protected void dataObserver() {

    }

    private void saveLocal(String imgAddress){

        new Thread(new Runnable() {
            @Override
            public void run() {
                File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
                if (!imgFile.exists()){
                    imgFile.mkdirs();
                }
                File file = new File(imgFile.getAbsolutePath() + File.separator + detailsImage);

                byte[] b = new byte[1024];
                try {
                    URL url = new URL(imgAddress);
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
                    Log.e("###", "e: " +e.getMessage());
                    System.exit(1);
                }

                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            shareAny(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker" + File.separator + detailsImage);
            dismissProgressDialog();
        }
        return false;
    });




    private void saveLocal2(Uri uri){
        File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker");
        if (!imgFile.exists()){
            imgFile.mkdirs();
        }

        try {
            File file = new File(imgFile.getAbsolutePath() + File.separator + detailsImage);
            InputStream inputStream =  getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = inputStream.read(buffer))){
                fileOutputStream.write(buffer,0,byteRead);
            }
            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("###", "e: " + e.getMessage());
        }
    }

}