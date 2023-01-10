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
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS485Util
import java.io.File
import java.util.ArrayList

class VegaActivity : BaseActivity(),View.OnClickListener, SensorEventListener {
    lateinit var titleView: TitleKtView
    lateinit var relativeRelay: RelativeLayout
    lateinit var relativeWiegand: RelativeLayout
    lateinit var btRS485: Button
    lateinit var btInputPort: Button
    lateinit var btCamera: Button
    lateinit var btSensor: Button
    lateinit var btRfid: Button
    lateinit var proximityInfo: TextView
    lateinit var ambientInfo: TextView
    var sensorManager: SensorManager? = null
    private var rs485: RS485Util? = null
    lateinit var inputOne: TextView
    lateinit var inputTwo: TextView
    lateinit var ivInputOne: ImageView
    lateinit var ivInputTwo: ImageView
    private val libraries = arrayOf("libiotest.so")
    private val mHandler = Handler()

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

    var inputTwoRunable: Runnable = object : Runnable {
        override fun run() {
            val result: Int = com.example.vod.MainActivity.input(2)
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

    override fun getLayoutId(): Int = R.layout.activity_vega

    override fun initView(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_vega)
        titleView.action = this

        rs485 = RS485Util(application)
        if (Build.MODEL == "rk3399") {
            rs485!!.setCOM("/dev/ttyUSB0")
        }

        relativeRelay = findViewById(R.id.relative_relay)
        relativeWiegand = findViewById(R.id.relative_wiegand)
        btRS485 = findViewById(R.id.bt_485)
        btInputPort = findViewById(R.id.bt_input)
        btCamera = findViewById(R.id.bt_camera)
        btSensor = findViewById(R.id.bt_sensor)
        btRfid = findViewById(R.id.bt_rfid)

        relativeRelay.setOnClickListener(this)
        relativeWiegand.setOnClickListener(this)
        btRS485.setOnClickListener(this)
        btInputPort.setOnClickListener(this)
        btCamera.setOnClickListener(this)
        btSensor.setOnClickListener(this)
        btRfid.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_relay -> startActivity(Intent(application, RelayActivity::class.java))
            R.id.relative_wiegand -> startActivity(Intent(application, WiegandActivity::class.java))
            R.id.bt_485 -> showDialog(this@VegaActivity)
            R.id.bt_input -> {
                inputPortDialog(this@VegaActivity)
                if (Build.MODEL != "rk3288") {
                    println("0000000000000")
                    if (checkSoFile(libraries)) {
                        println("11111111111111111")
                        mHandler.post(inputOneRunable)
                        mHandler.post(inputTwoRunable)
                    }
                }
            }
            R.id.bt_camera -> startActivity(Intent(this@VegaActivity, CamerasActivity::class.java))
            R.id.bt_sensor -> {
                sensorDialog(this@VegaActivity)
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
            R.id.bt_rfid -> startActivity(Intent(this@VegaActivity, RfidActivity::class.java))
        }
    }

    private fun showDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_port, null)
        var title = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

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
        rs485!!.getData(object : RS485Util.OnGetDataListener {
            override fun onDataReceive(var1: String) {
                runOnUiThread { tvInfo.setText(HexUtil.ByteArrToHex(var1.toByteArray())) }
            }
        })
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

    private fun inputPortDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_input_port, null)
        inputOne = view.findViewById(R.id.tv_input_1)
        inputTwo = view.findViewById(R.id.tv_input_2)
        ivInputOne = view.findViewById(R.id.iv_input_1)
        ivInputTwo = view.findViewById(R.id.iv_input_2)
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

    override fun onDestroy() {
        super.onDestroy()
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
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
}