package com.glorystar.startool.config.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import cn.wandersnail.commons.poster.ThreadMode
import com.glorystar.startool.config.bluetooth.BTManager
import com.glorystar.startool.utils.tool.DimenUtil
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.unit.Subunits

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        AutoSizeConfig.getInstance().setLog(true)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(DimenUtil.screenWidth.toFloat(), DimenUtil.screenHeight.toFloat()).supportSubunits = Subunits.MM

        //构建自定义实例，需要在EasyBLE.getInstance()之前
        val manager: BTManager = BTManager.getBuilder()
            .setObserveAnnotationRequired(false) //不强制使用{@link Observe}注解才会收到被观察者的消息，强制使用的话，性能会好一些
            .setMethodDefaultThreadMode(ThreadMode.BACKGROUND) //指定回调方法和观察者方法的默认线程
            .build()
        manager.initialize(this)


        //屏幕适配
        AutoSizeConfig.getInstance().setLog(false)
            .setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any, activity: Activity) {}
                override fun onAdaptAfter(target: Any, activity: Activity) {}
            })
            .unitsManager.setSupportDP(false)
            .setDesignSize(1920f, 1080f).supportSubunits = Subunits.MM


    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            @SuppressLint("StaticFieldLeak") private set

        var Testname = ""
    }


}