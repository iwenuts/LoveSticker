package com.example.lovesticker.base;

import android.util.Log;

import com.example.lovesticker.main.model.PackBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class BaseRepository {
    public final static String BASE_URL = "https://postcard.wastickersapp.com/";
    private ApiService apiService;
    private int currentPage = 1;
    private int totalPages;

    private BaseRepository() {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();

//        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
//                .addInterceptor(chain -> {
//                    Request request = chain.request();
//                    Request newRequest = request.newBuilder()
//                            .addHeader("token","ookgroup")
//                            .addHeader("lang","en")
//                            .addHeader("apptype","android")
//                            .addHeader("appversion","1.5.5")
//                            .addHeader("appname","com.wastickerapps.whatsapp.stickers")
//                            .build();
//                    return chain.proceed(newRequest);
//                });
//
//        Retrofit mRetrofit = builder.client(mBuilder.build()).build();

        apiService = builder.create(ApiService.class);
    }

    private static final BaseRepository instance = new BaseRepository();

    public static BaseRepository getInstance() {
        return instance;
    }

    private PackBean packBean;

    public void setPackBean(PackBean packBean){
        this.packBean = packBean;
    }

    public PackBean getLocalPackBean(){
        return packBean;
    }


    public Call<PackBean> getPackBean(){
        Log.e("###", "getPackBean: ");
        return apiService.getAllPack(currentPage,8);
    }

    public Call<PackBean> getNextPageData() {
        totalPages = LSMKVUtil.getInt("totalPages",1);
        if(currentPage >= totalPages) return null;
        return apiService.getAllPack(currentPage++,8);
    }

    public interface ApiService {

        @Headers({
                "token: ookgroup",
                "lang: en",
                "apptype: android",
                "appversion: 1.5.5",
                "appname: com.wastickerapps.whatsapp.stickers"
        })
        @GET("v0/postcards/get-stickers")
        Call<PackBean> getAllPack(@Query("page") int page,@Query("limit") int limit);

//        Call<>get
    }





}
