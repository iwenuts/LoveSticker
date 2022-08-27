package com.example.lovesticker.util.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.lovesticker.base.LoveStickerApp;

//创建数据库
@Database(entities = {SaveData.class,SaveStickerData.class},version = 1,exportSchema = false)
public abstract class SaveDatabase extends RoomDatabase {
    private static final String DB_NAME = "SaveDatabase.db";
    private static volatile SaveDatabase instance;

    public static synchronized SaveDatabase getInstance() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }

    private static SaveDatabase create() {
        return Room.databaseBuilder(LoveStickerApp.getApplication(),
                SaveDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public abstract SaveDao getUserDao();

    public abstract SaveStickerDao getStickerDao();

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE SaveData ADD COLUMN saveStickerPackGson TEXT NOT NULL DEFAULT 1");
        }
    };

}
