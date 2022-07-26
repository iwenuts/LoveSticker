package com.example.lovesticker.util.room;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.lovesticker.main.model.StickerPacks;

@Entity
public class SaveData {

    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;

    private int savePackId;

    private String savePackGson;

    public SaveData(int savePackId, String savePackGson) {
        this.savePackId = savePackId;
        this.savePackGson = savePackGson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSavePackGson() {
        return savePackGson;
    }

    public void setSavePackGson(String savePackGson) {
        this.savePackGson = savePackGson;
    }

    public int getSavePackId() {
        return savePackId;
    }

    public void setSavePackId(int savePackId) {
        this.savePackId = savePackId;
    }

}
