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

public class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.ViewHolder> {
    private List<AllAnimatedBean.Postcards> allPostcards;
    private Context context;
    private AllAnimatedBean.Postcards postcards;
    private String imageJPG;
    private String detailsImage;
    private Activity activity;
    private final OnPositionClickedListener onPositionClickedListener;

    public AnimationAdapter(List<AllAnimatedBean.Postcards> allPostcards, Context context, Activity activity,
                            OnPositionClickedListener onPositionClickedListener) {
        this.allPostcards = allPostcards;
        this.context = context;
        this.activity = activity;
        this.onPositionClickedListener = onPositionClickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        FrameLayout frameLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            img = view.findViewById(R.id.image);
            frameLayout = view.findViewById(R.id.ad_container);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("###", "holder.getAdapterPosition(): " + holder.getAdapterPosition() );

                detailsImage = allPostcards.get(holder.getAdapterPosition()).getImage();

                Intent intent = new Intent(context, StickersDetailsActivity.class);
                intent.putExtra("image",detailsImage);
                intent.putExtra("id",allPostcards.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        postcards = allPostcards.get(position);
        onPositionClickedListener.onPositionClicked(position);

        if (position == 6){
            if (LSMKVUtil.getBoolean("loadad",true)){
                holder.frameLayout.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                MaxADManager.loadMrecIntoView((AppCompatActivity) activity,holder.frameLayout);
            }else {
                holder.frameLayout.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
            }

        }else {
            holder.frameLayout.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
        }


        if (postcards != null){
            imageJPG = postcards.getImage().replaceAll("gif","jpg");
            Glide.with(context)
                    .load(LSConstant.image_jpg_uri + imageJPG)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.drawable.image_failed)
                    .into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        return allPostcards.size();
    }

    public interface OnPositionClickedListener {
        void onPositionClicked(int position);
    }


}
