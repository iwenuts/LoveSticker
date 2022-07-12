package com.example.lovesticker.main.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PackBean {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private int totalPacks;
        private int totalPages;

        @SerializedName("stickerPacks")
        private List<StickerPacks> stickerPacksList;

        public int getTotalPacks() {
            return totalPacks;
        }

        public void setTotalPacks(int totalPacks) {
            this.totalPacks = totalPacks;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<StickerPacks> getStickerPacksList() {
            return stickerPacksList;
        }

        public void setStickerPacksList(List<StickerPacks> stickerPacksList) {
            this.stickerPacksList = stickerPacksList;
        }
    }


}
