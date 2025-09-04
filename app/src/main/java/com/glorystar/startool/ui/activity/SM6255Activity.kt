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

class SM6255Activity : BaseActivity(), View.OnClickListener, SensorEventListener {

    lateinit var titleView: TitleKtView
    lateinit var btSensor: Button

    lateinit var btButtons: Button
    private lateinit var btLight: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView

    var sensorManager: SensorManager? = null

    override fun getLayoutId(): Int = R.layout.activity_sm6255

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

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
                    sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT),
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                sensorManager?.registerListener(
                    this,
                    sensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY),
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
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_sensor, null)
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
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values: FloatArray? = event?.values
        val type: Int? = event?.sensor?.getType()
        when (type) {
            Sensor.TYPE_LIGHT -> ambientInfo.setText((values?.get(0).toString()) + "lx")
            Sensor.TYPE_PROXIMITY -> proximityInfo.setText(values?.get(0).toString() + "cm")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}