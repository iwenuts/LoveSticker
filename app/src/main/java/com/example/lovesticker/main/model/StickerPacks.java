package com.example.lovesticker.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StickerPacks implements Serializable{
    private String licenseAgreementWebsite;
    private String privacyPolicyWebsite;
    private int id;
    private String title;
    private int isNew;
    private int isFree;
    private String identifier;
    private String publisher;
    private String trayImageFile;
    private String publisherWebsite;

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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public static class Stickers implements Serializable{
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
