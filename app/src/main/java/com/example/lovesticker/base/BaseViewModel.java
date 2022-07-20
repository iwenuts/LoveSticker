package com.example.lovesticker.base;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.lifecycle.ViewModel;

import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseViewModel extends ViewModel {
//    private HandlerThread handlerThread;
//    private Handler threadHandler;

//    protected Looper getThreadLooper() {
//        if(handlerThread == null) {
//            handlerThread = new HandlerThread("HandlerThread");
//            handlerThread.start();
//        }
//        return handlerThread.getLooper();
//    }
//
//    protected Handler getThreadHandler() {
//        if(threadHandler == null) {
//            threadHandler = new Handler(getThreadLooper());
//        }
//        return threadHandler;
//    }

//    @Override
//    protected void onCleared() {
//        super.onCleared();
//
//        if(threadHandler != null) {
//            threadHandler.removeCallbacksAndMessages(null);
//            threadHandler = null;
//        }
//        if(handlerThread != null) {
//            handlerThread.quit();
//            handlerThread = null;
//        }
//    }

//    protected BaseRepository baseRepository = BaseRepository.getInstance();
//    protected List<AnimatedCategoriesBean.Data> allCategoriesList;
//
//
//
//    protected void requestAllAnimatedCategoriesData(){
//        baseRepository.getAllAnimatedCategoriesData().enqueue(new Callback<AnimatedCategoriesBean>() {
//            @Override
//            public void onResponse(Call<AnimatedCategoriesBean> call, Response<AnimatedCategoriesBean> response) {
//                AnimatedCategoriesBean animatedCategoriesBean = response.body();
//                if (animatedCategoriesBean != null){
//
//                    for (AnimatedCategoriesBean.Data data:animatedCategoriesBean.getData()){
//                        allCategoriesList.add(data);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<AnimatedCategoriesBean> call, Throwable t) {
//
//            }
//        });
//    }




}
