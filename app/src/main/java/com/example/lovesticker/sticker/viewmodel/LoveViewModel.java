package com.example.lovesticker.sticker.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoveViewModel extends BaseViewModel {
    private BaseRepository baseRepository;
    private MutableLiveData<List<SingleAnimatedCategoriesBean.Postcards>> singleAnimatedLiveData;
    private List<SingleAnimatedCategoriesBean.Postcards> postcardsList = new ArrayList<>();
    private int currentPage = 1;

    public MutableLiveData<List<SingleAnimatedCategoriesBean.Postcards>> getSingleAnimatedLiveData(){
        if (singleAnimatedLiveData == null){
            singleAnimatedLiveData = new MutableLiveData<>();
        }
        return singleAnimatedLiveData;
    }

    public void requestInitialSingleAnimatedData(String link){
        baseRepository = BaseRepository.getInstance();
        baseRepository.getSingleAnimatedCategoriesData(link).enqueue(new Callback<SingleAnimatedCategoriesBean>() {
            @Override
            public void onResponse(Call<SingleAnimatedCategoriesBean> call, Response<SingleAnimatedCategoriesBean> response) {
                SingleAnimatedCategoriesBean singleAnimatedCategoriesBean = response.body();

                if (singleAnimatedCategoriesBean != null){
                    LSMKVUtil.put("singleAnimatedTotalPages", singleAnimatedCategoriesBean.getData().getTotalPages());

                    postcardsList.addAll(singleAnimatedCategoriesBean.getData().getPostcardsList());

                    singleAnimatedLiveData.setValue(postcardsList);

                }
            }

            @Override
            public void onFailure(Call<SingleAnimatedCategoriesBean> call, Throwable t) {

            }
        });

    }

    public void requestSurplusSingleAnimatedData(String link){

        baseRepository.getNextSingleAnimatedCategoriesData(link).enqueue(new Callback<SingleAnimatedCategoriesBean>() {
            @Override
            public void onResponse(Call<SingleAnimatedCategoriesBean> call, Response<SingleAnimatedCategoriesBean> response) {
                SingleAnimatedCategoriesBean singleAnimatedCategoriesBean = response.body();

                if (singleAnimatedCategoriesBean != null){
                    int increasePage = currentPage++;

//                    Log.e("###", "singleAnimatedTotalPages: " + singleAnimatedCategoriesBean.getData().getTotalPages());
//                    Log.e("###", "increasePage: " + increasePage);

                    if (singleAnimatedCategoriesBean.getData().getTotalPages() >= increasePage){
                        postcardsList.addAll(singleAnimatedCategoriesBean.getData().getPostcardsList());
                    }

                    LSMKVUtil.put("singleRefreshFinish", true);
                }
            }

            @Override
            public void onFailure(Call<SingleAnimatedCategoriesBean> call, Throwable t) {

            }
        });

    }




}
