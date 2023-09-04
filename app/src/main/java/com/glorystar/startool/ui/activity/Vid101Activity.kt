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
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.glorystar.lightstrip.LightSdk
import com.glorystar.relaylibrary.Sdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.ColorPickerView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS485Util
import com.yiran.keylibrary.NativeLib

class Vid101Activity : BaseActivity(), View.OnClickListener, SensorEventListener {
    lateinit var titleView: TitleKtView
    lateinit var btRelay1Open: Button
    lateinit var btRelay1Close: Button
    lateinit var btRelay2Open: Button
    lateinit var btRelay2Close: Button
    private var relaySdk: Sdk? = null

    lateinit var btRS485: Button
    lateinit var btInputPort: Button
    lateinit var btAlarmInputPort: Button
    lateinit var btCamera: Button
    lateinit var btSensor: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView
    var sensorManager: SensorManager? = null
    private var rs485: RS485Util? = null
    lateinit var inputOne: TextView
    lateinit var ivInputOne: ImageView
    lateinit var inputAlarmOne: TextView
    lateinit var ivInputAlarmOne: ImageView
    lateinit var inputAlarmTwo: TextView
    lateinit var ivInputAlarmTwo: ImageView
    lateinit var inputAlarmThree: TextView
    lateinit var ivInputAlarmThree: ImageView
    lateinit var inputAlarmFour: TextView
    lateinit var ivInputAlarmFour: ImageView
    lateinit var inputAlarmFive: TextView
    lateinit var ivInputAlarmFive: ImageView
    lateinit var inputAlarmSix: TextView
    lateinit var ivInputAlarmSix: ImageView
    lateinit var inputAlarmSeven: TextView
    lateinit var ivInputAlarmSeven: ImageView
    lateinit var inputAlarmEight: TextView
    lateinit var ivInputAlarmEight: ImageView
    private val mHandler = Handler()
    private lateinit var nativeLib: NativeLib
    private lateinit var btLight: Button

    var inputOneRunable: Runnable = object : Runnable {
        override fun run() {
            val result: Int = com.example.vod.MainActivity.input(1)
            if (result == 1) {
                ivInputOne.setImageResource(R.drawable.input_port_original)
                inputOne.text = getString(R.string.ready)
                inputOne.setTextColor(resources.getColor(R.color.colorFont))
            } else {
                ivInputOne.setImageResource(R.drawable.press_green)
                inputOne.text = getString(R.string.success)
                inputOne.setTextColor(resources.getColor(R.color.colorAccent))
            }
            mHandler.postDelayed(this, 500)
        }
    }

    var inputKeyRunable: Runnable = object : Runnable {
        override fun run() {
            var result: String = nativeLib.getData().toString(2).trim()
            if (result.length < 8) {
                for (i in 0 until (8 - result.length)) {
                    result = "0" + result
                }
            }

            if (result.length == 8) {
                val data = result.toCharArray()
                if (data[7] == '1') {
                    ivInputAlarmOne.setImageResource(R.drawable.press_green)
                    inputAlarmOne.text = getString(R.string.success)
                    inputAlarmOne.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmOne.setImageResource(R.drawable.input_port_original)
                    inputAlarmOne.text = getString(R.string.ready)
                    inputAlarmOne.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[6] == '1') {
                    ivInputAlarmTwo.setImageResource(R.drawable.press_green)
                    inputAlarmTwo.text = getString(R.string.success)
                    inputAlarmTwo.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmTwo.setImageResource(R.drawable.input_port_original)
                    inputAlarmTwo.text = getString(R.string.ready)
                    inputAlarmTwo.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[5] == '1') {
                    ivInputAlarmThree.setImageResource(R.drawable.press_green)
                    inputAlarmThree.text = getString(R.string.success)
                    inputAlarmThree.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmThree.setImageResource(R.drawable.input_port_original)
                    inputAlarmThree.text = getString(R.string.ready)
                    inputAlarmThree.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[4] == '1') {
                    ivInputAlarmFour.setImageResource(R.drawable.press_green)
                    inputAlarmFour.text = getString(R.string.success)
                    inputAlarmFour.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmFour.setImageResource(R.drawable.input_port_original)
                    inputAlarmFour.text = getString(R.string.ready)
                    inputAlarmFour.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[3] == '1') {
                    ivInputAlarmFive.setImageResource(R.drawable.press_green)
                    inputAlarmFive.text = getString(R.string.success)
                    inputAlarmFive.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmFive.setImageResource(R.drawable.input_port_original)
                    inputAlarmFive.text = getString(R.string.ready)
                    inputAlarmFive.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[2] == '1') {
                    ivInputAlarmSix.setImageResource(R.drawable.press_green)
                    inputAlarmSix.text = getString(R.string.success)
                    inputAlarmSix.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmSix.setImageResource(R.drawable.input_port_original)
                    inputAlarmSix.text = getString(R.string.ready)
                    inputAlarmSix.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[1] == '1') {
                    ivInputAlarmSeven.setImageResource(R.drawable.press_green)
                    inputAlarmSeven.text = getString(R.string.success)
                    inputAlarmSeven.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmSeven.setImageResource(R.drawable.input_port_original)
                    inputAlarmSeven.text = getString(R.string.ready)
                    inputAlarmSeven.setTextColor(resources.getColor(R.color.colorFont))
                }
                if (data[0] == '1') {
                    ivInputAlarmEight.setImageResource(R.drawable.press_green)
                    inputAlarmEight.text = getString(R.string.success)
                    inputAlarmEight.setTextColor(resources.getColor(R.color.colorAccent))
                } else {
                    ivInputAlarmEight.setImageResource(R.drawable.input_port_original)
                    inputAlarmEight.text = getString(R.string.ready)
                    inputAlarmEight.setTextColor(resources.getColor(R.color.colorFont))
                }
            }
            mHandler.postDelayed(this, 100)
        }
    }

