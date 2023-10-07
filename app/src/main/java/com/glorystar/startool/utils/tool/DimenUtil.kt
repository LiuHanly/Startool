package com.glorystar.startool.utils.tool

import com.glorystar.startool.config.base.MyApplication


object DimenUtil {
    val screenWidth: Int
        get() {
            val resources = MyApplication.context?.resources
            val dm = resources?.displayMetrics
            return dm!!.widthPixels
        }
    val screenHeight: Int
        get() {
            val resources = MyApplication.context?.resources
            val dm = resources?.displayMetrics
            return dm!!.heightPixels
        }

}