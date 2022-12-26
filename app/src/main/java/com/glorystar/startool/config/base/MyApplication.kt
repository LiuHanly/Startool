package com.glorystar.startool.config.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.view.WindowManager
import cn.wandersnail.commons.poster.ThreadMode
import com.glorystar.startool.config.bluetooth.BTManager
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.utils.tool.*
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.unit.Subunits

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        AutoSizeConfig.getInstance().setLog(true)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(
                DimenUtil.screenWidth.toFloat(),
                DimenUtil.screenHeight.toFloat()
            ).supportSubunits = Subunits.MM

        //构建自定义实例，需要在EasyBLE.getInstance()之前
        val manager: BTManager = BTManager.getBuilder()
            .setObserveAnnotationRequired(false) //不强制使用{@link Observe}注解才会收到被观察者的消息，强制使用的话，性能会好一些
            .setMethodDefaultThreadMode(ThreadMode.BACKGROUND) //指定回调方法和观察者方法的默认线程
            .build()
        manager.initialize(this)

/*

        //屏幕适配
        AutoSizeConfig.getInstance().setLog(false)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(1920f, 1080f).supportSubunits = Subunits.MM

*/
        switchResolution(true)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            @SuppressLint("StaticFieldLeak") private set
        var mConfiguration: Configuration? = null
        private var width = 0
        private var height: Int = 0
        var rotate = true
        var rotate_180 = false

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

    fun switchResolution(bool: Boolean) {
        mConfiguration = this.resources.configuration
        if (mConfiguration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = 1080
            height = 1920
        } else if (mConfiguration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = 1920
            height = 1080
        }
        if (bool) {
            mConfiguration?.orientation?.let {
                PreferenceUtils.putInt(
                    Constantdtas.STAROS_ORIENTATION,
                    it
                )
            }
            val rotate = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            //Android屏幕旋转180，不会调用onConfigurationChanged方法
            if (rotate == 3) {
                rotate_180 = true
            }
        }
        AutoSizeConfig.getInstance().setLog(true)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(width.toFloat(), height.toFloat()).supportSubunits = Subunits.MM
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(mConfiguration!!)
        val item = IntentJumpUtils.getLanguage()
        LanguageUtil.changLang(this, item)
        if (MyApplication.rotate) {
            switchResolution(false)
            LiveDataBus.get().with("startScreenRotation").postValue("refresh")
        }
    }
}