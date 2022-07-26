package com.example.lovesticker.util.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SaveStickerData {

    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;

    private int savePostcardId;

    private String savePostcardsImg;

    public SaveStickerData(int savePostcardId, String savePostcardsImg) {
        this.savePostcardId = savePostcardId;
        this.savePostcardsImg = savePostcardsImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSavePostcardId() {
        return savePostcardId;
    }

    public void setSavePostcardId(int savePostcardId) {
        this.savePostcardId = savePostcardId;
    }

    public String getSavePostcardsImg() {
        return savePostcardsImg;
    }

    public void setSavePostcardsImg(String savePostcardsImg) {
        this.savePostcardsImg = savePostcardsImg;
    }
}
