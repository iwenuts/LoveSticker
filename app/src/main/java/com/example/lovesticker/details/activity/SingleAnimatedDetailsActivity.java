package com.example.lovesticker.details.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivitySingleAnimatedDetailsBinding;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.gyf.immersionbar.ImmersionBar;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class SingleAnimatedDetailsActivity extends BaseActivity<BaseViewModel, ActivitySingleAnimatedDetailsBinding> {
    private String singleAnimatedDetailsImage;


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        singleAnimatedDetailsImage = getIntent().getStringExtra("singleAnimatedDetailsImage");
        
        if (singleAnimatedDetailsImage != null){
            Glide.with(this)
                    .load(LSConstant.image_gif_uri + singleAnimatedDetailsImage)
                    .into(viewBinding.singleDetailsImg);
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
                if (singleAnimatedDetailsImage != null){
                    showProgressDialog();
                    saveLocal(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
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
//            public void onClick(View v) {
//                Uri uri = Uri.parse(LSConstant.image_gif_uri + singleAnimatedDetailsImage);
//                shareAny(uri);
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
                File file = new File(imgFile.getAbsolutePath() + File.separator + singleAnimatedDetailsImage);

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
            shareAny(getExternalFilesDir(null).getAbsolutePath() + File.separator + "sticker" + File.separator + singleAnimatedDetailsImage);
            dismissProgressDialog();
        }
        return false;
    });






}