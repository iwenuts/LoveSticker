package com.example.lovesticker.util.score;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.lovesticker.R;
import com.example.lovesticker.databinding.ActivityRateDialogBinding;

public class RateDialog extends AppCompatActivity {
    private boolean mFinishWithAnimation = true;
    private ImageView[] startIVS = new ImageView[5];
    private PointContainer[] starFrameLayouts = new PointContainer[5];
    private AnimatorSet animatorSet;
    private String title;
    private String description;
    private static RatingClickListener sRatingClickListener;
    private int currentIndex = 0;

    private static int totalcount;
    private ActivityRateDialogBinding mBinding;

    public static void setPopTotalCount(Context context, int count) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt("recommend_total_count", count);
        editor.apply();
    }

    public static int getPopCount(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("recommend_count", 0);
    }

    private static int getPopTotalCount(Context contex) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contex);
        return preferences.getInt("recommend_total_count", 1);
    }

    private static void setPopCount(int count, Context contex) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(contex).edit();
        ++count;
        editor.putInt("recommend_count", count);
        editor.apply();
    }

    /**
     * 启动
     *
     * @param context
     */
    public static void launch(Context context, RatingClickListener listener) {
        try {
            if (!willShow(context)) {
                return;
            }

            Intent intent = new Intent(context, RateDialog.class);
            if (context instanceof Activity) {
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            setRatingClickListener(listener);
        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }

    public static void launch(Context context, String title, String description, RatingClickListener listener) {
        try {
            if (!willShow(context)) {
                listener.onClickReject();
                return;
            }
            Intent intent = new Intent(context, RateDialog.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            if (context instanceof Activity) {
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            setRatingClickListener(listener);
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    /**
     * 设置监听
     *
     * @param ratingClickListener
     */
    public static void setRatingClickListener(RatingClickListener ratingClickListener) {
        sRatingClickListener = ratingClickListener;
    }

    /**
     * 是否可以显示评分对话框---todo 逻辑
     *
     * @param context
     * @return
     */
    public static boolean willShow(Context context) {
        int count = getPopCount(context);
        totalcount = getPopTotalCount(context);
        if (count < totalcount) {
            setPopCount(count, context);
            return true;
        } else {
            return false;
        }
    }


    public RateDialog() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRateDialogBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        //FlurryEvent.sendRate("Show");
    }

    private void initView() {
        if (this.getIntent() != null) {
            this.title = this.getIntent().getStringExtra("title");
            this.description = this.getIntent().getStringExtra("description");
        }

//        if (TextUtils.isEmpty(this.title)) {
//            mBinding.titleTv.setText(String.format("Like %s", new Object[]{this.getString(R.string.app_name)}));
//        } else {
//            mBinding.titleTv.setText(this.title);
//        }

        if (!TextUtils.isEmpty(this.description)) {
            mBinding.descriptionTv.setText(this.description);
        }

        this.startIVS[0] = mBinding.starIv1;
        this.starFrameLayouts[0] = mBinding.star1;
        this.startIVS[1] = mBinding.starIv2;
        this.starFrameLayouts[1] = mBinding.star2;
        this.startIVS[2] = mBinding.starIv3;
        this.starFrameLayouts[2] = mBinding.star3;
        this.startIVS[3] = mBinding.starIv4;
        this.starFrameLayouts[3] = mBinding.star4;
        this.startIVS[4] = mBinding.starIv5;
        this.starFrameLayouts[4] = mBinding.star5;

        mBinding.starIv1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > 16) {
                    mBinding.starIv1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mBinding.starIv1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mBinding.starIv1.postDelayed(new Runnable() {
                    public void run() {
                        RateDialog.this.handleMoveAnimation(mBinding.handleIconIv, mBinding.starIv5, new Runnable() {
                            public void run() {
                                startAnimationSpread();
                            }
                        });
                    }
                }, 600L);
            }
        });
    }

    private void handleMoveAnimation(ImageView handleIV, View targetView, final Runnable endRunnable) {
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        ObjectAnimator XAnimator = ObjectAnimator.ofFloat(handleIV, "x", new float[]{handleIV.getX(), (float) location[0]});
        XAnimator.setDuration(200L);
        ObjectAnimator YAnimator = ObjectAnimator.ofFloat(handleIV, "y", new float[]{handleIV.getY(), (float) location[1]});
        YAnimator.setDuration(200L);
        ObjectAnimator aplhaAnimator = ObjectAnimator.ofFloat(handleIV, "alpha", new float[]{1.0F, 0.0F});
        aplhaAnimator.setDuration(200L);
        aplhaAnimator.setStartDelay(200L);
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[]{XAnimator, YAnimator, aplhaAnimator});
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (endRunnable != null) {
                    endRunnable.run();
                }

            }
        });
        this.animatorSet.start();
    }

    private void startAnimationSpread() {
        this.starFrameLayouts[this.currentIndex].startPointSpread(this.startIVS[this.currentIndex], new Runnable() {
            public void run() {
                RateDialog.this.currentIndex++;
                if (RateDialog.this.currentIndex < 5) {
                    RateDialog.this.starFrameLayouts[RateDialog.this.currentIndex].startPointSpread(RateDialog.this.startIVS[RateDialog.this.currentIndex], this);
                } else {
                    RateDialog.this.currentIndex = 0;
                }

            }
        });
    }

    private void stopAnimationSpread() {
        PointContainer[] var1 = this.starFrameLayouts;
        int var2 = var1.length;

        int var3;
        for (var3 = 0; var3 < var2; ++var3) {
            PointContainer PointContainer = var1[var3];
            PointContainer.stopPointSpread();
        }

        ImageView[] var5 = this.startIVS;
        var2 = var5.length;

        for (var3 = 0; var3 < var2; ++var3) {
            ImageView starImg = var5[var3];
            starImg.setImageResource(R.mipmap.icon_rating_star);
        }
    }

    public void onRatingButton1to3Clicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClick1To4Start();
        }
        ToastUtils.showShort("Thanks for your suggest!");
        //FlurryEvent.sendRate("stars1-3");
        this.mFinishWithAnimation = false;
        this.finish();
    }

    public void onCancelClicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClickCancel();
        }
        ToastUtils.showShort("Thanks for your suggest!");
        //FlurryEvent.sendRate("Cancel");
        this.mFinishWithAnimation = false;
        this.finish();
    }

    public void onRatingButtonClicked(View v) {
        if (sRatingClickListener != null) {
            sRatingClickListener.onClickFiveStart();
        }
        //FlurryEvent.sendRate("OK");
        this.mFinishWithAnimation = false;
        this.finish();

        //三次评分必须弹出
        //setPopCount(RateManager.getInstance().mTotoalCount, MainActivity.getInstance());
        try {
            openPlaystore(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //SendUtils.gotoGoogePlayStore(this, getPackageName());
    }

    public void finish() {
        super.finish();
        if (this.mFinishWithAnimation) {
            this.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onDismissButtonClicked(View v) {
        this.finish();
    }

    public void openPlaystore(Context context) {
        openPlaystore(context, context.getPackageName());
    }

    public void openPlaystore(Context context, String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
            intent.setPackage("com.android.vending");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    protected void onDestroy() {
        this.stopAnimationSpread();
        if (this.animatorSet != null && this.animatorSet.isRunning()) {
            this.animatorSet.cancel();
        }
        super.onDestroy();
        sRatingClickListener = null;
    }

    public interface RatingClickListener {
        void onClickFiveStart();

        void onClick1To4Start();

        void onClickReject();

        void onClickCancel();

    }
}
