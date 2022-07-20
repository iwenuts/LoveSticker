package com.example.lovesticker.sticker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.details.activity.AnimationDetailsActivity;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.util.constant.LSConstant;

import java.util.List;

public class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.ViewHolder> {
    private List<AllAnimatedBean.Postcards> allPostcards;
    private Context context;
    private AllAnimatedBean.Postcards postcards;
    private String imageJPG;
    private String detailsImage;

    public AnimationAdapter(List<AllAnimatedBean.Postcards> allPostcards, Context context) {
        this.allPostcards = allPostcards;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ViewHolder(@NonNull View view) {
            super(view);
            img = view.findViewById(R.id.image);
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
                detailsImage = allPostcards.get(holder.getAdapterPosition()).getImage();

                Intent intent = new Intent(context, AnimationDetailsActivity.class);
                intent.putExtra("detailsImage",detailsImage);
                context.startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        postcards = allPostcards.get(position);

        if (postcards != null){
            imageJPG = postcards.getImage().replaceAll("gif","jpg");

            Glide.with(context)
                    .load(LSConstant.image_jpg_uri + imageJPG)
                    .into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        return allPostcards.size();
    }


}
