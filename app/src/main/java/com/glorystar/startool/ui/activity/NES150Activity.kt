package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.dialog.NebDialog
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.RS232Util

class NES150Activity : BaseActivity(), View.OnClickListener, SensorEventListener {

    /*    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_nes150)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }*/

    lateinit var titleView: TitleKtView
    lateinit var relativeGpio: RelativeLayout
    lateinit var relativeWiegand: RelativeLayout
    lateinit var btUart: Button
    lateinit var btSensor: Button
    lateinit var btButtons: Button
    private lateinit var btLight: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView

    var rs232: RS232Util? = null
    var sensorManager: SensorManager? = null

    override fun getLayoutId(): Int = R.layout.activity_neb101

    override fun initView(savedInstanceState: Bundle?) {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rs232 = RS232Util(application)

        if (Build.MODEL.equals("rk3576_u")) {
            rs232!!.setCOM("/dev/ttyS9")
        }
        titleView = findViewById(R.id.tile_view)

        val title = intent.getStringExtra("title")

        titleView.title.text = title
        titleView.action = this


        titleView.title.text = getString(R.string.model_nes150)
        titleView.action = this


        relativeGpio = findViewById(R.id.relative_gpio)
        relativeWiegand = findViewById(R.id.relative_wiegand)
        btUart = findViewById(R.id.bt_uart)
        btSensor = findViewById(R.id.bt_sensor)
        btButtons = findViewById(R.id.bt_buttons)
        btLight = findViewById(R.id.bt_light)


        relativeGpio.setOnClickListener(this)
        relativeWiegand.setOnClickListener(this)
        btLight.setOnClickListener(this)
        btUart.setOnClickListener(this)
        btSensor.setOnClickListener(this)
        btButtons.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.relative_gpio -> {
                startActivity(Intent(this@NES150Activity, GpioActivity::class.java))
            }

            R.id.relative_wiegand -> {
                startActivity(Intent(this@NES150Activity, WiegandActivity::class.java))
            }

            R.id.bt_uart -> {
                NebDialog.showDialog(this@NES150Activity, rs232!!)
            }

            R.id.bt_sensor -> {
                sensorDialog(this@NES150Activity)
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
                NebDialog.buttonsDialog(this@NES150Activity)
            }

            R.id.bt_light -> NebDialog.lightStripDialog(this@NES150Activity)
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