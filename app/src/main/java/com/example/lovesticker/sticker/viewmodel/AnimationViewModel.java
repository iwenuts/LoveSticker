package com.example.lovesticker.sticker.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimationViewModel extends BaseViewModel {
    private BaseRepository baseRepository;
    private List<AllAnimatedBean.Postcards> postcardsList = new ArrayList<>();
    private MutableLiveData<List<AllAnimatedBean.Postcards>> allAnimated;


    public MutableLiveData<List<AllAnimatedBean.Postcards>> getAllAnimatedLiveData() {
        if (allAnimated == null) {
            allAnimated = new MutableLiveData<>();
        }
        return allAnimated;
    }


    public void requestInitialAllAnimatedData() {
        baseRepository = BaseRepository.getInstance();
        baseRepository.getAllAnimatedStickersData().enqueue(new Callback<AllAnimatedBean>() {
            @Override
            public void onResponse(Call<AllAnimatedBean> call, Response<AllAnimatedBean> response) {
                AllAnimatedBean allAnimatedBean = response.body();

                if (allAnimatedBean != null) {
                    LSMKVUtil.put("allAnimatedTotalPages", allAnimatedBean.getData().getTotalPages());

                    if (allAnimatedBean.getData().getPostcardsList() != null) {
                        postcardsList.addAll(allAnimatedBean.getData().getPostcardsList());

                        allAnimated.setValue(postcardsList);

                    }
                }
            }

            @Override
            public void onFailure(Call<AllAnimatedBean> call, Throwable t) {

            }
        });
    }

    public void requestSurplusAllAnimatedData() {
        baseRepository.getNextAllAllAnimatedStickersData().enqueue(new Callback<AllAnimatedBean>() {
            @Override
            public void onResponse(Call<AllAnimatedBean> call, Response<AllAnimatedBean> response) {
                AllAnimatedBean allAnimatedBean = response.body();

                if (allAnimatedBean != null) {
                    if (allAnimatedBean.getData().getPostcardsList() != null) {
                        for (AllAnimatedBean.Postcards postcards : allAnimatedBean.getData().getPostcardsList()) {
                            postcardsList.add(postcards);
                        }

                    }
                }

            }

            @Override
            public void onFailure(Call<AllAnimatedBean> call, Throwable t) {

            }
        });


    }


}
