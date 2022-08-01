package com.example.lovesticker.main.activity;

import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.main.model.LoveStickerBean;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class LoveStickerData {
    public final static String Love_Sticker_URL = "https://lovesticker.oss-us-west-1.aliyuncs.com/";
    private ApiService loveStickerAPI;

    private static LoveStickerData instance = null;


    public static LoveStickerData getInstance() {

        if (null == instance) instance = new LoveStickerData();
        return instance;
    }


    public LoveStickerData() {
        Retrofit loveStickerBuilder = new Retrofit.Builder()
                .baseUrl(Love_Sticker_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();

        loveStickerAPI =  loveStickerBuilder.create(ApiService.class);
    }

    public Call<LoveStickerBean> getLoveStickerData(){
        return loveStickerAPI.getLoveSticker();
    }


    public interface ApiService {
        @GET("lovesticker.json")
        Call<LoveStickerBean> getLoveSticker();
    }

}
