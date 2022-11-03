package com.example.lovesticker.util.event;

import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.BuildConfig;
//import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;
import java.util.Map;

public class LSEventUtil {
    private static Context applicationContext;
    private static FirebaseAnalytics mFirebaseAnalytics = null;

    public static void init(Context appContext) {
        applicationContext = appContext;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext);

    }

    public static void logInstallFrom(String from) {
        HashMap<String,String> value = new HashMap<>();
        value.put("from",from);
        logEventLS("InstallFrom",value);
    }



    private static void logEventLS(String key, HashMap<String,String> value) {
        Bundle bundle = new Bundle();
        HashMap<String,Object> umValue = new HashMap<>();
        for (Map.Entry<String,String> v : value.entrySet()){
            bundle.putString(v.getKey(), v.getValue());
            umValue.put(v.getKey(),v.getValue());
        }
        if(umValue.isEmpty()) {
            umValue.put("x","x");
        }
//        if(mFirebaseAnalytics != null)
//            mFirebaseAnalytics.logEvent(key, bundle);
        MobclickAgent.onEventObject(applicationContext, key, umValue);
    }

    public static void sendRevenue(Bundle bundle) {
        try {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);
        } catch (Exception e) {

        }
    }

    public static void logAdEvent(String key) {
        logEventLS(key);
    }

    private static void logEventLS(String key) {
        if(mFirebaseAnalytics != null)
            mFirebaseAnalytics.logEvent(key, new Bundle());
    }
}
