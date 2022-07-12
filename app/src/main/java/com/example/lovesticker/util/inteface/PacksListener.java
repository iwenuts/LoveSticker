package com.example.lovesticker.util.inteface;

import com.example.lovesticker.main.model.StickerPacks;

import java.util.List;

public interface PacksListener {
    void onStickerPacks(List<StickerPacks> stickerPacks);

    void onStickers(List<StickerPacks.Stickers> sticker);
}
