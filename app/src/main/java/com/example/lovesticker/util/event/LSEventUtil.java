package com.example.lovesticker.util.event;

import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.BuildConfig;
//import com.google.firebase.analytics.FirebaseAnalytics;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;
import java.util.Map;

public class LSEventUtil {
    private static Context applicationContext;
//    private static FirebaseAnalytics mFirebaseAnalytics = null;

    public static void init(Context appContext) {
        applicationContext = appContext;
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext);

        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
//        UMConfigure.preInit(applicationContext,BuildConfig.U_MENG_KEY,"GP" );
//
//        UMConfigure.init(applicationContext,BuildConfig.U_MENG_KEY,"GP",
//                UMConfigure.DEVICE_TYPE_PHONE,null);
        UMConfigure.setProcessEvent(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static void logInstallFrom(String from) {
        HashMap<String,String> value = new HashMap<>();
        value.put("from",from);
        logEventLS("InstallFrom",value);
    }

    public static void logToSplashPage(boolean isToForce) {
        logEventLS("toSplashPage");
        if(isToForce) {
            logEventLS("toSplashPageUS");
        }else{
            logEventLS("toSplashPageOR");
        }
    }

    public static void logToHomeFromSp() {
        logEventLS("toHomePageSP");
    }

    public static void logToSubsPage(boolean isForce,String from) {
        HashMap<String,String> value = new HashMap<>();
        value.put("from",from);

        if(isForce) {
            logEventLS("toSubsPageUS",value);
//            if(from.equals(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("toSubsPageUSSP");
//            }else{
//                logEventTestYHZ("toSubsPageUSAPP");
//            }
        }else{
            logEventLS("toSubsPageOR",value);
//            if(from.equals(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("toSubsPageORSP");
//            }else{
//                logEventTestYHZ("toSubsPageORAPP");
//            }
        }

    }

    public static void clickSubsBtn(boolean isForce,String sku,String from) {
        HashMap<String,String> value = new HashMap<>();
        value.put("sku",sku);
        if(isForce) {
            logEventLS("clickSubsBtnUS",value);
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("clickSubsBtnUSSP",value);
//            }else{
//                logEventTestYHZ("clickSubsBtnUSAPP",value);
//            }
        }else{
            logEventLS("clickSubsBtnOR",value);
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("clickSubsBtnORSP",value);
//            }else{
//                logEventTestYHZ("clickSubsBtnORAPP",value);
//            }
        }
    }

    public static void clickCloseBtn(boolean isForce,String from) {
        if(isForce) {
            logEventLS("clickCloseBtnUS");
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("clickCloseBtnUSSP");
//            }else{
//                logEventTestYHZ("clickCloseBtnUSAPP");
//            }
        }else{
            logEventLS("clickCloseBtnOR");
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("clickCloseBtnORSP");
//            }else{
//                logEventTestYHZ("clickCloseBtnORAPP");
//            }
        }
    }

    public static void logSubsSuccess(String sku,String orderId,
                                      double price,String currency,String from,boolean isForce) {
        HashMap<String,String> value = new HashMap<>();
        value.put("sku",sku);
        value.put("from",from);
        
        if(isForce) {
            logEventLS("subsSuccessUS", value);
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("subsSuccessUSSP", value);
//            }else{
//                logEventTestYHZ("subsSuccessUSAPP", value);
//            }
        }else{
            logEventLS("subsSuccessOR", value);
//            if(from.equalsIgnoreCase(YhTestXBaseSubsActivity.FROM_SPLASH)) {
//                logEventTestYHZ("subsSuccessORSP", value);
//            }else{
//                logEventTestYHZ("subsSuccessORAPP", value);
//            }
        }

        logStandSubsSuccess(orderId,price,currency);
    }

    public static void logSubsFail(String errorMsg,boolean isForce) {
        HashMap<String,String> value = new HashMap<>();
        value.put("msg",errorMsg);
        if(isForce) {
            logEventLS("subsFailedUS",value);
        }else{
            logEventLS("subsFailedOR",value);
        }

    }

    private static void logStandSubsSuccess(String orderId, double price,String currency) {
//        if(mFirebaseAnalytics != null) {
//            Bundle params = new Bundle();
//            params.putString(FirebaseAnalytics.Param.CURRENCY, currency);
//            params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
//            params.putDouble(FirebaseAnalytics.Param.VALUE, price);
//
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, params);
//        }
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

    public static void logAdEvent(String key) {
        logEventLS(key);
    }

    private static void logEventLS(String key) {
//        if(mFirebaseAnalytics != null)
//            mFirebaseAnalytics.logEvent(key, new Bundle());
        HashMap<String,Object> umValue = new HashMap<>();
        umValue.put("x","x");
        MobclickAgent.onEventObject(applicationContext, key, umValue);
    }
}
