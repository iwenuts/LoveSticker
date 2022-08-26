package com.example.lovesticker.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.details.activity.PackDetailsActivity;
import com.example.lovesticker.details.activity.SingleAnimatedDetailsActivity;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.room.InvokesData;

import java.util.List;

public class SaveStickerAdapter extends RecyclerView.Adapter<SaveStickerAdapter.ViewHolder> {
    private List<String> stringList;
    private Context context;
    private String stickerImg;


    public SaveStickerAdapter(List<String> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView deleteSticker;
        public ViewHolder(@NonNull View view) {
            super(view);
            img = view.findViewById(R.id.save_image);
            deleteSticker = view.findViewById(R.id.delete_sticker);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_sticker_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.deleteSticker.setVisibility(View.VISIBLE);
                return true;
            }
        });



        holder.deleteSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Delete this sticker？")
                        .setCancelable(false)
                        .setPositiveButton("DELETE", (dialog, which) -> {

                            InvokesData.getInvokesData(context).deleteSavePostcards(stringList.get(holder.getAdapterPosition()));

                        }).setNegativeButton("CANCEL", (dialog, which) -> {
                            holder.deleteSticker.setVisibility(View.GONE);
                            dialog.dismiss();
                        });
                alertDialog.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        stickerImg = stringList.get(position);

        holder.deleteSticker.setVisibility(View.GONE);

        if (stickerImg != null){
            String imageJPG = stickerImg.replaceAll("gif","jpg");

            Glide.with(context)
                    .load(LSConstant.image_jpg_uri + imageJPG)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .into(holder.img);

        }


    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }


}
