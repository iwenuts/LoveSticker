package com.example.lovesticker.mine.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.room.SaveData;
import com.example.lovesticker.util.room.SaveDatabase;
import com.example.lovesticker.util.room.SaveStickerData;

import java.util.ArrayList;
import java.util.List;

class Item{
    String image;
    int mId;
}
public class SavedStickerViewModel extends BaseViewModel {
    public MutableLiveData<List<SaveStickerData>> savedStickerLiveData = new MutableLiveData<>();
    public List<SaveStickerData> saveImgData = new ArrayList<>();

    public SavedStickerViewModel(){

    }

    public void getImageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SaveStickerData> getSaveGson = InvokesData.getInvokesData().getSavePostcardsData();
                if (saveImgData != null){  //刷新收藏页面时，清除List之前保存的数据，之后重新加载更改后的数据
                    saveImgData.clear();
                }
                if (getSaveGson != null) {
                    Log.e("###", "getImageData Size: " + getSaveGson.size() );
                    saveImgData.addAll(getSaveGson);
//                    savedStickerLiveData.setValue(saveImgData);
                    savedStickerLiveData.postValue(saveImgData);
                }
            }
        }).start();
    }
}
