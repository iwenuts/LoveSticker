package com.example.lovesticker.base;

import android.util.Log;

import com.example.lovesticker.main.model.PackBean;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BaseRepository {
    public final static String BASE_URL = "https://postcard.wastickersapp.com/";
    private ApiService apiService;
    private int currentPage = 1;
    private int currentAllAllAnimated = 1;
    private int currentSingleAnimated = 1;
    private int totalPages;
    private int allAnimatedTotalPages;
    private int singleAnimatedTotalPages;





    private BaseRepository() {
//        Retrofit builder = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
//                .build();
//
//        apiService = builder.create(ApiService.class);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()));


        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest = request.newBuilder()
                            .addHeader("token","ookgroup")
                            .addHeader("lang","en")
                            .addHeader("apptype","android")
                            .addHeader("appversion","1.5.5")
                            .addHeader("appname","com.wastickerapps.whatsapp.stickers")
                            .build();
                    return chain.proceed(newRequest);
                });

        Retrofit mRetrofit = builder.client(mBuilder.build()).build();

        apiService = mRetrofit.create(ApiService.class);
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
        return apiService.getAllPack(1,8);
    }

    public Call<PackBean> getNextPageData() {
        totalPages = LSMKVUtil.getInt("totalPages",1);
//        if(currentPage >= totalPages) return null;
        currentPage = currentPage + 1;
        return apiService.getAllPack(currentPage,8);
    }

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String data = dateFormat.format(date.getTime());


    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    String time = timeFormat.format(date.getTime());


    public Call<AllAnimatedBean> getAllAnimatedStickersData(){
        return apiService.getAllAnimatedStickers(1,36,data,time);
    }

    public Call<AllAnimatedBean> getNextAllAllAnimatedStickersData(){
        allAnimatedTotalPages = LSMKVUtil.getInt("allAnimatedTotalPages",1);
//        if(currentPage >= allAnimatedTotalPages) return null;
        currentAllAllAnimated = currentAllAllAnimated + 1;
        return apiService.getAllAnimatedStickers(currentAllAllAnimated,36,data,time);
    }


    public Call<AnimatedCategoriesBean> getAllAnimatedCategoriesData(){
        return apiService.getAnimatedStickersCategories();
    }

    public Call<SingleAnimatedCategoriesBean> getSingleAnimatedCategoriesData(String link){
        return apiService.getSingleAnimatedStickersCategories(link,36,1);
    }

    public Call<SingleAnimatedCategoriesBean> getNextSingleAnimatedCategoriesData(String link){
        singleAnimatedTotalPages = LSMKVUtil.getInt("singleAnimatedTotalPages",1);
//        if (currentPage >= singleAnimatedTotalPages) return null;
        currentSingleAnimated = currentSingleAnimated + 1;
        return apiService.getSingleAnimatedStickersCategories(link,36,currentSingleAnimated);
    }

    public interface ApiService {

//        @Headers({
//                "token: ookgroup",
//                "lang: en",
//                "apptype: android",
//                "appversion: 1.5.5",
//                "appname: com.wastickerapps.whatsapp.stickers"
//        })

        @GET("v0/postcards/get-stickers")
        Call<PackBean> getAllPack(@Query("page") int page,@Query("limit") int limit);


        @GET("v0/postcards/categories/page/home")
        Call<AllAnimatedBean>getAllAnimatedStickers(@Query("page") int page,@Query("limit") int limit,
                                                    @Query("date") String data,@Query("time") String time);


        @GET("v0/postcards/menus/mobile-menu/categories")
        Call<AnimatedCategoriesBean>getAnimatedStickersCategories();


        @GET("v0/postcards/get-by")
        Call<SingleAnimatedCategoriesBean> getSingleAnimatedStickersCategories(@Query("fullSlug") String fullSlug,
                                                 @Query("limit")int limit,@Query("page") int page);

    }


}
