package com.example.lovesticker.mine.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.util.room.SaveData;
import com.example.lovesticker.util.room.SaveDatabase;

import java.util.ArrayList;
import java.util.List;

public class SavedStickerViewModel extends BaseViewModel {
    private MutableLiveData<List<String>> savedStickerLiveData;
    List<String> saveImgData = new ArrayList<>();

    public MutableLiveData<List<String>> getSavedStickerLiveData() {
        if (savedStickerLiveData == null) {
            savedStickerLiveData = new MutableLiveData<>();
        }
        return savedStickerLiveData;
    }


    public void getImageData(Context context) {
        Handler handler = new Handler(msg -> {
            if (msg.what == 1) {
                List<String> saveGsonData = (List<String>) msg.obj;

                if (saveImgData != null){  //刷新收藏页面时，清除List之前保存的数据，之后重新加载更改后的数据
                    saveImgData.clear();
                }
                if (saveGsonData != null) {
                    Log.e("###", "getImageData Size: " + saveGsonData.size() );
                    for (int i = 0; i < saveGsonData.size(); i++) {
                        String savePostcardsImg = saveGsonData.get(i);
                        saveImgData.add(savePostcardsImg);
                    }
                    savedStickerLiveData.setValue(saveImgData);

                }

            }
            return false;
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> getSaveGson = SaveDatabase.getInstance(context).getStickerDao().getSavePostcardsData();

                Message msg = new Message();
                msg.what = 1;
                msg.obj = getSaveGson;
                handler.sendMessage(msg);
            }
        }).start();

    }


}
