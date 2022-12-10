package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.glorystar.startool.config.base.MyApplication;
import com.glorystar.startool.config.constant.Constant;


public class SharePreferences {
    private static volatile SharePreferences sharePreferences;


    public SharedPreferences prefs;
    // server_url
    private final String PREFERENCE_SERVER_URL = "preference_server_url";

    public static SharePreferences getInstance() {
        if (sharePreferences == null) {
            synchronized (SharePreferences.class) {
                if (sharePreferences == null) {
                    sharePreferences = new SharePreferences(MyApplication.Companion.getContext());
                }
            }
        }
        return sharePreferences;
    }

    private SharePreferences(Context context) {
        prefs = context.getSharedPreferences(Constant.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    // 设置 ServerUrl
    public void setServerUrl(String serverUrl) {
        putString(PREFERENCE_SERVER_URL, serverUrl);
    }

    public String getServerUrl() {
        return getString(PREFERENCE_SERVER_URL);
    }

    /**
     * 存放普通数据的方法
     *
     * @param key  存储数据的键
     * @param data 存储数据的值
     */
    protected void putString(String key, String data) {
        prefs.edit().putString(key, data).apply();
    }

    /**
     * 读取普通数据的方法
     *
     * @param key 要读取数据的key
     * @return 要读取的数据
     */
    protected String getString(String key) {
        return prefs.getString(key, "");
    }


    /**
     * 读取普通数据的方法
     *
     * @param key 要读取数据的key
     * @return 要读取的数据
     */
    public  String getString(String key, String value) {
        return prefs.getString(key, value);
    }


    /**
     * 存放普通数据的方法
     *
     * @param key  存储数据的键
     * @param data 存储数据的值
     */
    protected void putInt(String key, int data) {
        prefs.edit().putInt(key, data).apply();
    }

    /**
     * 读取普通数据的方法
     *
     * @param key 要读取数据的key
     * @return 要读取的数据
     */
    public int getInt(String key) {
        return prefs.getInt(key, 0);
    }

    public int getInt(String key ,int value){
        return prefs.getInt(key, value);
    }

    /**
     * 移除相关key对应的item
     *
     * @param key 需要移除的key
     */
    protected void remove(String key) {
        prefs.edit().remove(key).apply();
    }


    protected void putLong(String key, long data) {
        prefs.edit().putLong(key, data).apply();
    }

    protected Long getLong(String key) {
        return prefs.getLong(key, 0);
    }

    protected void putBoolean(String key, Boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    protected Boolean getBoolean(String value) {
        return prefs.getBoolean(value, false);
    }

    protected Boolean getBoolean(String key ,Boolean value){
        return prefs.getBoolean(key, value);
    }
    /**
     * 清除相关prefs数据
     */
    public void clear() {
        prefs.edit().clear().apply();
    }


}
