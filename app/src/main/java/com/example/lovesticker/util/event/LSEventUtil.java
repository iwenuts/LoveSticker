package com.example.lovesticker.util.event;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

//import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.umeng.analytics.MobclickAgent;

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

    public static void logToClickPack(int id,String title) {
        String event = "clickPackId_" + id + "_title_" + title;
        logEvent(event);
    }

    public static void logToTabStickers() {
        logEvent("tabStickers");
    }

    public static void logToTabPack() {
        logEvent("tabPack");
    }

    public static void logToTabMine() {
        logEvent("tabMine");
    }

    public static void logToViewSticker() {
        logEvent("viewSticker");
    }

    public static void logToAdd2WSP(int id,String title) {
        String event = "add2WSPId_" + id + "_title_" + title;
        logEvent(event);
    }

    public static void logToPackDownloadComplete(int id,String title) {
        String event = "packDownloadCompleteId_" + id  + "_title_" + title;
        logEvent(event);
    }

    public static void logToPackDownloadFailed(int id,String title) {
        String event = "packDownloadFailedId_" + id + "_title_" + title;
        logEvent(event);
    }

    public static void logToPackAddSuccess(int id,String title) {
        String event = "packAddSuccessId_" + id + "_title_" + title;
        logEvent(event);
    }

    public static void logToCategorySwitch(int id,String title) {
        String event = "categorySwitchId_" + id + "_title_" + title;
//        Log.e("###", "logToCategorySwitch: " + event);
        logEvent(event);
    }

    public static void logToClickSticker(int id) {
        String event = "clickStickerId_" + id;
        logEvent(event);
    }

    public static void logToSendSticker(int id) {
        String event = "sendStickerId_" + id;
        logEvent(event);
    }

    public static void logToDownloadStickerComplete(int id) {
        String event = "downloadStickerCompleteId_" + id;
        logEvent(event);
    }

    public static void logToDownloadStickerFailed(int id) {
        String event = "downloadStickerFailedId_" + id;
        logEvent(event);
    }

    public static void logToFavSticker(int id) {
        String event = "favStickerId_" + id;
        logEvent(event);
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

    public static void logEvent(String key) {
        logEventLS(key);
    }

    private static void logEventLS(String key) {
        if(mFirebaseAnalytics != null)
            mFirebaseAnalytics.logEvent(key, new Bundle());
    }
}
