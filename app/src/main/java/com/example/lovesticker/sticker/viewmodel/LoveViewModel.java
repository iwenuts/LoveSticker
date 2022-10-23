package com.example.lovesticker.sticker.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoveViewModel extends BaseViewModel {
    private BaseRepository baseRepository;
    private MutableLiveData<Integer> singleAnimatedLiveData;
    public List<SingleAnimatedCategoriesBean.Postcards> postcardsList = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 0;

    public MutableLiveData<Integer> getSingleAnimatedLiveData(){
        if (singleAnimatedLiveData == null){
            singleAnimatedLiveData = new MutableLiveData<>();
        }
        return singleAnimatedLiveData;
    }

    public void requestInitialSingleAnimatedData(String link,int page){
        baseRepository = BaseRepository.getInstance();
        baseRepository.getSingleAnimatedCategoriesData(link,page).enqueue(new Callback<SingleAnimatedCategoriesBean>() {
            @Override
            public void onResponse(Call<SingleAnimatedCategoriesBean> call, Response<SingleAnimatedCategoriesBean> response) {
                SingleAnimatedCategoriesBean singleAnimatedCategoriesBean = response.body();

                if (singleAnimatedCategoriesBean != null){
                    LSMKVUtil.put("singleAnimatedTotalPages", singleAnimatedCategoriesBean.getData().getTotalPages());

                    totalPage = singleAnimatedCategoriesBean.getData().getTotalPages();

                    if (1 == page)
                        postcardsList.clear();

                    if (singleAnimatedCategoriesBean.getData().getPostcardsList() != null){
                        postcardsList.addAll(singleAnimatedCategoriesBean.getData().getPostcardsList());
                        singleAnimatedLiveData.setValue(singleAnimatedCategoriesBean.getData().getPostcardsList().size());
                    }

                }
            }

            @Override
            public void onFailure(Call<SingleAnimatedCategoriesBean> call, Throwable e) {
                Log.e("###", "SingleAnimatedData onFailure: " + e.getMessage());

                if (page > 1){
                    nowPage --;
                }

                singleAnimatedLiveData.setValue(-1);
            }
        });

    }

    public void requestInitialSingleAnimatedData(String link){
        nowPage = 1;
        requestInitialSingleAnimatedData(link,nowPage);
    }

    public Boolean requestSurplusSingleAnimatedData(String link){
        nowPage ++;

        if (nowPage == totalPage)
            return false;

        requestInitialSingleAnimatedData(link,nowPage);

        return true;

    }




}
