package com.example.lovesticker.util.score;

import android.content.Context;

import com.example.lovesticker.base.LoveStickerApp;

public class RateController {
    private static volatile RateController instance;

    public int mTotoalCount = 3;

    public static RateController getInstance() {
        if (instance == null) {
            synchronized (RateController.class) {
                if (instance == null) {
                    instance = new RateController();
                }
            }
        }
        return instance;
    }

    private RateController() {
        try {
            Context context = LoveStickerApp.getAppContext();
            if (null != context)
                RateDialog.setPopTotalCount(context, mTotoalCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void tryRateFinish(Context context, RateDialog.RatingClickListener listene) {
        //if (Facebook.getInstance().sBean.israte) {
        RateDialog.launch(context, "Thank you for your support", "Would you please give us a 5-star rating?", listene);
        //}
    }
}