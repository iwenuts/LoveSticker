package com.example.lovesticker.base;

import android.app.Application;
import android.content.Context;

import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LoveStickerApp extends Application {

    private static Context applicationContext;
    public static Context getAppContext() {
        return applicationContext;
    }

    private static Application application;
    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        applicationContext = getApplicationContext();

        // mkv init
        LSMKVUtil.initMKV(applicationContext);

        // Hawk
//        Hawk.init(applicationContext).build();

        // app event upload init
        LSEventUtil.init(applicationContext);

        // logger preset
        initLogger();

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
