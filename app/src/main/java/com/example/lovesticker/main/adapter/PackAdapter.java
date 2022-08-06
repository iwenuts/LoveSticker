package com.example.lovesticker.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.details.activity.PackDetailsActivity;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.io.Serializable;
import java.util.List;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.ViewHolder> {
    private PackViewModel packViewModel;
    private List<StickerPacks> allStickerPacks;
    private Context context;
    private Activity activity;
    private StickerPacks stickerPacks;
    private OnItemClickListener listener;

    public PackAdapter(List<StickerPacks> allStickerPacks, PackViewModel packViewModel, Context context, Activity activity) {
        this.allStickerPacks = allStickerPacks;
        this.packViewModel = packViewModel;
        this.context = context;
        this.activity = activity;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView packNumber;
        LinearLayout packPackage;
        ImageView newImage;
        ImageView img1, img2, img3, img4;
        FrameLayout frameLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemName = view.findViewById(R.id.item_name);
            packNumber = view.findViewById(R.id.pack_number);
            packPackage = view.findViewById(R.id.pack_package);
            img1 = view.findViewById(R.id.img1);
            img2 = view.findViewById(R.id.img2);
            img3 = view.findViewById(R.id.img3);
            img4 = view.findViewById(R.id.img4);
            newImage = view.findViewById(R.id.new_image);
            frameLayout = view.findViewById(R.id.ad_container);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int itemPosition);

    }

    private void setItemListener(OnItemClickListener itemListener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pack_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.packPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LSMKVUtil.put("isStickerClear",true);
//                Log.e("###", "onClick: " + holder.getAdapterPosition());
                Intent intent = new Intent(activity, PackDetailsActivity.class);
                intent.putExtra("stickerPack_value", (Serializable) allStickerPacks.get(holder.getAdapterPosition()));
                intent.putExtra("stickerPack_number", packViewModel.getEachPackNumber().get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        stickerPacks = allStickerPacks.get(position);  //返回stickerPacks里面的数据

        if (position == 3) {
            if (LSMKVUtil.getBoolean("loadad", true)) {
                holder.frameLayout.setVisibility(View.VISIBLE);
                MaxADManager.loadMrecIntoView((AppCompatActivity) activity, holder.frameLayout);
            }else {
                holder.frameLayout.setVisibility(View.GONE);
            }

        } else {
            holder.frameLayout.setVisibility(View.GONE);
        }


        holder.itemName.setText(stickerPacks.getTitle());


        if (stickerPacks.getIsNew() == 1) {
            holder.newImage.setVisibility(View.VISIBLE);
        } else {
            holder.newImage.setVisibility(View.GONE);
        }


        holder.packNumber.setText(packViewModel.getEachPackNumber().get(position) + " Stickers");


        if (stickerPacks.getStickersList().size() != 0) {
            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(0).getImage())
                    .into(holder.img1);

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(1).getImage())
                    .into(holder.img2);

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(2).getImage())
                    .into(holder.img3);

            Glide.with(context)
                    .load(LSConstant.image_uri + stickerPacks.getStickersList().get(3).getImage())
                    .into(holder.img4);
        }


    }

    @Override
    public int getItemCount() {
        return allStickerPacks.size();
    }

}
