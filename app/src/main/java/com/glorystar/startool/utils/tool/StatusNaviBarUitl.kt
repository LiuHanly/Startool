package com.glorystar.startool.utils.tool

import android.content.Context
import android.content.Intent

/**
 * Created by hj on 2018/7/25.
 */
object StatusNaviBarUitl {
    // hide status navigation bar
   fun hide(context: Context) {
        context.sendBroadcast(Intent("com.glorystartech.HideStatus"))
    }
    // show status navigation bar
    fun show(context: Context) {
        context.sendBroadcast(Intent("com.glorystartech.OpenStatus"))
    }
}