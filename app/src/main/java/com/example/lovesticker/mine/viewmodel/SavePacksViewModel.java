package com.example.lovesticker.mine.viewmodel;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.room.SaveDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SavePacksViewModel extends BaseViewModel {
    public MutableLiveData<List<StickerPacks>> savePackLiveData =  new MutableLiveData<>();
    public List<StickerPacks> saveData = new ArrayList<>();

    public void getGsonData(){
        new Thread(() -> {
            List<String> getSaveGson = SaveDatabase.getInstance().getUserDao().getSavePackData();
            if (saveData.size() > 0){ //刷新收藏页面时，清除List之前保存的数据，之后重新加载更改后的数据
                saveData.clear();
            }

            if (getSaveGson != null){
                Log.e("###", "saveGsonData Size: "+ getSaveGson.size());
                Gson gson = new Gson();
                for(int i = 0;i< getSaveGson.size();i++){
                    StickerPacks stickerPacks =  gson.fromJson(getSaveGson.get(i),StickerPacks.class);
                    saveData.add(stickerPacks);
                }
                savePackLiveData.postValue(saveData);
            }

        }).start();
    }
}
