package com.example.lovesticker.util.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadStickerPacks {
    @SerializedName("stickers")
    private List<StickerPacks> stickerPacks;

    @SerializedName("androidPlayStoreLink")
    private String androidLink;

    @SerializedName("iosAppStoreLink")
    private String iosLink;

    public List<StickerPacks> getStickerPacks() {
        return stickerPacks;
    }

    public void setStickerPacks(List<StickerPacks> stickerPacks) {
        this.stickerPacks = stickerPacks;
    }

    public String getAndroidLink() {
        return androidLink;
    }

    public void setAndroidLink(String androidLink) {
        this.androidLink = androidLink;
    }

    public String getIosLink() {
        return iosLink;
    }

    public void setIosLink(String iosLink) {
        this.iosLink = iosLink;
    }
    
    

    public static class StickerPacks{
        private String identifier;
        private String name;
        private String publisher;
        private String trayImageFile;
        private String imageDataVersion;
        private String avoidCache;
        private String publisherEmail;
        private String publisherWebsite;
        private String privacyPolicyWebsite;
        private String licenseAgreementWebsite;

        @SerializedName("stickers")
        private List<Stickers> stickersList;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }


        public String getTrayImageFile() {
            return trayImageFile;
        }

        public void setTrayImageFile(String trayImageFile) {
            this.trayImageFile = trayImageFile;
        }

        public String getImageDataVersion() {
            return imageDataVersion;
        }

        public void setImageDataVersion(String imageDataVersion) {
            this.imageDataVersion = imageDataVersion;
        }

        public String getAvoidCache() {
            return avoidCache;
        }

        public void setAvoidCache(String avoidCache) {
            this.avoidCache = avoidCache;
        }

        public String getPublisherEmail() {
            return publisherEmail;
        }

        public void setPublisherEmail(String publisherEmail) {
            this.publisherEmail = publisherEmail;
        }

        public String getPublisherWebsite() {
            return publisherWebsite;
        }

        public void setPublisherWebsite(String publisherWebsite) {
            this.publisherWebsite = publisherWebsite;
        }

        public String getPrivacyPolicyWebsite() {
            return privacyPolicyWebsite;
        }

        public void setPrivacyPolicyWebsite(String privacyPolicyWebsite) {
            this.privacyPolicyWebsite = privacyPolicyWebsite;
        }

        public String getLicenseAgreementWebsite() {
            return licenseAgreementWebsite;
        }

        public void setLicenseAgreementWebsite(String licenseAgreementWebsite) {
            this.licenseAgreementWebsite = licenseAgreementWebsite;
        }

        public List<Stickers> getStickersList() {
            return stickersList;
        }

        public void setStickersList(List<Stickers> stickersList) {
            this.stickersList = stickersList;
        }
        
    }


    public static class Stickers{
        private String imageFile;

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }
        
    }

}
