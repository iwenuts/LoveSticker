package com.example.lovesticker.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.SaveData;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.List;

public class LoveStickerApp extends Application {

    private static Context applicationContext;
    public static Context getAppContext() {
        return applicationContext;
    }

    private static Application application;
    public static Application getApplication() {
        return application;
    }


//    private static SaveData saveData;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        applicationContext = getApplicationContext();

        // mkv init
        LSMKVUtil.initMKV(applicationContext);

//        MaxADManager.enableDebugMaxAd(this);

        MaxADManager.initMaxAd(this);

        // app event upload init
        LSEventUtil.init(applicationContext);

        // logger preset
        initLogger();

        new FlurryAgent.Builder()
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, "5QZSQGBGTY9TPNPV7BV9");

        FileDownloader.setup(application);
    }


    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(3)
                .methodOffset(1)
                .tag("LoveSticker")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }



}
