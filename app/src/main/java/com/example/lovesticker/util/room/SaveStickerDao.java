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

    @Query("DELETE FROM SaveStickerData where savePostcardsImg=:savePostcardsImg")
    void deleteSavePostcardsGson(String savePostcardsImg);

    @Update
    void update(SaveStickerData... saveStickerData);

    @Query("SELECT * FROM SaveStickerData WHERE savePostcardId=:savePostcardId")
    List<SaveStickerData> getSavePostcardsGson(int savePostcardId);

    @Query("SELECT savePostcardsImg FROM SaveStickerData ORDER BY ID DESC")
    List<String> getSavePostcardsData();

}
