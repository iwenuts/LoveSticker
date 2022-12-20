package com.example.lovesticker.sticker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.details.activity.StickersDetailsActivity;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.List;

public class LoveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SingleAnimatedCategoriesBean.Postcards> categoryPostcards;
    private SingleAnimatedCategoriesBean.Postcards postcards;
    private Context context;
    private String singleAnimatedDetailsImage;
    private Activity activity;

    public LoveAdapter(List<SingleAnimatedCategoriesBean.Postcards> categoryPostcards, Context context,
                       Activity activity ) {
        this.categoryPostcards = categoryPostcards;
        this.context = context;
        this.activity = activity;
    }

    public class ItemViewOneHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ItemViewOneHolder(@NonNull View view) {
            super(view);
            img = view.findViewById(R.id.image);

        }
    }

    public class ItemViewTwoHolder extends RecyclerView.ViewHolder {
        FrameLayout frameLayout;

        public ItemViewTwoHolder(@NonNull View view) {
            super(view);
            frameLayout = view.findViewById(R.id.ad_container);

        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.love_item, parent, false);
            return new ItemViewOneHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_love_ad, parent, false);
            return new ItemViewTwoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 6) {
            ItemViewTwoHolder itemViewTwoHolder = (ItemViewTwoHolder) holder;

            if (LSMKVUtil.getBoolean("loadad", true)) {
                itemViewTwoHolder.frameLayout.setVisibility(View.VISIBLE);
                MaxADManager.loadMrecIntoView((AppCompatActivity) activity, itemViewTwoHolder.frameLayout);
            }

        } else {
            ItemViewOneHolder itemViewOneHolder = (ItemViewOneHolder) holder;

            if (position > 6){
                postcards = categoryPostcards.get(position -1);
                if (postcards != null) {
                    String imageJPG = postcards.getImage().replaceAll("gif", "jpg");

                    Glide.with(context)
                            .load(LSConstant.image_jpg_uri + imageJPG)
                            .placeholder(R.mipmap.ic_launcher_foreground)
                            .error(R.drawable.image_failed)
                            .into(itemViewOneHolder.img);
                }



                itemViewOneHolder.img.setOnClickListener(v -> {
                    singleAnimatedDetailsImage = categoryPostcards.get(position -1).getImage();

                    Intent intent = new Intent(context, StickersDetailsActivity.class);
                    intent.putExtra("image", singleAnimatedDetailsImage);
                    intent.putExtra("id", categoryPostcards.get(position -1).getId());
                    context.startActivity(intent);
                });

            }else {
                postcards = categoryPostcards.get(position);
                if (postcards != null) {
                    String imageJPG = postcards.getImage().replaceAll("gif", "jpg");

                    Glide.with(context)
                            .load(LSConstant.image_jpg_uri + imageJPG)
                            .placeholder(R.mipmap.ic_launcher_foreground)
                            .error(R.drawable.image_failed)
                            .into(itemViewOneHolder.img);
                }



                itemViewOneHolder.img.setOnClickListener(v -> {
                    singleAnimatedDetailsImage = categoryPostcards.get(position).getImage();

                    Intent intent = new Intent(context, StickersDetailsActivity.class);
                    intent.putExtra("image", singleAnimatedDetailsImage);
                    intent.putExtra("id", categoryPostcards.get(position).getId());
                    context.startActivity(intent);
                });
            }

        }

    }

    @Override
    public int getItemCount() {
        return categoryPostcards.size() > 0 ? categoryPostcards.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 6) {
            return 1;
        } else {
            return 0;
        }
    }


    public interface OnPositionClickedListener {
        void onPositionClicked(int position);
    }
}
