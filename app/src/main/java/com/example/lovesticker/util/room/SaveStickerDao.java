package com.example.lovesticker.util.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao

public interface SaveStickerDao {

    @Insert
    void insert(SaveStickerData... saveStickerData);

    @Query("DELETE FROM SaveStickerData where savePostcardId=:savePostcardId")
    void deleteSavePostcardsGson(int savePostcardId);

    @Update
    void update(SaveStickerData... saveStickerData);

    @Query("SELECT * FROM SaveStickerData WHERE savePostcardId=:savePostcardId")
    List<SaveStickerData> getSavePostcardsGson(int savePostcardId);

    @Query("SELECT * FROM SaveStickerData ORDER BY ID DESC")
    List<SaveStickerData> getSavePostcardsData();
}
