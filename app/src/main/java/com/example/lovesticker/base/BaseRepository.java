package com.example.lovesticker.base;

public class BaseRepository {

    private BaseRepository() {

    }

    private static final BaseRepository instance = new BaseRepository();
    public static BaseRepository getInstance() {
        return instance;
    }


}
