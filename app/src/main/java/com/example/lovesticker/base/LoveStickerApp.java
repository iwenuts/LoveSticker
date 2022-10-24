package com.example.lovesticker.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.example.lovesticker.main.activity.MainActivity;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.event.UpdatePacksEvent;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class LoveStickerApp extends Application implements Application.ActivityLifecycleCallbacks{

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            String packageName = this.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        registerActivityLifecycleCallbacks(this);
        // mkv init
        LSMKVUtil.initMKV(applicationContext);

        MaxADManager.enableDebugMaxAd(this);

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

    private String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) { //后台切回到前台界面"
        Class<?> clazz = activity.getClass();
        EventBus.getDefault().post(new UpdatePacksEvent());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
