package com.le.info.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.le.info.base.MyApplication;

public class SPUtils {

    private SPUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
    }

    public static void setPrefString(final String key, final String value){
        getSharedPreferences().edit().putString(key,value).apply();
    }

    public static String getPrefString(final String key,final String defaultValue){
        return getSharedPreferences().getString(key,defaultValue);
    }

    public static void setPrefBoolean(final String key,final boolean value){
        getSharedPreferences().edit().putBoolean(key,value).apply();
    }

    public static boolean getPrefBoolean(final String key,final boolean defaultValue){
        return getSharedPreferences().getBoolean(key,defaultValue);
    }

    public static void setPrefInt(final String key,final int value){
        getSharedPreferences().edit().putInt(key,value).apply();
    }

    public static int getPrefInt(final String key,final int defaultValue){
        return getSharedPreferences().getInt(key,defaultValue);
    }

    public static void setPrefFloat(final String key,final float value) {
        getSharedPreferences().edit().putFloat(key,value).apply();
    }

    public static float getPrefFloat(final String key,final float defaultValue){
        return getSharedPreferences().getFloat(key,defaultValue);
    }

    public static void setPrefFloat(final String key,final long value) {
        getSharedPreferences().edit().putLong(key,value).apply();
    }

    public static long getPrefLong(final String key,final long defaultValue){
        return getSharedPreferences().getLong(key,defaultValue);
    }

    public static boolean hasKey(final String key){
        return getSharedPreferences().contains(key);
    }

    public static void clearPreference(){
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

}
