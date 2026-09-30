package com.glorystar.startool.utils.tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets

/**
 * Created by hj on 2018/7/25.
 */
object StatusNaviBarUitl {
    // 另一个原厂应用的包名（即包含 StarosBroadcastReceiver 的应用包名）
    // 从你上一次发送的日志看，原厂的核心进程名是 "com.glorystartech.staros"
    private const val TARGET_PACKAGE = "com.glorystartech.staros"
    private const val TARGET_RECEIVER = "com.glorystartech.staros.receiver.StarosBroadcastReceiver"

    /**
     * 隐藏全局和应用内的状态导航栏
     */
    fun hide(activity: Activity) {
        try {
            // 1. 先把当前 Activity 内部切到沉浸式全屏
            val window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }

            // 2. 跨进程借壳：显式发送广播给原厂具有 system 特权的应用，让它帮你执行 HIDE
            val intent = Intent("HIDE_STATUS").apply {
                // 关键：显式指定目标应用的包名和接收器的完整类名
                setClassName(TARGET_PACKAGE, TARGET_RECEIVER)
            }
            activity.sendBroadcast(intent)

            Log.d("StatusNaviBarUitl", "Forced cross-app HIDE_STATUS broadcast sent.")
        } catch (e: Exception) {
            Log.e("StatusNaviBarUitl", "hide error: ${e.message}")
        }
    }

    /**
     * 恢复全局和应用内的状态导航栏（复活返回键）
     */
    fun show(activity: Activity) {
        try {
            // 1. 还原应用内的 Window 属性
            val window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }

            // 2. 跨进程借壳：显式发送广播给原厂应用，借它的手强行在底层复活返回键！
            // 它是安全的自定义 Action，绝对不会触发系统的 SecurityException 闪退！
            val intent = Intent("SHOW_STATUS").apply {
                setClassName(TARGET_PACKAGE, TARGET_RECEIVER)
            }
            activity.sendBroadcast(intent)

            Log.d("StatusNaviBarUitl", "Forced cross-app SHOW_STATUS broadcast sent.")
        } catch (e: Exception) {
            Log.e("StatusNaviBarUitl", "show error: ${e.message}")
        }
    }
}