    override fun getLayoutId() = R.layout.activity_vid101

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        titleView = findViewById(R.id.tile_view)

        val title = intent.getStringExtra("title")
        titleView.title.text = title
        titleView.action = this

        btRelay1Open = findViewById(R.id.bt_relay1_open)
        btRelay1Close = findViewById(R.id.bt_relay1_close)
        btRelay2Open = findViewById(R.id.bt_relay2_open)
        btRelay2Close = findViewById(R.id.bt_relay2_close)

        btRS485 = findViewById(R.id.bt_485)
        btInputPort = findViewById(R.id.bt_input)
        btAlarmInputPort = findViewById(R.id.bt_alarm_input)
        btCamera = findViewById(R.id.bt_camera)
        btSensor = findViewById(R.id.bt_sensor)
        btLight = findViewById(R.id.bt_light)

        btRelay1Open.setOnClickListener(this)
        btRelay1Close.setOnClickListener(this)
        btRelay2Open.setOnClickListener(this)
        btRelay2Close.setOnClickListener(this)

        btRS485.setOnClickListener(this)
        btInputPort.setOnClickListener(this)
        btAlarmInputPort.setOnClickListener(this)
        btCamera.setOnClickListener(this)
        btSensor.setOnClickListener(this)
        btLight.setOnClickListener(this)

        relaySdk = Sdk()
        rs485 = RS485Util(application)
        rs485?.setCOM("/dev/ttyAS2")

        nativeLib = NativeLib()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_relay1_open -> relaySdk!!.openRelay(1)
            R.id.bt_relay1_close -> relaySdk!!.closeRelay(1)
            R.id.bt_relay2_open -> relaySdk!!.openRelay(2)
            R.id.bt_relay2_close -> relaySdk!!.closeRelay(2)
            R.id.bt_input -> {
                inputPortDialog(this)
                mHandler.post(inputOneRunable)
            }
            R.id.bt_alarm_input -> {
                inputEightPortDialog(this)
                mHandler.post(inputKeyRunable)
            }
            R.id.bt_camera -> startActivity(Intent(this, CamerasActivity::class.java))
            R.id.bt_sensor -> {
                sensorDialog(this)
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
            R.id.bt_485 -> rs485Dialog(this)
            R.id.bt_light -> lightStripDialog(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lightStripDialog(context: Context?) {
        var lightStatus: Boolean = false
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
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun inputPortDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_door_bell_port, null)
        inputOne = view.findViewById(R.id.tv_input_1)
        ivInputOne = view.findViewById(R.id.iv_input_1)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            dialog.dismiss()
            mHandler.removeCallbacksAndMessages(null)
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun rs485Dialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_a133_rs485, null)
        var title = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)
        var edtText = view.findViewById<EditText>(R.id.edit_text)
        var send = view.findViewById<Button>(R.id.bt_send)

        title.setText(getString(R.string.rs485_port))

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            rs485?.stop()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        rs485?.start()
        rs485!!.getData { buffer, size ->
            runOnUiThread {
                val data = ByteArray(size)
                for (i in 0 until size) {
                    data[i] = buffer[i]
                }
                tvInfo.setText(HexUtil.ByteArrToHex(data))
            }
        }

        send.setOnClickListener {
            val info = edtText.text.toString()
            if (info.isNotEmpty()) {
                rs485?.sendData(info)
            }
        }
    }

    private fun inputEightPortDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_input_eight_port, null)
        inputAlarmOne = view.findViewById(R.id.tv_input_1)
        ivInputAlarmOne = view.findViewById(R.id.iv_input_1)
        inputAlarmTwo = view.findViewById(R.id.tv_input_2)
        ivInputAlarmTwo = view.findViewById(R.id.iv_input_2)
        inputAlarmThree = view.findViewById(R.id.tv_input_3)
        ivInputAlarmThree = view.findViewById(R.id.iv_input_3)
        inputAlarmFour = view.findViewById(R.id.tv_input_4)
        ivInputAlarmFour = view.findViewById(R.id.iv_input_4)
        inputAlarmFive = view.findViewById(R.id.tv_input_5)
        ivInputAlarmFive = view.findViewById(R.id.iv_input_5)
        inputAlarmSix = view.findViewById(R.id.tv_input_6)
        ivInputAlarmSix = view.findViewById(R.id.iv_input_6)
        inputAlarmSeven = view.findViewById(R.id.tv_input_7)
        ivInputAlarmSeven = view.findViewById(R.id.iv_input_7)
        inputAlarmEight = view.findViewById(R.id.tv_input_8)
        ivInputAlarmEight = view.findViewById(R.id.iv_input_8)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            dialog.dismiss()
            mHandler.removeCallbacksAndMessages(null)
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val values: FloatArray? = p0?.values
        val type: Int? = p0?.sensor?.getType()
        when (type) {
            Sensor.TYPE_LIGHT -> ambientInfo.setText((values?.get(0).toString()) + "lx")
            Sensor.TYPE_PROXIMITY -> proximityInfo.setText(values?.get(0).toString() + "cm")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
    }
}