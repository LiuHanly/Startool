package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.glorystar.lightstrip.LightSdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.ColorPickerView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS485Util
import com.glorystar.vodlibrary.VodLib
import java.io.File
import java.util.ArrayList

class Vod101Activity : BaseActivity(), View.OnClickListener, SensorEventListener {
    lateinit var titleView: TitleKtView
    lateinit var relativeRelay: RelativeLayout
    lateinit var relativeWiegand: RelativeLayout
    lateinit var btRS485: Button
    lateinit var btInputPort: Button
    lateinit var btCamera: Button
    lateinit var btSensor: Button
    lateinit var btRfid: Button
    lateinit var btLed: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView
    var sensorManager: SensorManager? = null
    private var rs485: RS485Util? = null
    lateinit var inputOne: TextView
    lateinit var inputTwo: TextView
    lateinit var inputThree: TextView
    lateinit var ivInputOne: ImageView
    lateinit var ivInputTwo: ImageView
    lateinit var ivInputThree: ImageView
    private val mHandler = Handler()
    private var ledStatus = false
    private var loopStatus = false
    private var play = false
    private lateinit var btLight: Button
    private lateinit var btLoop: Button
    private lateinit var btPlay: Button
    private lateinit var mediaPlayer: MediaPlayer

    var inputOneRunable: Runnable = object : Runnable {
        override fun run() {
            val result: Int = VodLib.input(1)
            Log.e("inputKeyRunable", "inputOneRunable- run: $result")
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

    var inputTwoRunable: Runnable = object : Runnable {
        override fun run() {

            val result: Int = VodLib.input(2)
          //  Log.e("inputKeyRunable", "inputTwoRunable- run: $result")
            if (result == 1) {
                ivInputTwo.setImageResource(R.drawable.input_port_original)
                inputTwo.text = getString(R.string.ready)
                inputTwo.setTextColor(resources.getColor(R.color.colorFont))
            } else {
                ivInputTwo.setImageResource(R.drawable.press_green)
                inputTwo.text = getString(R.string.success)
                inputTwo.setTextColor(resources.getColor(R.color.colorAccent))
            }
            mHandler.postDelayed(this, 500)
        }
    }

    var inputThreeRunable: Runnable = object : Runnable {
        override fun run() {
            val result: Int = VodLib.input(3)
            if (result == 1) {
                ivInputThree.setImageResource(R.drawable.input_port_original)
                inputThree.text = getString(R.string.ready)
                inputThree.setTextColor(resources.getColor(R.color.colorFont))
            } else {
                ivInputThree.setImageResource(R.drawable.press_green)
                inputThree.text = getString(R.string.success)
                inputThree.setTextColor(resources.getColor(R.color.colorAccent))
            }
            mHandler.postDelayed(this, 500)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_vod101

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_vega)
        titleView.action = this

        rs485 = RS485Util(application)
        rs485!!.setCOM("/dev/ttyS8")
        mediaPlayer = MediaPlayer.create(this, R.raw.channelleft)
        mediaPlayer.isLooping = true

        relativeRelay = findViewById(R.id.relative_relay)
        relativeWiegand = findViewById(R.id.relative_wiegand)
        btRS485 = findViewById(R.id.bt_485)
        btInputPort = findViewById(R.id.bt_input)
        btCamera = findViewById(R.id.bt_camera)
        btSensor = findViewById(R.id.bt_sensor)
        btRfid = findViewById(R.id.bt_rfid)
        btLed = findViewById(R.id.bt_led)
        btLight = findViewById(R.id.bt_light)
        btLoop = findViewById(R.id.bt_loop)
        btPlay = findViewById(R.id.bt_play)

        relativeRelay.setOnClickListener(this)
        relativeWiegand.setOnClickListener(this)
        btRS485.setOnClickListener(this)
        btInputPort.setOnClickListener(this)
        btCamera.setOnClickListener(this)
        btSensor.setOnClickListener(this)
        btRfid.setOnClickListener(this)
        btLed.setOnClickListener(this)
        btLight.setOnClickListener(this)
        btLoop.setOnClickListener(this)
        btPlay.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_relay -> startActivity(Intent(this, Vod101RelayActivity::class.java))
            R.id.relative_wiegand -> startActivity(Intent(this, WiegandActivity::class.java))
            R.id.bt_485 -> showDialog(this)
            R.id.bt_input -> {
                inputPortDialog(this)
                if (Build.MODEL != "rk3288") {
                    mHandler.post(inputOneRunable)
                    mHandler.post(inputTwoRunable)
                    mHandler.post(inputThreeRunable)
                }
            }
            R.id.bt_camera -> startActivity(Intent(this, DualCameraActivity::class.java))
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
            R.id.bt_rfid -> startActivity(Intent(this, RfidActivity::class.java))
            R.id.bt_led -> {
                if (ledStatus) {
                    VodLib.cameraLed(0)
                    btLed.text = getString(R.string.turn_on)
                    ledStatus = false
                }else {
                    VodLib.cameraLed(1)
                    btLed.text = getString(R.string.turn_off)
                    ledStatus = true
                }
            }
            R.id.bt_light -> lightStripDialog(this)
            R.id.bt_loop -> {
                if (loopStatus){
                    VodLib.loop(0)
                    btLoop.text = getString(R.string.turn_on)
                    loopStatus = false
                }else {
                    VodLib.loop(1)
                    btLoop.text = getString(R.string.turn_off)
                    loopStatus = true
                }
            }
            R.id.bt_play -> {
                if (play){
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                    btPlay.text = getString(R.string.play)
                    play = false
                }else {
                    mediaPlayer.start()
                    btPlay.text = getString(R.string.stop)
                    play = true
                }
            }
        }
    }

    private fun showDialog(context: Context?) {
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
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        rs485?.start()
        rs485!!.getData { buffer, size -> runOnUiThread {
            val data = ByteArray(size)
            for (i in 0 until size) {
                data[i] = buffer[i]
            }
            tvInfo.setText(HexUtil.ByteArrToHex(data)) } }

        send.setOnClickListener {
            val info = edtText.text.toString()
            if (info.isNotEmpty()) {
                rs485?.sendData(info)
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
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun inputPortDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_input_port_3, null)
        inputOne = view.findViewById(R.id.tv_input_1)
        inputTwo = view.findViewById(R.id.tv_input_2)
        inputThree = view.findViewById(R.id.tv_input_3)
        ivInputOne = view.findViewById(R.id.iv_input_1)
        ivInputTwo = view.findViewById(R.id.iv_input_2)
        ivInputThree = view.findViewById(R.id.iv_input_3)
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
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
        mediaPlayer.release()
    }

    private fun checkSoFile(libraries: Array<String>): Boolean {
        val dir = File(applicationInfo.nativeLibraryDir)
        val files = dir.listFiles()
        if (files == null || files.size == 0) {
            return false
        }
        val libraryNameList: MutableList<String> = ArrayList()
        for (file in files) {
            libraryNameList.add(file.name)
        }
        var exists = true
        for (library in libraries) {
            exists = exists and libraryNameList.contains(library)
        }
        return exists
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
}