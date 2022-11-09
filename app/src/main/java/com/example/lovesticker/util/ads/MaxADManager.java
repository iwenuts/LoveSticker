package com.example.lovesticker.util.ads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkSettings;
import com.applovin.sdk.AppLovinSdkUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.main.model.LoveStickerBean;
import com.example.lovesticker.util.event.LSEventUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

public class MaxADManager implements LifecycleObserver {

    public static final String INTERSTITIAL_Detail = BuildConfig.INTERSTITIAL_Detail;
    public static final String INTERSTITIAL_Back = BuildConfig.INTERSTITIAL_Back;
    public static final String MAX_REWARD = BuildConfig.MAX_REWARD;
    public static final String MAX_MREC = BuildConfig.MAX_MREC;
    public static final String MAX_BANNER = BuildConfig.MAX_BANNER;
    public static final String APP_LOVIN_KEY = BuildConfig.APP_LOVIN_ID;
    private AppLovinSdk appLovinSdk;

    private static OnRewardListener mOnRewardListener;
    private static FirebaseAnalytics mFirebaseAnalytics;

    private MaxADManager() {
    }

    private static final MaxADManager instance = new MaxADManager();

    public static void initMaxAd(Context context) {
        try {
            instance.appLovinSdk = AppLovinSdk.getInstance(APP_LOVIN_KEY, new AppLovinSdkSettings(context), context);
            instance.appLovinSdk.setMediationProvider(AppLovinMediationProvider.MAX);
            instance.appLovinSdk.initializeSdk((new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(AppLovinSdkConfiguration config) {

                    Activity topAct = ActivityUtils.getTopActivity();
                    if (topAct != null) {
                        loadRewardAd(topAct);
                        SPStaticUtils.put("initReward", true);
                    }

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMaxDebugger() {
        if (instance.appLovinSdk != null && BuildConfig.DEBUG) {
            instance.appLovinSdk.showMediationDebugger();
        }
    }

    public static void enableDebugMaxAd(Context context) {
        if (BuildConfig.DEBUG) {
            SharedPreferences sp = context.getSharedPreferences("com.applovin.sdk.preferences."
                    + APP_LOVIN_KEY, Context.MODE_PRIVATE);
            sp.edit().putBoolean("com.applovin.sdk.mediation.test_mode_enabled", true).apply();
        }
    }


    // 加载InterstitialDetail广告，加载出来后不显示，等调用下面的方法的时候再显示
    public static void loadInterstitialDetailAd(AppCompatActivity activity) {
//        Log.e("###", "loadInterstitialDetailAd ");
        instance.loadInterstitialDetail(activity);
    }

    // 展示InterstitialDetail广告
    public static void tryShowInterstitialDetailAd(AppCompatActivity activity) {
//        Log.e("###", "tryShowInterstitialDetailAd ");
        instance.tryShowInterstitialDetail(activity);
    }


    public static void loadInterstitialBackAd(AppCompatActivity activity) {
//        Log.e("###", "loadInterstitialBackAd ");
        instance.loadInterstitialBack(activity);
    }


    public static void tryShowInterstitialBackAd(AppCompatActivity activity) {
//        Log.e("###", "tryShowInterstitialBackAd ");
        instance.tryShowInterstitialBack(activity);
    }


    // 加载并显示Reward广告，第二个参数是超时事件，如果加载时间超过这个时间还没有加载出来，
    //    则回调timeout，第三个参数为回调接口，具体方法见接口注释
    public static void loadRewardAd(Activity activity) {
        instance.loadReward(activity);
    }

    public static void tryShowRewardAd(Activity activity) {
        instance.tryShowReward(activity);
    }

    public static void rewardListener(OnRewardListener listener) {
        mOnRewardListener = listener;
    }


    public interface OnRewardListener {
        // reward广告加载失败，不给用户奖励
        void onRewardFail();

        // reward广告展示，在此回调里隐藏加载进度条
        void onRewardShown();

        // reward广告播放完，并关闭，即用户获得奖励
        void onRewarded();

        // 加载reward广告超时，此时可以根据情况给用户奖励
        void onTimeOut();
    }

    //// 加载Mrec广告
    public static void loadMrecIntoView(AppCompatActivity activity, FrameLayout adContainer) {
        instance.loaMrecAndShow(activity, adContainer);
    }


    // 加载banner广告，第二个参数为放banner广告的容器，高50dp
    public static void loadBannerIntoView(AppCompatActivity activity, FrameLayout adContainer) {
        instance.loadBannerToView(activity, adContainer);
    }


    //interstitial_detail
    private MaxInterstitialAd interstitialDetail;
    private boolean waitingIntersShow = false;

    private void loadInterstitialDetail(AppCompatActivity activity) {
//        Log.e("###", "loadInterstitialDetail: "+ interstitialDetail );

        if (interstitialDetail != null) return;

        interstitialDetail = new MaxInterstitialAd(INTERSTITIAL_Detail, appLovinSdk, activity);

        interstitialDetail.setRevenueListener(ad -> {
            try {
                double revenue = ad.getRevenue(); // In USD
                // Miscellaneous data
                // String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
                String networkName = ad.getNetworkName(); // Display name of the network that showed the movid.append.in.ad (e.g. "AdColony")
                String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
                MaxAdFormat adFormat = ad.getFormat(); // The movid.append.in.ad format of the movid.append.in.ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
                String placement = ad.getPlacement(); // The placement this movid.append.in.ad's postbacks are tied to


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                LSEventUtil.sendRevenue(bundle);
            } catch (Exception e) {

            }
        });

        interstitialDetail.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                if (waitingIntersShow) {
                    interstitialDetail.showAd();
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                waitingIntersShow = false;
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                if (interstitialDetail != null) interstitialDetail.destroy();
                interstitialDetail = null;
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                waitingIntersShow = false;
                if (interstitialDetail != null) interstitialDetail.destroy();
                interstitialDetail = null;
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                waitingIntersShow = false;
                if (interstitialDetail != null) interstitialDetail.destroy();
                interstitialDetail = null;
            }
        });
        activity.getLifecycle().addObserver(this);

        interstitialDetail.loadAd();

    }

    private void tryShowInterstitialDetail(AppCompatActivity activity) {
        if (interstitialDetail == null) {
            waitingIntersShow = true;
            loadInterstitialDetail(activity);
            return;
        }
        if (interstitialDetail.isReady()) interstitialDetail.showAd();
        else waitingIntersShow = true;
    }


    //interstitial_back
    private MaxInterstitialAd interstitialBack;
    private boolean waitingBackShow = false;

    private void loadInterstitialBack(AppCompatActivity activity) {

        if (interstitialBack != null) return;

        interstitialBack = new MaxInterstitialAd(INTERSTITIAL_Back, appLovinSdk, activity);

        interstitialBack.setRevenueListener(ad -> {
            try {
                double revenue = ad.getRevenue(); // In USD
                // Miscellaneous data
                // String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
                String networkName = ad.getNetworkName(); // Display name of the network that showed the movid.append.in.ad (e.g. "AdColony")
                String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
                MaxAdFormat adFormat = ad.getFormat(); // The movid.append.in.ad format of the movid.append.in.ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
                String placement = ad.getPlacement(); // The placement this movid.append.in.ad's postbacks are tied to


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                LSEventUtil.sendRevenue(bundle);
            } catch (Exception e) {

            }
        });

        interstitialBack.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                if (waitingBackShow) {
                    interstitialBack.showAd();
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                waitingBackShow = false;
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                if (interstitialBack != null) interstitialBack.destroy();
                interstitialBack = null;
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                waitingBackShow = false;
                if (interstitialBack != null) interstitialBack.destroy();
                interstitialBack = null;
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                waitingBackShow = false;
                if (interstitialBack != null) interstitialBack.destroy();
                interstitialBack = null;
            }
        });
        activity.getLifecycle().addObserver(this);

        interstitialBack.loadAd();

    }

    private void tryShowInterstitialBack(AppCompatActivity activity) {
        if (interstitialBack == null) {
            waitingBackShow = true;
            loadInterstitialBack(activity);
            return;
        }
        if (interstitialBack.isReady()) interstitialBack.showAd();
        else waitingBackShow = true;

    }


    /**
     * reward ad
     */
    private MaxRewardedAd rewardAd;
    private final Handler rewardHandler = new Handler(Looper.getMainLooper());
    private boolean isRewarded = false;

    private void loadReward(Activity activity) {
        if (appLovinSdk == null) return;
        isRewarded = false;
        if (rewardAd == null) {
            rewardAd = MaxRewardedAd.getInstance(MAX_REWARD, appLovinSdk, activity);
        }

        rewardAd.setRevenueListener(ad -> {
            try {
                double revenue = ad.getRevenue(); // In USD
                // Miscellaneous data
                // String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
                String networkName = ad.getNetworkName(); // Display name of the network that showed the movid.append.in.ad (e.g. "AdColony")
                String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
                MaxAdFormat adFormat = ad.getFormat(); // The movid.append.in.ad format of the movid.append.in.ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
                String placement = ad.getPlacement(); // The placement this movid.append.in.ad's postbacks are tied to


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                LSEventUtil.sendRevenue(bundle);
            } catch (Exception e) {

            }
        });

        rewardAd.setListener(rewardListener(activity));

        rewardAd.loadAd();

//        rewardHandler.postDelayed(() -> {
//            waitingRewardShow = false;
//            mOnRewardListener.onTimeOut(); // 加载reward广告超时，此时可以根据情况给用户奖励
//        }, 10000);

//        if(rewardAd.isReady()){
//            rewardAd.showAd();
//        }else{
//            rewardAd.loadAd();
//            waitingRewardShow = true;
//
//            rewardHandler.postDelayed(() -> {
//                waitingRewardShow = false;
//                listener.onTimeOut(); // 加载reward广告超时，此时可以根据情况给用户奖励
//            },timeoutT);
//        }
    }


    private void tryShowReward(Activity activity) {
//        Log.e("###", "tryShowReward: ");
//        Log.e("###", "rewardAd: " + rewardAd);

        if (rewardAd == null) {
            rewardAd = MaxRewardedAd.getInstance(MAX_REWARD, appLovinSdk, activity);
            rewardAd.setListener(rewardListener(activity));
            rewardAd.loadAd();
        }

        if (rewardAd.isReady()) {
            rewardAd.showAd();
        } else {
            rewardHandler.postDelayed(() -> {
                if (rewardAd.isReady()){
                    rewardAd.showAd();
                }else {
                    rewardAd.loadAd();
                    mOnRewardListener.onTimeOut(); // 加载reward广告超时，此时可以根据情况给用户奖励
                }

            }, 10000);
        }

    }

    private MaxRewardedAdListener rewardListener(Activity activity){

        return new MaxRewardedAdListener() {
            @Override
            public void onRewardedVideoStarted(MaxAd ad) {

            }

            @Override
            public void onRewardedVideoCompleted(MaxAd ad) {

            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
//                Log.e("###", "onUserRewarded: ");
                isRewarded = true;
            }

            @Override
            public void onAdLoaded(MaxAd ad) {
//                Log.e("###", "onAdLoaded: ");

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
//                Log.e("###", "onAdDisplayed: ");
                rewardHandler.removeCallbacksAndMessages(null);
                mOnRewardListener.onRewardShown(); // reward广告展示，在此回调里隐藏加载进度条
            }

            @Override
            public void onAdHidden(MaxAd ad) {
//                Log.e("###", "onAdHidden: ");

                if (isRewarded) {
                    mOnRewardListener.onRewarded(); // reward广告播放完，并关闭，即用户获得奖励
                } else {
                    mOnRewardListener.onRewardFail(); // reward广告加载失败，不给用户奖励
                }

                isRewarded = false;

                if (rewardAd != null) {
                    rewardAd.loadAd();
                } else {
                    rewardAd = MaxRewardedAd.getInstance(MAX_REWARD, appLovinSdk, activity);
                    rewardAd.setListener(this);
                    rewardAd.loadAd();
                }
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
//                Log.e("###", "onAdLoadFailed: ");
                rewardHandler.removeCallbacksAndMessages(null);

                if (rewardAd != null) {
                    rewardAd.loadAd();
                } else {
                    rewardAd = MaxRewardedAd.getInstance(MAX_REWARD, appLovinSdk, activity);
                    rewardAd.setListener(this);
                    rewardAd.loadAd();
                }

                mOnRewardListener.onRewardFail(); // reward广告加载失败，不给用户奖励
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//                Log.e("###", "onAdDisplayFailed: ");
                rewardHandler.removeCallbacksAndMessages(null);

                if (rewardAd != null) {
                    rewardAd.loadAd();
                } else {
                    rewardAd = MaxRewardedAd.getInstance(MAX_REWARD, appLovinSdk, activity);
                    rewardAd.setListener(this);
                    rewardAd.loadAd();
                }
                mOnRewardListener.onRewardFail();  // reward广告加载失败，不给用户奖励
            }
        };
    }


    private MaxAdView mrecAd;
    private ViewGroup mRootView;
    private boolean waitingMrecShow = false;


    private void loaMrecAndShow(AppCompatActivity activity, FrameLayout adContainer) {
        if (appLovinSdk == null) return;

        mrecAd = new MaxAdView(MAX_MREC, MaxAdFormat.MREC, appLovinSdk, activity);

        if (adContainer.toString() == null) {
            mrecAd.destroy();
            adContainer.removeAllViews();
        } else {
            activity.getLifecycle().addObserver(this);
        }


        int heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        mrecAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

//        adContainer.removeAllViews();
        adContainer.addView(mrecAd);
        mrecAd.loadAd();

        mrecAd.setRevenueListener(ad -> {
            try {
                double revenue = ad.getRevenue(); // In USD
                // Miscellaneous data
                // String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
                String networkName = ad.getNetworkName(); // Display name of the network that showed the movid.append.in.ad (e.g. "AdColony")
                String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
                MaxAdFormat adFormat = ad.getFormat(); // The movid.append.in.ad format of the movid.append.in.ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
                String placement = ad.getPlacement(); // The placement this movid.append.in.ad's postbacks are tied to


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                LSEventUtil.sendRevenue(bundle);
            } catch (Exception e) {

            }
        });

        mrecAd.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                mrecAd.startAutoRefresh();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });

    }

