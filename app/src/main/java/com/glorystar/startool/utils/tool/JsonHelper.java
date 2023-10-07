package com.glorystar.startool.utils.tool;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Gson
 */
public class JsonHelper {

	public static Gson getGson() {
		return new Gson();
	}

	public synchronized static <T> T newfromJson(String json, Class<T> clazz) {
		try {
			if (!TextUtils.isEmpty(json)) {
				Gson gson = new Gson();
				T t = gson.fromJson(json, clazz);
				clazz = null;
				gson = null;
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T fromJson(String json, TypeToken<T> typeToken) {
		try {
			if (!TextUtils.isEmpty(json)) {
				if (json.endsWith(";")) {
					json = json.substring(0, json.length() - 1);
				}
				Gson gson = getGson();
				Type type = typeToken.getType();
				T t = gson.fromJson(json, type);
				typeToken = null;
				type = null;
				gson = null;
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String newtoJson(Object obj) {
		Gson gson = new Gson();
		String result = gson.toJson(obj);
		gson = null;
		return result;
	}

	public static <T> List<T> jsonToList(String json, Class<T[]> clazz)
	{
		Gson gson = new Gson();
		T[] array = gson.fromJson(json, clazz);
		return Arrays.asList(array);
	}
}
