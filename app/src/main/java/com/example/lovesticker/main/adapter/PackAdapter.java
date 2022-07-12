package com.example.lovesticker.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lovesticker.R;
import com.example.lovesticker.main.activity.MainActivity;
import com.example.lovesticker.main.fragment.PackFragment;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;

import java.util.List;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.ViewHolder> {
    private PackViewModel packViewModel;
    private List<StickerPacks> allStickerPacks;
    private Context context;
    private final String image_uri  = "https://cdn.wastickersapp.com/storage/stickers/webp";

    public PackAdapter(List<StickerPacks> allStickerPacks, PackViewModel packViewModel, Context context) {
        this.allStickerPacks = allStickerPacks;

        this.packViewModel = packViewModel;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView packNumber;
        LinearLayout packPackage;
        ImageView newImage;
        ImageView img1,img2,img3,img4;
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
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pack_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StickerPacks stickerPacks = allStickerPacks.get(position);  //返回stickerPacks里面的数据


        holder.itemName.setText(stickerPacks.getTitle());


        if (stickerPacks.getIsNew() == 1){
            holder.newImage.setVisibility(View.VISIBLE);
        }else {
            holder.newImage.setVisibility(View.GONE);
        }


        holder.packNumber.setText(packViewModel.getEachPackNumber().get(position) + " Stickers");

//        Log.e("###", "getImage: " + stickers.getImage());
//

        if (stickerPacks.getStickersList().size() != 0){
            Glide.with(context)
                    .load(image_uri + stickerPacks.getStickersList().get(0).getImage())
                    .into(holder.img1);

            Glide.with(context)
                    .load(image_uri + stickerPacks.getStickersList().get(1).getImage())
                    .into(holder.img2);

            Glide.with(context)
                    .load(image_uri + stickerPacks.getStickersList().get(2).getImage())
                    .into(holder.img3);

            Glide.with(context)
                    .load(image_uri + stickerPacks.getStickersList().get(3).getImage())
                    .into(holder.img4);

        }



    }

    @Override
    public int getItemCount() {
        return allStickerPacks.size();
    }

}
