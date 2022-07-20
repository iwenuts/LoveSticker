package com.example.lovesticker.main.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerViewModel extends BaseViewModel {
    private BaseRepository baseRepository;
    private MutableLiveData<List<AnimatedCategoriesBean.CategoriesData>> animateCategoriesLiveData;
    private List<AnimatedCategoriesBean.CategoriesData> allCategoriesList = new ArrayList<>();

    public MutableLiveData<List<AnimatedCategoriesBean.CategoriesData>> getAllAnimatedCategoriesLiveData(){
        if (animateCategoriesLiveData == null){
            animateCategoriesLiveData = new MutableLiveData<>();
        }
        return animateCategoriesLiveData;
    }


    public void requestAllAnimatedCategoriesData(){
        baseRepository = BaseRepository.getInstance();
        baseRepository.getAllAnimatedCategoriesData().enqueue(new Callback<AnimatedCategoriesBean>() {
            @Override
            public void onResponse(Call<AnimatedCategoriesBean> call, Response<AnimatedCategoriesBean> response) {
                AnimatedCategoriesBean animatedCategoriesBean = response.body();

                if (animatedCategoriesBean != null){

                    for (AnimatedCategoriesBean.CategoriesData data : animatedCategoriesBean.getData()){
                        allCategoriesList.add(data);
                    }
                    animateCategoriesLiveData.setValue(allCategoriesList);
                }
            }

            @Override
            public void onFailure(Call<AnimatedCategoriesBean> call, Throwable t) {

            }
        });
    }

}
