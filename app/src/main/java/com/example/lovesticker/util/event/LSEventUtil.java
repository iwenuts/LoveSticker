package com.example.lovesticker.util.event;

import android.content.Context;
import android.os.Bundle;

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
        logEvent("clickPack " + "id:" + id + " " + "title:" + title);
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
        logEvent("add2WSP " + "id:" + id + " " + "title:" + title);
    }

    public static void logToPackDownloadComplete(int id,String title) {
        logEvent("packDownloadComplete " + "id:" + id + " " + "title:" + title);
    }

    public static void logToPackDownloadFailed(int id,String title) {
        logEvent("packDownloadFailed " + "id:" + id + " " + "title:" + title);
    }

    public static void logToPackAddSuccess(int id,String title) {
        logEvent("packAddSuccess " + "id:" + id + " " + "title:" + title);
    }

    public static void logToCategorySwitch(int id,String title) {
        logEvent("categorySwitch " + "id:" + id + " " + "title:" + title);
    }

    public static void logToClickSticker(int id) {
        logEvent("clickSticker id:" + id);
    }

    public static void logToSendSticker(int id) {
        logEvent("sendSticker id:" + id);
    }

    public static void logToDownloadStickerComplete(int id) {
        logEvent("downloadStickerComplete id:" + id);
    }

    public static void logToDownloadStickerFailed(int id) {
        logEvent("downloadStickerFailed id:" + id);
    }

    public static void logToFavSticker(int id) {
        logEvent("favSticker id:" + id);
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
