package com.example.lovesticker.mine.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveData;
import com.example.lovesticker.util.room.SaveDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SavePacksViewModel extends BaseViewModel {
    private MutableLiveData<List<StickerPacks>> savePackLiveData;
    Gson gson = new Gson();
    List<String> saveGsonData;
    List<StickerPacks> saveData = new ArrayList<>();


    public MutableLiveData<List<StickerPacks>> getSavePackLiveData() {
        if (savePackLiveData == null){
            savePackLiveData= new MutableLiveData<>();
        }
        return savePackLiveData;
    }

    public void getGsonData(Context context){

        Handler handler = new Handler(msg -> {
            if (msg.what == 0){
                saveGsonData = (List<String>) msg.obj;

                if (saveData != null){ //刷新收藏页面时，清除List之前保存的数据，之后重新加载更改后的数据
                    saveData.clear();
                }

                if (saveGsonData != null){
                    Log.e("###", "saveGsonData Size: "+ saveGsonData.size());

                    for(int i = 0;i< saveGsonData.size();i++){
                        StickerPacks stickerPacks =  gson.fromJson(saveGsonData.get(i),StickerPacks.class);
                        saveData.add(stickerPacks);
                    }
                    savePackLiveData.setValue(saveData);
                }
            }
            return false;
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
//                List<SaveData> getSaveGson = SaveDatabase.getInstance(context).getUserDao().savePackGson();
                List<String> getSaveGson = SaveDatabase.getInstance(context).getUserDao().getSavePackData();

                Message msg = new Message();
                msg.what = 0;
                msg.obj = getSaveGson;
                handler.sendMessage(msg);

            }
        }).start();

//        saveGsonData = InvokesData.getInvokesData(context).queryAllSaveGsonData();
//
//        if (saveGsonData != null){
//            for(int i = 0;i< saveGsonData.size();i++){
//                StickerPacks stickerPacks =  gson.fromJson(saveGsonData.get(i).getSavePackGson(),StickerPacks.class);
//                saveData.add(stickerPacks);
//            }
//            savePackLiveData.setValue(saveData);
//
//        }
    }


}
