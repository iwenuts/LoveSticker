package com.example.lovesticker.mine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.lovesticker.details.activity.PackDetailsActivity;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.StickerPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavedPackAdapter extends RecyclerView.Adapter<SavedPackAdapter.ViewHolder> {
    private List<StickerPacks> spList;
    private StickerPacks stickerPacks;
    private Context context;

    private final OnAddButtonClickedListener onAddButtonClickedListener;

    public SavedPackAdapter(List<StickerPacks> spList, Context context, OnAddButtonClickedListener onAddButtonClickedListener) {
        this.spList = spList;
        this.context = context;
        this.onAddButtonClickedListener = onAddButtonClickedListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView packNumber;
        ImageView img1,img2,img3,packAdd,packNoAdd;
        ConstraintLayout packPackage;
        public ViewHolder(@NonNull View view) {
            super(view);
            itemName = view.findViewById(R.id.save_pack_title);
            packNumber = view.findViewById(R.id.save_pack_number);
            img1 = view.findViewById(R.id.img1);
            img2 = view.findViewById(R.id.img2);
            img3 = view.findViewById(R.id.img3);
            packAdd = view.findViewById(R.id.pack_add);
            packNoAdd = view.findViewById(R.id.pack_no_add);
            packPackage = view.findViewById(R.id.pack_package);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_pack_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.packNoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("###", "getAdapterPosition() : "+ holder.getAdapterPosition());

//                Log.e("###", "onAddButtonClickedListener : " + onAddButtonClickedListener);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Would you like to add Lovely！to WhatsApp?")
                        .setCancelable(false)
                        .setPositiveButton("ADD",((dialog, which) -> {
                            onAddButtonClickedListener.onAddButtonClicked(spList.get(holder.getAdapterPosition()),holder.getAdapterPosition() );
                            dialog.dismiss();

                        })).setNegativeButton("CANCEL",((dialog, which) -> dialog.dismiss()));
                alertDialog.show();
            }
        });

        holder.packPackage.setOnClickListener(v -> {
            Intent intent = new Intent(context, PackDetailsActivity.class);
            intent.putExtra("saveStickerPack", (Serializable) spList.get(holder.getAdapterPosition()));
            intent.putExtra("saveStickerPackNumber",spList.get(holder.getAdapterPosition()).getStickersList().size());
            intent.putExtra("isSaved",true);
            context.startActivity(intent);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        stickerPacks = spList.get(position);
        if (stickerPacks != null && spList.size() > 0){

            holder.packNumber.setText(stickerPacks.getStickersList().size() + " Stickers");

            holder.itemName.setText(stickerPacks.getTitle());

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(0).getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .into(holder.img1);

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(1).getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .into(holder.img2);

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(2).getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .into(holder.img3);


            boolean isWhitelisted = WhitelistCheck.isWhitelisted(context, stickerPacks.getIdentifier());
//            for (StickerPack stickerPack : stickerPackList) {
//                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(context, stickerPacks.getIdentifier()));
//
//                if (stickerPack.getIsWhitelisted()) {
//                    holder.packAdd.setVisibility(View.VISIBLE);
//                    holder.packAdd.setImageResource(R.drawable.finished_adding);
//                    holder.packNoAdd.setVisibility(View.GONE);
//                } else {
//                    holder.packAdd.setVisibility(View.GONE);
//                    holder.packNoAdd.setVisibility(View.VISIBLE);
//                    holder.packNoAdd.setImageResource(R.drawable.add_pack);
//                }
//            }

            if (isWhitelisted){
                holder.packAdd.setVisibility(View.VISIBLE);
                holder.packAdd.setImageResource(R.drawable.finished_adding);
                holder.packNoAdd.setVisibility(View.GONE);
            }else {
                holder.packAdd.setVisibility(View.GONE);
                holder.packNoAdd.setVisibility(View.VISIBLE);
                holder.packNoAdd.setImageResource(R.drawable.add_pack);
            }

        }

    }

    @Override
    public int getItemCount() {
        return spList.size();
    }

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(StickerPacks stickerPack, int index);
    }
}