//    public void startAutoRefresh() {
//        if (mrecAd == null) {
//            return;
//        }
//
//        mRootView.setVisibility(View.VISIBLE);
//        mrecAd.setVisibility(View.VISIBLE);
//        mrecAd.startAutoRefresh();
//    }
//
//    public void stopAutoRefresh() {
//        if (mrecAd == null) {
//            return;
//        }
//
//        mRootView.setVisibility(View.GONE);
//        mrecAd.setVisibility(View.GONE);
//        mrecAd.stopAutoRefresh();
//    }


    /**
     * banner ad
     */
    private final HashMap<String, MaxAdView> bannerAdMap = new HashMap<>();

    private void loadBannerToView(AppCompatActivity activity, FrameLayout adContainer) {
        if (appLovinSdk == null) return;
        MaxAdView bannerAd;
        if (bannerAdMap.containsKey(activity.toString())
                && bannerAdMap.get(activity.toString()) != null) {
            bannerAd = bannerAdMap.get(activity.toString());
            bannerAd.destroy();
            adContainer.removeAllViews();
        } else {
            activity.getLifecycle().addObserver(this);

        }
        bannerAd = initBannerAd(activity);
        if (bannerAd == null) return;
        bannerAdMap.put(activity.toString(), bannerAd);

        bannerAd.setLayoutParams(getLayoutParam());
        adContainer.addView(bannerAd);
        bannerAd.loadAd();
    }

    private FrameLayout.LayoutParams getLayoutParam() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = ConvertUtils.dp2px(50);
        return new FrameLayout.LayoutParams(width, heightPx);
    }

    private MaxAdView initBannerAd(AppCompatActivity activity) {
        if (appLovinSdk == null) return null;

        MaxAdView adView = new MaxAdView(MAX_BANNER, appLovinSdk, activity);

        adView.setRevenueListener(ad -> {
            try {
                double revenue = ad.getRevenue(); // In USD
                // Miscellaneous data
                // String countryCode = appLovinSdk.getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD" in most cases!
                String networkName = ad.getNetworkName(); // Display name of the network that showed the movid.append.in.ad (e.g. "AdColony")
                String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
                MaxAdFormat adFormat = ad.getFormat(); // The movid.append.in.ad format of the movid.append.in.ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
                String placement = ad.getPlacement(); // The placement this movid.append.in.ad's postbacks are tied to


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "MaxApplovin");
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, networkName);
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat.getLabel());
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId + "-" + placement);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
                LSEventUtil.sendRevenue(bundle);
            } catch (Exception e) {

            }
        });

        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                adView.startAutoRefresh();
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        return adView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onActivityResume(LifecycleOwner owner) {
//        Log.e("###","on activity resume :" + owner.toString());
        if (bannerAdMap.get(owner.toString()) != null) {
            bannerAdMap.get(owner.toString()).startAutoRefresh();
        }

        if (mrecAd != null) {
            mrecAd.startAutoRefresh();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onActivityStop(LifecycleOwner owner) {
//        Log.e("###","on activity stop :" + owner.toString());
        if (bannerAdMap.get(owner.toString()) != null) {
            bannerAdMap.get(owner.toString()).stopAutoRefresh();
        }

        if (mrecAd != null) {
            mrecAd.stopAutoRefresh();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onActivityDestroy(LifecycleOwner owner) {
//        Log.e("###","on activity destroy :" + owner.toString());
        if (bannerAdMap.get(owner.toString()) != null) {
            bannerAdMap.get(owner.toString()).destroy();
            bannerAdMap.remove(owner.toString());
        }

        if (interstitialDetail != null) {
            interstitialDetail.destroy();
        }

        if (interstitialBack != null) {
            interstitialBack.destroy();
        }

        if (mrecAd != null) {
            mrecAd.destroy();
        }


        if (rewardAd != null) {
            rewardAd.destroy();
            rewardAd = null;
        }
    }

}
