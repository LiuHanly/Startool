package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * @className:HideUpDownStatus
 * @author jinyuan
 * @date 2016-7-26 上午11:25:46
 * @desc 隐藏和显示状态栏类
 */
public class StatusNavigationBarUtil {

	/**
	 * @title:Hide
	 * @desc:通过布尔值来实现状态栏的隐藏与显示
	 * @return void
	 * @param enable
	 */
	public void hide(boolean enable, Context context) {
		if (enable) {
			hideStatus(context);
		} else {
			showStatus(context);
		}
	}

	/**
	 * @title:HideStatus
	 * @desc:隐藏状态栏
	 * @return void
	 */
	private void hideStatus(Context context) {
		String sysVersion = VERSION.RELEASE;
		if (sysVersion.equals("4.4.2")) {
			HideStatus();
		} else {
			context.sendBroadcast(new Intent("com.glorystartech.HideStatus"));
		}
	}

	/**
	 * @title:ShowStatus
	 * @desc:显示状态栏
	 * @return void
	 */
	private void showStatus(Context context) {
		String sysVersion = VERSION.RELEASE;
		if (sysVersion.equals("4.4.2")) {
			ShowStatus();
		} else {
			context.sendBroadcast(new Intent("com.glorystartech.OpenStatus"));
		}
	}

	/**
	 * @title:ShowStatus
	 * @desc:显示状态栏
	 * @return void
	 */
	private void ShowStatus() {
		try {
			Process proc = Runtime.getRuntime().exec(
					new String[] { "su", "-c", "am", "startservice", "-n",
							"com.android.systemui/.SystemUIService" });
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @title:HideStatus
	 * @desc:隐藏状态栏
	 * @return void
	 */
	private void HideStatus() {
		try {
			String ProcID = "79";
			if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
				ProcID = "42"; // ICS AND NEWER
			}
			// 需要root 权限
			Process proc = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							"service call activity " + ProcID
									+ " s16 com.android.systemui" }); // WAS
			proc.waitFor();
		} catch (Exception ex) {
		}
	}
}
