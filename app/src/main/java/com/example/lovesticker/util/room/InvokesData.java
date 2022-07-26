package com.example.lovesticker.util.room;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.details.activity.PackImageDetailsActivity;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.mine.adapter.SavedPackAdapter;

import java.util.List;

public class InvokesData {
    private static InvokesData invokesData;
    private Context context;
    private SaveDao saveDao;
    private SaveStickerDao saveStickerDao;
    private List<SaveData> savePackGson;

    public InvokesData(Context context) {
        this.context = context;
        SaveDatabase saveDatabase = SaveDatabase.getInstance(context);
        saveDao = saveDatabase.getUserDao();
        saveStickerDao = saveDatabase.getStickerDao();
    }

    public static InvokesData getInvokesData(Context context){
        if (invokesData == null){
            invokesData = new InvokesData(context);
        }
        return invokesData;
    }

    public void insertPackData(SaveData... saveData){
        saveDao.insert(saveData);
//        Handler handler = new Handler(msg -> {
//            if (msg.what == 0){
//                Toast.makeText(LoveStickerApp.getAppContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        });
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                saveDao.insert(saveData);
//                Message msg = new Message();
//                msg.what = 0;
//                handler.sendMessage(msg);
//            }
//        }).start();

    }

    public void insertStickerData(SaveStickerData... saveStickerData){
        saveStickerDao.insert(saveStickerData);
    }



    public void deleteSavePacks(String savePackGson){
        saveDao.deleteSavePackGson(savePackGson);
//        Handler handler = new Handler(msg -> {
//            if (msg.what == 0){
//                Toast.makeText(LoveStickerApp.getAppContext(),"Successfully deleted!",Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                saveDao.deleteSavePackGson(savePackGson);
//                Message msg = new Message();
//                msg.what = 0;
//                handler.sendMessage(msg);
//            }
//        }).start();
    }

    public void deleteSavePostcards(String savePostcardsImg){
        saveStickerDao.deleteSavePostcardsGson(savePostcardsImg);
    }

    public Boolean querySavePackGson(int savePackId){
        if (saveDao.getSavePackGson(savePackId) != null){
//            Log.e("###", "getSavePackGson(savePackId) != null: "+ saveDao.getSavePackGson(savePackId).isEmpty());
            return saveDao.getSavePackGson(savePackId).isEmpty();
        }else {
//            Log.e("###", "getSavePackGson(savePackId) == null: ");
            return true;
        }

    }

   public Boolean querySaveStickerGson(int saveStickerId){
        if (saveStickerDao.getSavePostcardsGson(saveStickerId) != null){
            return saveStickerDao.getSavePostcardsGson(saveStickerId).isEmpty();
        }else {
            return true;
        }
   }






}
