package com.example.lovesticker.main.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickersCategoriesBean {

    @SerializedName("data")
    List<StickersData> stickersData;

    public static class StickersData{
        private String link;
        private String icon;
        private String title;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
