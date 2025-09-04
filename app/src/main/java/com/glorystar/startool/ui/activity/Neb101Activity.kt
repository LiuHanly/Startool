package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.glorystar.lightstrip.LightSdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.ColorPickerView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS232Util

class Neb101Activity : BaseActivity(), View.OnClickListener, SensorEventListener {
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
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_neb101)
        titleView.action = this



        if (Build.MODEL.equals("rk3568")){
            rs232!!.setCOM("/dev/ttyS7")
        }else if(Build.MODEL.equals("rk3576_u")){
            println("======Neb101Activity====rk3576=====")
            rs232!!.setCOM("/dev/ttyS9")
        }

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

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_gpio ->{
                startActivity(Intent(this@Neb101Activity, GpioActivity::class.java))
            }
            R.id.relative_wiegand ->{
                startActivity(Intent(this@Neb101Activity, WiegandActivity::class.java))
            }
            R.id.bt_uart ->{
                showDialog(this@Neb101Activity)
            }
            R.id.bt_sensor ->{
                sensorDialog(this@Neb101Activity)
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
            R.id.bt_buttons ->{
                buttonsDialog(this@Neb101Activity)
            }
            R.id.bt_light -> lightStripDialog(this)
        }
    }

    private fun showDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_port, null)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            rs232?.stop()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)

        rs232?.start()
        rs232!!.getData { buffer, size ->
            runOnUiThread {
                val data = ByteArray(size)
                for (i in 0 until size) {
                    data[i] = buffer[i]
                }
                tvInfo.setText(HexUtil.ByteArrToHex(data))
            }
        }
    }

    private fun sensorDialog(context: Context?) {
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
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun buttonsDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_buttons, null)
        var output = view.findViewById<EditText>(R.id.edt_output)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
                when (keyCode) {
                    KeyEvent.KEYCODE_VOLUME_DOWN -> output.setText("press volume -")
                    KeyEvent.KEYCODE_VOLUME_UP -> output.setText("press volume +")
                    KeyEvent.KEYCODE_HOME -> {
                        output.setText("press home")
                        return true
                    }
                    else -> {
                    }
                }
                return false
            }
        })
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val values: FloatArray? = p0?.values
        val type: Int? = p0?.sensor?.getType()
        when (type) {
            Sensor.TYPE_LIGHT ->ambientInfo.setText((values?.get(0).toString()) + "lx")
            Sensor.TYPE_PROXIMITY -> proximityInfo.setText(values?.get(0).toString() + "cm")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    private fun lightStripDialog(context: Context?) {
        var lightStatus = false
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_light, null)
        var mColorPickerView = view.findViewById<ColorPickerView>(R.id.color_pick_view)
        var ivLight = view.findViewById<ImageView>(R.id.iv_light)
        var tvRed = view.findViewById<TextView>(R.id.tv_red)
        var tvYellow = view.findViewById<TextView>(R.id.tv_yellow)
        var tvGreen = view.findViewById<TextView>(R.id.tv_green)
        var tvQing = view.findViewById<TextView>(R.id.tv_qing)
        var tvWhite = view.findViewById<TextView>(R.id.tv_white)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        mColorPickerView.setOnColorChangedListener {
            LightSdk.getInstance().setLightColorHsv(it)
        }

        ivLight.setOnClickListener {
            if (lightStatus) {
                LightSdk.getInstance().closeLight()
                ivLight.setImageResource(R.drawable.ic_light_off)
                lightStatus = false
            }else {
                LightSdk.getInstance().openLight()
                ivLight.setImageResource(R.drawable.ic_light_on)
                lightStatus = true
            }
        }

        tvRed.setOnClickListener {
            LightSdk.getInstance().setLightColor(getColor(R.color.colorRed))
        }

        tvYellow.setOnClickListener {
            LightSdk.getInstance().setLightColor(getColor(R.color.colorYellow))
        }

        tvGreen.setOnClickListener {
            LightSdk.getInstance().setLightColor(getColor(R.color.colorAccent))
        }

        tvQing.setOnClickListener {
            LightSdk.getInstance().setLightColor(getColor(R.color.teal_200))
        }

        tvWhite.setOnClickListener {
            LightSdk.getInstance().setLightColor(getColor(R.color.white))
        }
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
    }
}