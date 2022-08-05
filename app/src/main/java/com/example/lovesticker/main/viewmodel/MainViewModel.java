package com.example.lovesticker.main.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.main.activity.LoveStickerData;
import com.example.lovesticker.main.model.LoveStickerBean;
import com.example.lovesticker.main.model.StickerPacks;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends BaseViewModel {
    private MutableLiveData<LoveStickerBean> mainLiveData;

    public MutableLiveData<LoveStickerBean> getMainLiveData(){
        if (mainLiveData != null){
            mainLiveData = new MutableLiveData<>();
        }
        return mainLiveData;
    }



    public void requestConfigureData(){
        LoveStickerData.getInstance().getLoveStickerData().enqueue(new Callback<LoveStickerBean>() {
            @Override
            public void onResponse(Call<LoveStickerBean> call, Response<LoveStickerBean> response) {
                LoveStickerBean loveStickerBean = response.body();
                Log.e("###", "loveStickerBean1: " + loveStickerBean.getUv());
                Log.e("###", "loveStickerBean2: " + loveStickerBean.getForce());
                if (loveStickerBean!= null){
                    mainLiveData.setValue(loveStickerBean);
                }

            }

            @Override
            public void onFailure(Call<LoveStickerBean> call, Throwable t) {

            }
        });

    }

}
