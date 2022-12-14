package com.glorystar.startool.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * 陀螺仪测试
 * */
class GyroscopeActivity : BaseActivity() {
    private val TAG = GyroscopeActivity::class.java.simpleName
    private var orientationListener: CameraOrientationListener? = null
    lateinit var failure_bu: Button
    lateinit var through_bu: Button

    lateinit var image1: ImageView
    lateinit var image2: ImageView


    /**
     * 当前屏幕旋转角度
     */
    private var mOrientation = 0

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, GyroscopeActivity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_gyroscope

    override fun initView(savedInstanceState: Bundle?) {
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)

        orientationListener = CameraOrientationListener(this)
        orientationListener?.enable()
        startOrientationChangeListener()

        failure_bu?.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@GyroscopeActivity, 12, 1)


        }

        through_bu?.setOnClickListener {


            IntenAcitivity.getIntenAcitivity(this@GyroscopeActivity, 12, 2)

        }

    }

    /**
     * 启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
     */
    private fun startOrientationChangeListener() {
        val mOrEventListener: OrientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(rotation: Int) {
                var rotation = rotation
                Log.e("GyroscopeActivity", "rotation--->$rotation")
                image1.animate().rotation(-rotation.toFloat())
                image2.animate().rotation(-rotation.toFloat())
            }
        }
        mOrEventListener.enable()
    }


    private class CameraOrientationListener(context: Context?) :
        OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
        private var mCurrentNormalizedOrientation = 0
        override fun onOrientationChanged(orientation: Int) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation)
            }

            Runnable {
                val str = "当前屏幕手持角度:$orientation°\n当前屏幕手持方向:$mCurrentNormalizedOrientation"

            }
        }

        private fun normalize(degrees: Int): Int {
            if (degrees > 315 || degrees <= 45) {
                return 0
            }
            if (degrees in 46..135) {
                return 90
            }
            if (degrees in 136..225) {
                return 180
            }
            if (degrees in 226..315) {
                return 270
            }
            throw RuntimeException("The physics as we know them are no more. Watch out for anomalies.")
        }
    }

}