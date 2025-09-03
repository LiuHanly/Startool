/**
 * @Title:AppInfoUtil.java
 * @Package com.glorystartech.staros.remote_monitoring.util
 * @author jinyuan
 * @date 2016-11-15
 */
package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.lang.reflect.Method;

/**
 * @className:AppInfoUtil
 * @author jinyuan
 * @desc TODO
 */
public class VersionInfoUtil {
	public static int getVersionCode(Context mContext) {
		int versionCode = 0;
		try {
			versionCode = mContext.getPackageManager().
					getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		PackageManager pm = null;
		if (context != null) {
			pm = context.getPackageManager();
			try {
				verName = pm.getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return verName;
	}

	public static String getSystemProperties(String key) {
		String value = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			value = (String) get.invoke(c, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
