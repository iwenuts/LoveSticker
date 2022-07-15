package com.example.lovesticker.util.inteface;

import android.content.Intent;

import com.example.lovesticker.main.model.StickerPacks;

import java.util.List;

public interface PacksListener {
    void onPosition(Integer positon);



    void onStickers(List<StickerPacks.Stickers> sticker);
}
