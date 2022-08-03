package com.example.lovesticker.sticker.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AnimatedCategoriesBean {

    @SerializedName("data")
    private List<CategoriesData> data;

    public List<CategoriesData> getData() {
        return data;
    }

    public void setData(List<CategoriesData> data) {
        this.data = data;
    }

    public static class CategoriesData implements Serializable {
        private String icon;
        private String title;
        private int id;
        private String type;
        private String link;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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
