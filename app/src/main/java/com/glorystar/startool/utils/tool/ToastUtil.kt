package com.glorystar.startool.utils.tool

import android.annotation.SuppressLint
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.glorystar.startool.config.base.MyApplication



object ToastUtil {
    @SuppressLint("ShowToast")
    fun showMsg(text: String?) {
        try {
            val mToast  =  Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT)
            val layout  : LinearLayout =  mToast.view as LinearLayout
            val tv : TextView  = layout.getChildAt(0) as TextView
            tv.textSize = 5f
            mToast.setGravity(Gravity.BOTTOM, 0, 100)
            mToast.setText(text)
            mToast.show()
        } catch (e: Exception) {
            // 解决在子线程中调用Toast的异常情况处理
            Looper.prepare()
            val mToast  =  Toast.makeText(MyApplication.context, text, Toast.LENGTH_SHORT)
            val layout  : LinearLayout =  mToast.view as LinearLayout
            val tv : TextView  = layout.getChildAt(0) as TextView
            tv.textSize = 5f
            mToast.setGravity(Gravity.BOTTOM, 0, 100)
            mToast.setText(text)
            mToast.show()
            Looper.loop()
        }
    }

}