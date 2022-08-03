package com.example.lovesticker.main.model;

public class LoveStickerBean {
    private int rewardinter;
    private int uv;
    private String content;
    private String pkg;
    private Boolean isForce;
    private Boolean loadad;


    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public Boolean getForce() {
        return isForce;
    }

    public void setForce(Boolean force) {
        isForce = force;
    }

    public Boolean getLoadad() {
        return loadad;
    }

    public void setLoadad(Boolean loadad) {
        this.loadad = loadad;
    }

    public int getRewardinter() {
        return rewardinter;
    }

    public void setRewardinter(int rewardinter) {
        this.rewardinter = rewardinter;
    }
}
