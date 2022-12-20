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
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.List;

public class AnimationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AllAnimatedBean.Postcards> allPostcards;
    private Context context;
    private AllAnimatedBean.Postcards postcards;
    private String imageJPG;
    private String detailsImage;
    private Activity activity;

    public AnimationAdapter(List<AllAnimatedBean.Postcards> allPostcards, Context context, Activity activity) {
        this.allPostcards = allPostcards;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_item, parent, false);
            return new ItemViewOneHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_ad_item, parent, false);
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
//            onPositionClickedListener.onPositionClicked(position);

            if (position > 6) {
                postcards = allPostcards.get(position -1);

                if (postcards != null) {
                    imageJPG = postcards.getImage().replaceAll("gif", "jpg");
                    Glide.with(context)
                            .load(LSConstant.image_jpg_uri + imageJPG)
                            .placeholder(R.mipmap.ic_launcher_foreground)
                            .error(R.drawable.image_failed)
                            .into(itemViewOneHolder.img);
                }

                itemViewOneHolder.img.setOnClickListener(v -> {
                    detailsImage = allPostcards.get(position -1).getImage();

                    Intent intent = new Intent(context, StickersDetailsActivity.class);
                    intent.putExtra("image", detailsImage);
                    intent.putExtra("id", allPostcards.get(position -1).getId());
                    context.startActivity(intent);
                });

            } else {
                postcards = allPostcards.get(position);

                if (postcards != null) {
                    imageJPG = postcards.getImage().replaceAll("gif", "jpg");
                    Glide.with(context)
                            .load(LSConstant.image_jpg_uri + imageJPG)
                            .placeholder(R.mipmap.ic_launcher_foreground)
                            .error(R.drawable.image_failed)
                            .into(itemViewOneHolder.img);
                }

                itemViewOneHolder.img.setOnClickListener(v -> {
                    detailsImage = allPostcards.get(position).getImage();

                    Intent intent = new Intent(context, StickersDetailsActivity.class);
                    intent.putExtra("image", detailsImage);
                    intent.putExtra("id", allPostcards.get(position).getId());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return allPostcards.size() > 0 ? allPostcards.size() + 1 : 0;
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
