package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.dialog.NebDialog
import com.glorystar.startool.ui.view.TitleKtView
import kotlin.io.path.Path

class SM6255Activity : BaseActivity(), View.OnClickListener, SensorEventListener {

    lateinit var titleView: TitleKtView
    lateinit var btSensor: Button

    lateinit var btButtons: Button
    private lateinit var btLight: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView

    var sensorManager: SensorManager? = null
    var sensorManager1: SensorManager? = null

    override fun getLayoutId(): Int = R.layout.activity_sm6255

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager1 = getSystemService(SENSOR_SERVICE) as SensorManager


        titleView = findViewById(R.id.tile_view)

        val title = intent.getStringExtra("title")

        titleView.title.text = title
        titleView.action = this

        titleView.title.text = getString(R.string.model_sm6255)
        titleView.action = this
        btSensor = findViewById(R.id.bt_sensor)
        btLight = findViewById(R.id.bt_light)
        btButtons = findViewById(R.id.bt_buttons)

        btSensor.setOnClickListener(this)
        btLight.setOnClickListener(this)
        btSensor.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_sensor -> {
                sensorDialog(this@SM6255Activity)
                sensorManager?.registerListener(
                    this,
                    sensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                sensorManager1?.registerListener(
                    this,
                    sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT),
                    SensorManager.SENSOR_DELAY_NORMAL
                )

            }

            R.id.bt_buttons -> {
                NebDialog.buttonsDialog(this@SM6255Activity)
            }

            R.id.bt_light -> NebDialog.lightStripDialog(this@SM6255Activity)
        }

    }

    fun sensorDialog(context: Context?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_sensor, null)
        proximityInfo = view.findViewById<TextView>(R.id.tv_proximity_info)
        ambientInfo = view.findViewById<TextView>(R.id.tv_ambient_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
 //   {Sensor name="mxc4005x Accelerometer Non-wakeup", vendor="memsic", version=10120150, type=1, maxRange=3000.0288, resolution=0.0976, power=1.0, minDelay=5000}, {Sensor name="mxc4005x Accelerometer Wakeup", vendor="memsic", version=10120150, type=1, maxRange=3000.0288, resolution=0.0976, power=1.0, minDelay=5000}, {Sensor name="rpr0521rs Ambient Light Sensor Non-wakeup", vendor="ROHM", version=262146, type=5, maxRange=43000.0, resolution=2.0, power=0.09, minDelay=0}, {Sensor name="rpr0521rs Ambient Light Sensor Wakeup", vendor="ROHM", version=262146, type=5, maxRange=43000.0, resolution=2.0, power=0.09, minDelay=0}, {Sensor name="rpr0521rs Proximity Sensor Non-wakeup", vendor="ROHM", version=262146, type=8, maxRange=20.0, resolution=1.0, power=0.09, minDelay=0}, {Sensor name="rpr0521rs Proximity Sensor Wakeup", vendor="ROHM", version=262146, type=8, maxRange=20.0, resolution=1.0, power=0.09, minDelay=0}, {Sensor name="sns_smd  Wakeup", vendor="qualcomm", version=1, type=17, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}, {Sensor name="step_detect  Non-wakeup", vendor="qualcomm", version=1, type=18, maxRange=1.0, resolution=1.0, power=0.15, minDelay=0}, {Sensor name="step_detect  Wakeup", vendor="qualcomm", version=1, type=18, maxRange=1.0, resolution=1.0, power=0.15, minDelay=0}, {Sensor name="pedometer  Non-wakeup", vendor="qualcomm", version=1, type=19, maxRange=4.2949673E9, resolution=1.0, power=0.15, minDelay=0}, {Sensor name="pedometer  Wakeup", vendor="qualcomm", version=1, type=19, maxRange=4.2949673E9, resolution=1.0, power=0.15, minDelay=0}, {Sensor name="sns_tilt  Wakeup", vendor="qualcomm", version=1, type=22, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}, {Sensor name="Device Orientation  Non-wakeup", vendor="qualcomm", version=1, type=27, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}, {Sensor name="Device Orientation  Wakeup", vendor="qualcomm", version=1, type=27, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}, {Sensor name="stationary_detect", vendor="qualcomm", version=1, type=29, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}, {Sensor name="stationary_detect_wakeup", vendor="qualcomm", version=1, type=29, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}, {Sensor name="motion_detect", vendor="qualcomm", version=1, type=30, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}, {Sensor name="motion_detect_wakeup", vendor="qualcomm", version=1, type=30, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}, {Sensor name="mxc4005x Accelerometer-Uncalibrated Non-wakeup", vendor="memsic", version=10120150, type=35, maxRange=3000.0288, resolution=0.0976, power=1.0, minDelay=5000}, {Sensor name="mxc4005x Accelerometer-Uncalibrated Wakeup", vendor="memsic", version=10120150, type=35, maxRange=3000.0288, resolution=0.0976, power=1.0, minDelay=5000}


    override fun onSensorChanged(event: SensorEvent?) {
        val values: FloatArray? = event?.values
        val type: Int? = event?.sensor?.getType()
        println("onSensorChanged:${type} -- ${values?.size} --${values?.get(0)}")
        when (type) {
            Sensor.TYPE_LIGHT -> ambientInfo.setText((values?.get(0).toString()) + "lx")
            Sensor.TYPE_PROXIMITY -> proximityInfo.setText(values?.get(0).toString() + "cm")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
        if (sensorManager1 != null) {
            sensorManager1!!.unregisterListener(this)
        }
    }

}