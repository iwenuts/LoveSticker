package com.example.lovesticker.util.mmkv;

import android.content.Context;
import android.content.SharedPreferences;

public class PixMKVUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void initMKV(Context appContext) {
        sharedPreferences = appContext.getSharedPreferences("Pixocut-config",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public static void put(String key,String value) {
        editor.putString(key,value);
        editor.apply();
    }

    public static void put(String key,boolean value) {
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static void put(String key,long value) {
        editor.putLong(key,value);
        editor.apply();
    }

    public static void put(String key,float value) {
        editor.putFloat(key,value);
        editor.apply();
    }

    public static void put(String key,int value) {
        editor.putInt(key,value);
        editor.apply();
    }

    public static String getString(String key,String defaultValue) {
        return sharedPreferences.getString(key,defaultValue);
    }

    public static boolean getBoolean(String key,boolean defaultValue) {
        return sharedPreferences.getBoolean(key,defaultValue);
    }

    public static long getLong(String key,long defaultValue) {
        return sharedPreferences.getLong(key,defaultValue);
    }

    public static float getFloat(String key,float defaultValue) {
        return sharedPreferences.getFloat(key,defaultValue);
    }

    public static int getInt(String key,int defaultValue) {
        return sharedPreferences.getInt(key,defaultValue);
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key,"");
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }

    public static long getLong(String key) {
        return sharedPreferences.getLong(key,0);
    }

    public static float getFloat(String key) {
        return sharedPreferences.getFloat(key,0f);
    }

    public static int getInt(String key) {
        return sharedPreferences.getInt(key,0);
    }

}
