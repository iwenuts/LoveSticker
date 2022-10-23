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
    public List<AllAnimatedBean.Postcards> postcardsList = new ArrayList<>();
    private MutableLiveData<Integer> allAnimated;
    private int nowPage = 1;
    private int totalPage = 0;



    public MutableLiveData<Integer> getAllAnimatedLiveData() {
        if (allAnimated == null) {
            allAnimated = new MutableLiveData<>();
        }
        return allAnimated;
    }

    public void requestAllAnimatedData(int page){
        baseRepository = BaseRepository.getInstance();

        baseRepository.getAnimatedStickersData(page).enqueue(new Callback<AllAnimatedBean>() {
            @Override
            public void onResponse(Call<AllAnimatedBean> call, Response<AllAnimatedBean> response) {
                AllAnimatedBean allAnimatedBean = response.body();
                if (allAnimatedBean != null) {
                    LSMKVUtil.put("allAnimatedTotalPages", allAnimatedBean.getData().getTotalPages());
                    totalPage = allAnimatedBean.getData().getTotalPages();

                    if (1 == page)
                        postcardsList.clear();

                    if (allAnimatedBean.getData().getPostcardsList() != null) {
                        postcardsList.addAll(allAnimatedBean.getData().getPostcardsList());

                        allAnimated.setValue(allAnimatedBean.getData().getPostcardsList().size()); //新添加的数量

                    }
                }
            }

            @Override
            public void onFailure(Call<AllAnimatedBean> call, Throwable t) {
                if (page > 1){
                    nowPage --;
                }
                allAnimated.setValue(-1);
            }
        });
    }

    public void requestInitialAllAnimatedData() {
        nowPage = 1;
        requestAllAnimatedData(nowPage);
    }

    public Boolean requestSurplusAllAnimatedData() {
        nowPage ++;

        if (nowPage == totalPage)
            return false;

        requestAllAnimatedData(nowPage);

        return true;
    }


}
