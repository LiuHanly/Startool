package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log; // 💡 引入日志库

/**
 * @className:HideUpDownStatus
 * @author jinyuan
 * @date 2016-7-26 上午11:25:46
 */
public class StatusNavigationBarUtil {

	private static final String TAG = "StatusNaviBarUtil";

	/**
	 * @title:Hide
	 * @return void
	 * @param enable
	 */
	public void hide(boolean enable, Context context) {
		try {
			if (enable) {
				hideStatus(context);
			} else {
				showStatus(context);
			}
		} catch (Exception e) {
			// 💡 修复：不再抛出异常导致闪退，仅打印堆栈日志
			e.printStackTrace();
		}
	}

	/**
	 * @title:HideStatus
	 * @return void
	 */
	private void hideStatus(Context context) {
		try {
			String sysVersion = VERSION.RELEASE;
			if ("4.4.2".equals(sysVersion)) {
				HideStatus();
			} else {
				if (context != null) {
					context.sendBroadcast(new Intent("com.glorystartech.HideStatus"));
				}
			}
		} catch (SecurityException e) {
			// 💡 修复：专门捕获由于缺失系统签名引起的权限拒绝异常
			Log.e(TAG, "Permission Denial: check if your app is signed with GloryStar Platform certificate.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @title:ShowStatus
	 * @return void
	 */
	private void showStatus(Context context) {
		try {
			String sysVersion = VERSION.RELEASE;
			if ("4.4.2".equals(sysVersion)) {
				ShowStatus();
			} else {
				if (context != null) {
					context.sendBroadcast(new Intent("com.glorystartech.OpenStatus"));
				}
			}
		} catch (SecurityException e) {
			Log.e(TAG, "Permission Denial: open status bar failed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @title:ShowStatus
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
	 * @return void
	 */
	private void HideStatus() {
		try {
			String ProcID = "79";
			if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
				ProcID = "42"; // ICS AND NEWER
			}
			Process proc = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							"service call activity " + ProcID
									+ " s16 com.android.systemui" }); // WAS
			proc.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
