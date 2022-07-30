package com.example.lovesticker.sticker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.lovesticker.details.activity.SingleAnimatedDetailsActivity;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;

import java.util.List;

public class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.ViewHolder> {
    private List<SingleAnimatedCategoriesBean.Postcards> categorPostcards;
    private SingleAnimatedCategoriesBean.Postcards postcards;
    private Context context;
    private String singleAnimatedDetailsImage;
    private final AnimationAdapter.OnPositionClickedListener onPositionClickedListener;
    private Activity activity;

    public LoveAdapter(List<SingleAnimatedCategoriesBean.Postcards> categorPostcards, Context context,
                       Activity activity, AnimationAdapter.OnPositionClickedListener onPositionClickedListener) {
        this.categorPostcards = categorPostcards;
        this.context = context;
        this.activity = activity;
        this.onPositionClickedListener = onPositionClickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        FrameLayout frameLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            frameLayout = view.findViewById(R.id.ad_container);
            img = view.findViewById(R.id.image);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.love_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleAnimatedDetailsImage = categorPostcards.get(holder.getAdapterPosition()).getImage();

                Intent intent = new Intent(context, SingleAnimatedDetailsActivity.class);
                intent.putExtra("singleAnimatedDetailsImage",singleAnimatedDetailsImage);
                intent.putExtra("singlePostcards",categorPostcards.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        postcards = categorPostcards.get(position);
        onPositionClickedListener.onPositionClicked(position);

        if (postcards != null){

            if (position == 6){
                holder.frameLayout.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                MaxADManager.loadMrecIntoView((AppCompatActivity) activity,holder.frameLayout);

            }else {
                holder.frameLayout.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
            }

            String imageJPG = postcards.getImage().replaceAll("gif","jpg");

            Glide.with(context)
                    .load(LSConstant.image_jpg_uri + imageJPG)
                    .into(holder.img);

//            Log.e("###", "imageJPG: " + LSConstant.image_jpg_uri + imageJPG );
        }


    }

    @Override
    public int getItemCount() {
        return categorPostcards.size();
    }

    public interface OnPositionClickedListener {
        void onPositionClicked(int position);
    }


}
