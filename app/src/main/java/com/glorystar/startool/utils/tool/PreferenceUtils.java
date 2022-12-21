package com.glorystar.startool.utils.tool;

import android.content.SharedPreferences;


import com.glorystar.startool.config.base.MyApplication;

import java.util.Set;

public class PreferenceUtils {

    private static SharedPreferences mSp;

    private static SharedPreferences getSp() {
        if (mSp == null) {
            mSp = MyApplication.Companion.getStarosInstance();
        }
        return mSp;
    }


    public static boolean getBoolean(String key) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, Boolean value) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(key, value);
    }


    public static void putLoog(String key, long value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key, long value) {
        SharedPreferences sp = getSp();
        return sp.getLong(key, value);
    }

    /**
     * 设置boolean值
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取boolean数据
     *
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获得String的数据,方法重载
     */
    public static String getString(String key) {
        SharedPreferences sp = getSp();
        return sp.getString(key, "");
    }

    /**
     * 获得String的数据,方法重载
     */
    public static String getString(String key, String value) {
        SharedPreferences sp = getSp();
        return sp.getString(key, value);
    }

    /**
     * 设置String值
     */
    public static void putString(String key, String value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * 获取int数据,重载
     */
    public static int getInt(String key, int defValue) {
        SharedPreferences sp = getSp();
        return sp.getInt(key, defValue);
    }

    /**
     * 设置int数据,重载
     */
    public static void putInt(String key, int value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    //添加set<String>数据
    public static void putSet(String key, Set<String> value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    //获取set<String>数据
    public static Set<String> getSet(String key) {
        SharedPreferences sp = getSp();
        return sp.getStringSet(key, null);
    }

    //删除数据
    public static void removeData(String key){
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
