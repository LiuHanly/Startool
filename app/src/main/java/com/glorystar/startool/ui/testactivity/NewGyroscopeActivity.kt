package com.glorystar.startool.ui.testactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.activity.GyroscopeActivity

class NewGyroscopeActivity : BaseActivity(), SensorEventListener {

    var sensorManager: SensorManager? = null
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView
    private var orientationListener: CameraOrientationListener? = null

    lateinit var image1: ImageView
    lateinit var image2: ImageView
    lateinit var through_bu: Button


    /**
     * 当前屏幕旋转角度
     */
    private var mOrientation = 0


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewGyroscopeActivity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_new_gyroscope

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        proximityInfo = findViewById(R.id.tv_proximity_info)
        ambientInfo = findViewById(R.id.tv_ambient_info)
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        through_bu = findViewById(R.id.through_bu)

        orientationListener = CameraOrientationListener(this)
        orientationListener?.enable()
        startOrientationChangeListener()
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        through_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@NewGyroscopeActivity, 11, 2,1)
        }


    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val values: FloatArray? = p0?.values
        val type: Int? = p0?.sensor?.type
        when (type) {
            Sensor.TYPE_LIGHT -> ambientInfo.text = (values?.get(0).toString()) + "lx"
            Sensor.TYPE_PROXIMITY -> proximityInfo.text = values?.get(0).toString() + "cm"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
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
                val str =
                    "当前屏幕手持角度:$orientation°\n当前屏幕手持方向:$mCurrentNormalizedOrientation"

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