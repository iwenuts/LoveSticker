package com.example.lovesticker.util.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//创建数据库
@Database(entities = {SaveData.class,SaveStickerData.class},version = 1,exportSchema = false)
public abstract class SaveDatabase extends RoomDatabase {
    private static final String DB_NAME = "SaveDatabase.db";
    private static volatile SaveDatabase instance;

    public static synchronized SaveDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static SaveDatabase create(final Context context) {
        return Room.databaseBuilder(context,
                SaveDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public abstract SaveDao getUserDao();

    public abstract SaveStickerDao getStickerDao();

}
