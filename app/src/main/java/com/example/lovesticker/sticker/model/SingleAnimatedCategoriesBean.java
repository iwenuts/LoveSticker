package com.example.lovesticker.sticker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleAnimatedCategoriesBean {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private int totalPostcards;
        private int totalPages;

        @SerializedName("category")
        private Category category;

        @SerializedName("postcards")
        private List<Postcards> postcardsList;

        public int getTotalPostcards() {
            return totalPostcards;
        }

        public void setTotalPostcards(int totalPostcards) {
            this.totalPostcards = totalPostcards;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public List<Postcards> getPostcardsList() {
            return postcardsList;
        }

        public void setPostcardsList(List<Postcards> postcardsList) {
            this.postcardsList = postcardsList;
        }
    }

    public static class Category{
        private int id;
        private String title;
        private String shortTitle;
        private String fullSlug;

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

        public String getShortTitle() {
            return shortTitle;
        }

        public void setShortTitle(String shortTitle) {
            this.shortTitle = shortTitle;
        }

        public String getFullSlug() {
            return fullSlug;
        }

        public void setFullSlug(String fullSlug) {
            this.fullSlug = fullSlug;
        }
    }

    public static class Postcards {
        private int id;
        private int categoryId;
        private String categoryFullSlug;
        private String title;
        private String image;
        private Boolean hasGif;
        private Boolean hasJpeg;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryFullSlug() {
            return categoryFullSlug;
        }

        public void setCategoryFullSlug(String categoryFullSlug) {
            this.categoryFullSlug = categoryFullSlug;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Boolean getHasGif() {
            return hasGif;
        }

        public void setHasGif(Boolean hasGif) {
            this.hasGif = hasGif;
        }

        public Boolean getHasJpeg() {
            return hasJpeg;
        }

        public void setHasJpeg(Boolean hasJpeg) {
            this.hasJpeg = hasJpeg;
        }
    }









}




