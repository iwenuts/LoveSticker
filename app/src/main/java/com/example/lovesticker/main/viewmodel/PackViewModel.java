package com.example.lovesticker.main.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.main.model.PackBean;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackViewModel extends BaseViewModel {
    private MutableLiveData<Integer> spLiveData;
    private BaseRepository baseRepository;

    private List<String> packTitleList;
    private List<Integer> eachPackNumberList;
    private List<Integer> isNewList;
    private List<Integer> isFreeList;
    private List<String> packImageList;
    private int nowPage = 1;
    private int totalPage = 0;

    public List<StickerPacks>  stickerPacksList = new ArrayList<>();
    private List<StickerPacks.Stickers> stickersList = new ArrayList<>();


    public MutableLiveData<Integer> getStickerPacksBean(){
        if (spLiveData == null){
            spLiveData = new MutableLiveData<>();
        }
        return spLiveData;
    }


    private void request(int page){
        baseRepository = BaseRepository.getInstance();

        baseRepository.getPageData(page).enqueue(new Callback<PackBean>() {
            @Override
            public void onResponse(Call<PackBean> call, Response<PackBean> response) {
                PackBean packBean = response.body(); //requestInitialPackData()

                if (packBean != null){
                    Log.e("###", "getTotalPages: " + packBean.getData().getTotalPages());
                    totalPage =  packBean.getData().getTotalPages();

                    if (1 == page)
                        stickerPacksList.clear();

                    if (packBean.getData().getStickerPacksList() != null){
                        stickerPacksList.addAll(packBean.getData().getStickerPacksList());

                        spLiveData.setValue(packBean.getData().getStickerPacksList().size()); //新添加的数量
                    }
                }
            }

            @Override
            public void onFailure(Call<PackBean> call, Throwable t) {
                Log.e("###", "onFailure: " + t.getMessage());
                if (page > 1){
                    nowPage --;
                }
                spLiveData.setValue(-1);
            }
        });

    }


    public void requestInitialPackData(){
        nowPage = 1;
        request(nowPage);
    }

    public boolean requestSurplusPackData(){
        nowPage ++;

        if (nowPage > totalPage)
            return false;

        request(nowPage);

        return true;
    }

    public List<Integer> getEachPackNumber(){
        eachPackNumberList = new ArrayList<>();
        for (int i= 0;i < stickerPacksList.size();i++){
            eachPackNumberList.add(stickerPacksList.get(i).getStickersList().size());
        }
        return eachPackNumberList;
    }

    //todo 以下代码与viewModel的observe所表达的东西重合
    public List<String> getPackTitle(){
        packTitleList = new ArrayList<>();
        for (int i= 0; i < stickerPacksList.size();i++) {
            packTitleList.add(stickerPacksList.get(i).getTitle());

        }
        return packTitleList;

    }

    public List<Integer> getIsNew(){
        isNewList = new ArrayList<>();
        for (int i= 0;i < stickerPacksList.size();i++){
            isNewList.add(stickerPacksList.get(i).getIsNew());
        }
        return isNewList;
    }

    public List<Integer> getIsFree(){
        isFreeList = new ArrayList<>();
        for (int i= 0;i < stickerPacksList.size();i++){
            isFreeList.add(stickerPacksList.get(i).getIsFree());
        }
        return isFreeList;
    }

    public List<String> getPackImage(){
        packImageList = new ArrayList<>();
        for (int i= 0;i < stickersList.size();i++){
            packImageList.add(stickersList.get(i).getImage());
        }
        return packImageList;
    }

    public Integer getTotalPackNumber(){
        return stickersList.size();
    }
}
