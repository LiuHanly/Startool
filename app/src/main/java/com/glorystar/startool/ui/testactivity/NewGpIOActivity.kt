package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.glorystar.gsiosdk.GSIOSdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS232Util
import com.glorystar.startool.utils.tool.RS485Util
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger

class NewGpIOActivity : BaseActivity(), View.OnClickListener {

    lateinit var titleView: TitleKtView
    lateinit var btGpio1Open: Button
    lateinit var btGpio1Close: Button
    lateinit var btGpio2Open: Button
    lateinit var btGpio2Close: Button
    private var gsioSdk: GSIOSdk? = null
    lateinit var through_bu: Button
    lateinit var btUart: Button

    lateinit var bt_uart1: Button
    var rs232: RS232Util? = null
    private var rs485: RS485Util? = null
    private val TYPE_ID = "02"
    private val TYPE_IC = "01"
    lateinit var inputInfo: TextView
    private val PATH = "/sys/kernel/wiegand/wiegand_read"

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewGpIOActivity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    inputInfo.setText("")
                    inputInfo.setText(msg.obj.toString().trim { it <= ' ' } as CharSequence)
                }

                else -> {
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_new_gp_ioactivity

    var wiegandRunable: Runnable = object : Runnable {
        override fun run() {
            var fileInputStream: FileInputStream? = null
            var result: String? = null
            try {
                fileInputStream = FileInputStream(PATH)
                val buff = ByteArray(1024)
                var length = 0
                while (fileInputStream.read(buff).also { length = it } != -1) {
                    result = String(buff, 0, length)
                }
                val message: Message = mHandler.obtainMessage()
                message.what = 0
                message.obj = result
                mHandler.sendMessage(message)
                fileInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mHandler.postDelayed(this, 500)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {/*    titleView = findViewById(R.id.tile_view)
            titleView.title.text = getString(R.string.gpio)
            titleView.action = this*/
        rs232 = RS232Util(application)
        rs485 = RS485Util(application)

        if (Build.MODEL.equals("rk3568")) {
            rs232!!.setCOM("/dev/ttyS7")
        }

        if (Build.MODEL == "rk3399" || Build.MODEL.contains("rk3399")) {
            rs485!!.setCOM("/dev/ttyUSB1")
            rs485?.start()

        } else if (Build.MODEL == "rk3568" || Build.MODEL == "rk3568_VOD101" || Build.MODEL.contains(
                "rk3568"
            )
        ) {
            rs485!!.setCOM("/dev/ttyS7")
            rs485?.start()

        }
        btGpio1Open = findViewById(R.id.bt_gpio1_open)
        btGpio1Close = findViewById(R.id.bt_gpio1_close)
        btGpio2Open = findViewById(R.id.bt_gpio2_open)
        btGpio2Close = findViewById(R.id.bt_gpio2_close)
        through_bu = findViewById(R.id.through_bu)
        btUart = findViewById(R.id.bt_uart)

        bt_uart1 = findViewById(R.id.bt_uart1)


        btGpio1Open.setOnClickListener(this)
        btGpio1Close.setOnClickListener(this)
        btGpio2Open.setOnClickListener(this)
        btGpio2Close.setOnClickListener(this)
        through_bu.setOnClickListener(this)
        btUart.setOnClickListener(this)

        bt_uart1.setOnClickListener(this)

        gsioSdk = GSIOSdk()

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_gpio1_open -> gsioSdk!!.setA(1)
            R.id.bt_gpio1_close -> gsioSdk!!.setA(0)
            R.id.bt_gpio2_open -> gsioSdk!!.setB(1)
            R.id.bt_gpio2_close -> gsioSdk!!.setB(0)
            R.id.through_bu -> IntenAcitivity.getIntenAcitivity(this@NewGpIOActivity, 12, 2, 1)
            R.id.bt_uart -> {
                showDialog(this@NewGpIOActivity)
            }

            R.id.bt_uart1 -> {
                showWiegandDialog(
                    this@NewGpIOActivity,
                    getString(R.string.wiegand_listening_detail),
                    getString(R.string.wiegand_input_tip)
                )
                mHandler.post(wiegandRunable)
            }
        }

    }

    private fun showDialog(context: Context?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_port, null)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)
        var relative_help = view.findViewById<LinearLayout>(R.id.relative_help)
        relative_help.visibility = View.GONE

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
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

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


    private fun showWiegandDialog(context: Context?, detail: String, tip: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_wiegand_listening, null)
        inputInfo = view.findViewById<TextView>(R.id.retrieve_value)
        var inputDetail = view.findViewById<TextView>(R.id.tv_input_detail)
        var inputTip = view.findViewById<TextView>(R.id.tv_input_tip)
        var stop = view.findViewById<TextView>(R.id.stop_listening)
        var relative_help = view.findViewById<LinearLayout>(R.id.relative_help)
        relative_help.visibility = View.GONE

        inputDetail.text = detail
        inputTip.text = tip

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        stop.setOnClickListener {
            mHandler.removeCallbacks(wiegandRunable)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

    }


}