package com.example.lovesticker.util.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lovesticker.main.model.StickerPacks;
import java.util.List;

//数据访问对象
@Dao
public interface SaveDao {

    @Insert
    void insert(SaveData... saveData);

    @Query("DELETE FROM SaveData where savePackGson=:savePackGson") //删除stickerPacks项的全部数据
    void deleteSavePackGson(String savePackGson);

    @Update
    void update(SaveData... saveData);

    @Query("SELECT * FROM SaveData WHERE savePackId=:savePackId")  //查询stickerPacks项的全部数据
    List<SaveData> getSavePackGson(int savePackId);


    @Query("SELECT * FROM SaveData ORDER BY ID DESC")
    List<SaveData> getSaveGsonData();

    @Query("SELECT savePackGson FROM SaveData ORDER BY ID DESC")
    List<String> getSavePackData();

}
