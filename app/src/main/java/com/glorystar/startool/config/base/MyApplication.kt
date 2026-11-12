package com.glorystar.startool.config.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import cn.wandersnail.commons.poster.ThreadMode
import com.glorystar.startool.config.bluetooth.BTManager
import com.glorystar.startool.config.constant.Constantdtas
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.unit.Subunits
import java.io.BufferedReader
import java.io.FileReader

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Android 9 (API 28) 及以上系统，必须为多进程 WebView 设置独立的数据目录后缀
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getCurrentProcessNameLegacy()
            if (!TextUtils.isEmpty(processName) && packageName != processName) {
                // 将 :webview 进程的目录后缀设为独立的名字（如 webview_sub）
                // 这样系统就会创建一个独立安全的沙箱目录，彻底避开 System UID 的主进程禁用限制
                WebView.setDataDirectorySuffix("webview_sub")
            }
        }
        context = applicationContext
        mConfiguration =  this.resources.configuration;
        AutoSizeConfig.getInstance().setLog(true)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(
                800f,
                1280f
            ).supportSubunits = Subunits.MM

        //构建自定义实例，需要在EasyBLE.getInstance()之前
        val manager: BTManager = BTManager.getBuilder()
            .setObserveAnnotationRequired(false) //不强制使用{@link Observe}注解才会收到被观察者的消息，强制使用的话，性能会好一些
            .setMethodDefaultThreadMode(ThreadMode.BACKGROUND) //指定回调方法和观察者方法的默认线程
            .build()
        manager.initialize(this)
    }
    /**
     * 兼容性更好的获取当前进程名的方法
     */
    private fun getCurrentProcessNameLegacy(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return getProcessName()
        }
        try {
            val reader = BufferedReader(FileReader("/proc/self/cmdline"))
            val processName = reader.readLine().trim()
            reader.close()
            return processName
        } catch (e: Exception) {
            return null
        }
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        var mConfiguration: Configuration? = null
        var Testname = ""
        private var sp_setting: SharedPreferences? = null

        fun getStarosInstance(): SharedPreferences? {
            if (null == sp_setting) {
                sp_setting = context
                    ?.getSharedPreferences(Constantdtas.STAROS_SETTING, MODE_PRIVATE)
            }
            return sp_setting
        }
    }
}