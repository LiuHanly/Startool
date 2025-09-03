package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.Rootcommands
import java.io.FileInputStream
import java.io.IOException
import java.util.regex.Pattern

class WiegandActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    private val PATH = "/sys/kernel/wiegand/wiegand_read"
    private val WRITE_PATH = "/sys/kernel/wiegand/wiegand26"
    private val PATH_2 = "/sys/kernel/wiegand_b/wiegand_read_b"
    private val WRITE_PATH_2 = "/sys/kernel/wiegand_b/wiegand26_b"
    lateinit var btStartTest: Button
    lateinit var btSender: Button
    lateinit var btReceiver: Button
    lateinit var inputInfo: TextView
    lateinit var hid: String
    lateinit var pid: String

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

    var sendRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                Rootcommands.runRootCommand("echo '$hid:$pid'>$WRITE_PATH")
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
            mHandler.postDelayed(this, 500)
        }
    }

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

    override fun getLayoutId(): Int = R.layout.activity_wiegand

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.wiegand_interface)
        titleView.action = this

        btStartTest = findViewById(R.id.bt_start_test)
        btSender = findViewById(R.id.bt_sender)
        btReceiver = findViewById(R.id.bt_receiver)

        btStartTest.setOnClickListener(this)
        btSender.setOnClickListener(this)
        btReceiver.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_start_test -> {
                showDialog(this@WiegandActivity, getString(R.string.wiegand_listening_detail), getString(R.string.wiegand_input_tip))
                mHandler.post(wiegandRunable)
            }
            R.id.bt_sender -> {
                sendDialog(this@WiegandActivity)

            }
            R.id.bt_receiver -> {
                showDialog(this@WiegandActivity, getString(R.string.receiver_input_detail), getString(R.string.receiver_input_tip))
                mHandler.post(wiegandRunable)
            }
        }
    }

    private fun showDialog(context: Context?, detail: String, tip: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_wiegand_listening, null)
        inputInfo = view.findViewById<TextView>(R.id.retrieve_value)
        var inputDetail = view.findViewById<TextView>(R.id.tv_input_detail)
        var inputTip = view.findViewById<TextView>(R.id.tv_input_tip)
        var stop = view.findViewById<TextView>(R.id.stop_listening)

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
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private fun sendDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_wiegand_sender, null)
        var edtHid = view.findViewById<EditText>(R.id.edt_input_hid)
        var edtPid = view.findViewById<EditText>(R.id.edt_input_pid)
        var btSend = view.findViewById<Button>(R.id.bt_send)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        edtHid.addTextChangedListener(inputWatch(edtHid, 255))
        edtPid.addTextChangedListener(inputWatch(edtPid, 65535))

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener {
            dialog.dismiss()
        }

        btSend.setOnClickListener {
            hid = edtHid.text.toString()
            pid = edtPid.text.toString()
            if (!TextUtils.isEmpty(hid) && !TextUtils.isEmpty(pid)){
                sendProcessingDialog(this@WiegandActivity, hid, pid)
                mHandler.post(sendRunnable)
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun sendProcessingDialog(context: Context?, hid: String, pid: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_sender_processing, null)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var stop = view.findViewById<TextView>(R.id.tv_go_back)

        tvInfo.text = "HID:$hid\nPID:$pid"

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        stop.setOnClickListener {
            mHandler.removeCallbacks(sendRunnable)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }


    fun inputWatch(input: EditText, MAX_NUM: Int): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val words = s.toString()
                //首先内容进行非空判断，空内容（""和null）不处理
                if (!TextUtils.isEmpty(words)) {
                    if (isNumeric(words)) {
                        if (Integer.valueOf(words) > MAX_NUM) {
                            input.setText(MAX_NUM.toString() + "")
                        }
                    } else {
                        Toast.makeText(application, "must enter a number", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                //这里的处理是不让输入0开头的值
                val words = s.toString()
                //首先内容进行非空判断，空内容（""和null）不处理
                if (!TextUtils.isEmpty(words)) {
                    println(words)
                    if (isNumeric(words)) {
                        if (s.toString().toInt() < 0) {
                            input.setText("")
                        }
                    } else {
                        input.setText("")
                        Toast.makeText(application, "must enter a number", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    fun isNumeric(str: String?): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(str)
        return if (!isNum.matches()) {
            false
        } else true
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}