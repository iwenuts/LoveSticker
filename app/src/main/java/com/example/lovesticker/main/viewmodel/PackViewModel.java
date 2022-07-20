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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackViewModel extends BaseViewModel {
    private MutableLiveData<List<StickerPacks>> spLiveData;
    private BaseRepository baseRepository;

    private List<String> packTitleList;
    private List<Integer> eachPackNumberList;
    private List<Integer> isNewList;
    private List<Integer> isFreeList;
    private List<String> packImageList;


//    public PackViewModel() {
//        baseRepository = BaseRepository.getInstance();
//    }

    private List<StickerPacks>  stickerPacksList = new ArrayList<>();
    private List<StickerPacks.Stickers> stickersList = new ArrayList<>();

//    public final MutableLiveData<List<StickerPacks>> getStickerPacksBean = new MutableLiveData<>();
//    public final MutableLiveData<List<StickerPacks.Stickers>> getStickersBean = new MutableLiveData<>();


    public MutableLiveData<List<StickerPacks>> getStickerPacksBean(){
        if (spLiveData == null){
            spLiveData = new MutableLiveData<>();
//            spLiveData.setValue(stickerPacksList);
        }
        return spLiveData;
    }




    public void requestInitialPackData(){

        baseRepository = BaseRepository.getInstance();
        baseRepository.getPackBean().enqueue(new Callback<PackBean>() {
            @Override
            public void onResponse(Call<PackBean> call, Response<PackBean> response) {
                PackBean packBean = response.body(); //requestInitialPackData()

                if (packBean != null){
                    Log.e("###", "getTotalPages: " + packBean.getData().getTotalPages());
                    LSMKVUtil.put("totalPages", packBean.getData().getTotalPages());

                    if (packBean.getData().getStickerPacksList() != null){

                        for (StickerPacks spData: packBean.getData().getStickerPacksList()) {
                            stickerPacksList.add(spData);
                        }
                        //只有重新进入这个fragment中才会启动
//                        if (packBean.getData().getTotalPages()!= 1){
//                            requestSurplusPackData();
//                        }

                        spLiveData.setValue(stickerPacksList);
//                        sLiveData.setValue(stickersList);
                    }

                }
            }

            @Override
            public void onFailure(Call<PackBean> call, Throwable t) {
                Log.e("###", "onFailure: " + t.getMessage());
            }
        });

    }

    public void requestSurplusPackData(){
        Log.e("###", "requestSurplusPackData: ");

        baseRepository.getNextPageData().enqueue(new Callback<PackBean>() {
            @Override
            public void onResponse(Call<PackBean> call, Response<PackBean> response) {
                PackBean packBean = response.body();  //requestInitialPackData()

                if (packBean != null){
                    if (packBean.getData().getStickerPacksList() != null){
                        for (StickerPacks spData: packBean.getData().getStickerPacksList()) {
                            Log.e("###", "onResponse spData: ");
                            stickerPacksList.add(spData);
                        }
                        LSMKVUtil.put("refreshFinish", true);
//                        spLiveData.setValue(stickerPacksList);
                    }
                }
            }

            @Override
            public void onFailure(Call<PackBean> call, Throwable t) {

            }
        });
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
