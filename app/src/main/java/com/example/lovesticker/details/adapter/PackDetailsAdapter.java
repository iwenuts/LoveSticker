package com.example.lovesticker.details.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.details.activity.PackImageDetailsActivity;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import org.w3c.dom.Text;

public class PackDetailsAdapter extends RecyclerView.Adapter<PackDetailsAdapter.ViewHolder> {
    private StickerPacks imageDetails;
    private  Context context;
    private Integer stickerPackNumber; //图片总数
    public PackDetailsAdapter(StickerPacks imageDetails, Context context,Integer stickerPackNumber) {
        this.imageDetails = imageDetails;
        this.context = context;
        this.stickerPackNumber = stickerPackNumber;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pack_details_item,parent,false);
        ViewHolder holder = new ViewHolder(view);


        holder.img.setOnClickListener(v -> {
//                Log.e("###", "imagePosition: " + holder.getAdapterPosition());
            LSMKVUtil.put("IsStickerDetailsClear", true);
            Intent intent = new Intent(context, PackImageDetailsActivity.class);
            intent.putExtra("position",holder.getAdapterPosition());
            intent.putExtra("packDetails_value",imageDetails);
            intent.putExtra("stickerPackNumber",stickerPackNumber);
            context.startActivity(intent);
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StickerPacks.Stickers stickers = imageDetails.getStickersList().get(position);
//        Log.e("###", "stickersImage: " + stickers.getImage());
//        Log.e("###", "postionss: " + position);

        if (stickers.getImage() != null){
            Glide.with(context)
                    .load(LSConstant.image_uri + stickers.getImage())
                    .into(holder.img);

        }

    }

    @Override
    public int getItemCount() {
        return imageDetails.getStickersList().size();
    }


}
