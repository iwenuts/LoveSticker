package com.example.lovesticker.sticker.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseRepository;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.main.fragment.StickerFragment;
import com.example.lovesticker.sticker.fragment.LoveFragment;
import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingCategoriesAdapter extends RecyclerView.Adapter<LoadingCategoriesAdapter.ViewHolder> {
    private List<AnimatedCategoriesBean.CategoriesData> categoriesData;
    private AnimatedCategoriesBean.CategoriesData data;
    private Context context;

    public LoadingCategoriesAdapter(List<AnimatedCategoriesBean.CategoriesData> categoriesData, Context context) {
        this.categoriesData = categoriesData;
        this.context = context;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView categories_img;
        ConstraintLayout categories;
        public ViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.categories_text);
            categories_img = view.findViewById(R.id.categories_img);
            categories = view.findViewById(R.id.categories);
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_categories_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StickerFragment.newInstance().getPosition(holder.getAdapterPosition());  //获取单个种类所在的位置

                if (Activity.class.isInstance(context)){ //关闭当前Activity
                    Activity activity = (Activity) context;
                    activity.finish();
                }


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        data = categoriesData.get(position);

//        new LoveFragment().setGetListener(new ImageListener() {
//            @Override
//            public void onImage(List<SingleAnimatedCategoriesBean.Postcards> postcards) {
//                Log.e("###", "111postcards: " + postcards);
//                if (postcards != null) {
//                    String postcardsImg = postcards.get(0).getImage().replaceAll("gif","jpg");
//                    Log.e("###", "postcardsImg: " + postcardsImg);
//
////                    holder.categories_img.setImageDrawable(Drawable.createFromPath(LSConstant.image_jpg_uri + postcardsImg));
//                    Glide.with(LoveStickerApp.getAppContext())
//                            .load(LSConstant.image_jpg_uri + postcardsImg)
//                            .into(holder.categories_img);
//                }
//
//            }
//        });

//        BaseRepository.getInstance().getSingleAnimatedCategoriesData(data.getLink()).enqueue(new Callback<SingleAnimatedCategoriesBean>() {
//            @Override
//            public void onResponse(Call<SingleAnimatedCategoriesBean> call, Response<SingleAnimatedCategoriesBean> response) {
//                Log.e("###", "onResponse: " + response.body());
//
//                if (response.body() != null){
//                    String categoriesImage = response.body().getData().getPostcardsList().get(0).getImage().replaceAll("gif","jpg");
//                    Glide.with(context)
//                            .load(LSConstant.image_jpg_uri + categoriesImage)
//                            .into(holder.categories_img);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<SingleAnimatedCategoriesBean> call, Throwable t) {
//
//            }
//        });


        holder.text.setText(data.getTitle());


    }

    @Override
    public int getItemCount() {
        return categoriesData.size();
    }




}
