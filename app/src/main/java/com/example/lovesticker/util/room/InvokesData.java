package com.example.lovesticker.util.room;

import com.example.lovesticker.util.event.UpdatePacksEvent;
import com.example.lovesticker.util.event.UpdateStickerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class InvokesData {
    private static InvokesData invokesData;
    private SaveDao saveDao;
    private SaveStickerDao saveStickerDao;
    private List<SaveData> savePackGson;

    public InvokesData() {
        SaveDatabase saveDatabase = SaveDatabase.getInstance();
        saveDao = saveDatabase.getUserDao();
        saveStickerDao = saveDatabase.getStickerDao();
    }

    public static InvokesData getInvokesData(){
        if (invokesData == null){
            invokesData = new InvokesData();
        }
        return invokesData;
    }

    public void insertPackData(SaveData... saveData){
        saveDao.insert(saveData);
        EventBus.getDefault().post(new UpdatePacksEvent());
    }

    public void deleteSavePacks(String savePackGson){
        saveDao.deleteSavePackGson(savePackGson);
        EventBus.getDefault().post(new UpdatePacksEvent());
    }

    public void insertStickerData(SaveStickerData... saveStickerData){
        saveStickerDao.insert(saveStickerData);
        EventBus.getDefault().post(new UpdateStickerEvent());
    }

    public void deleteSavePostcards(int savePostcardId){
        saveStickerDao.deleteSavePostcardsGson(savePostcardId);
        EventBus.getDefault().post(new UpdateStickerEvent());
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

   public Boolean querySaveStickerGson(int saveStickerId) {
       if (saveStickerDao.getSavePostcardsGson(saveStickerId) != null) {
           return saveStickerDao.getSavePostcardsGson(saveStickerId).isEmpty();
       } else {
           return true;
       }
   }

    public List<SaveStickerData> getSavePostcardsData() {
        return saveStickerDao.getSavePostcardsData();
    }
}
