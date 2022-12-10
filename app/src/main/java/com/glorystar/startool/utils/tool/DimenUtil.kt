package com.glorystar.startool.utils.tool

import com.glorystar.startool.config.base.MyApplication


/**
 * 获取屏幕的宽高
 */
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