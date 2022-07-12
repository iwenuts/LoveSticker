package com.example.lovesticker.main.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerPacks {
    private int id;
    private String title;
    private int isNew;
    private int isFree;

    @SerializedName("stickers")
    private List<Stickers> stickersList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public List<Stickers> getStickersList() {
        return stickersList;
    }

    public void setStickersList(List<Stickers> stickersList) {
        this.stickersList = stickersList;
    }


    public static class Stickers {
        private int id;
        private String image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
