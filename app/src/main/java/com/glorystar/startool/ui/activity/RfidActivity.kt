package com.glorystar.startool.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS485Util

class RfidActivity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var retrievedValue: EditText
    private var rfid: RS485Util? = null

    override fun getLayoutId(): Int = R.layout.activity_rfid

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.rfid)
        titleView.action = this

        retrievedValue = findViewById(R.id.et_retrieved_value)

        retrievedValue.setShowSoftInputOnFocus(false)

        if (Build.MODEL == "rk3399") {
            retrievedValue.clearFocus()
            rfid = RS485Util(application)
            rfid!!.setCOM("/dev/ttyUSB1")
            rfid?.start()
            rfid!!.getData(object : RS485Util.OnGetDataListener {
                override fun onDataReceive(var1: String) {
                    runOnUiThread { retrievedValue.setText(HexUtil.ByteArrToHex(var1.toByteArray())) }
                }
            })
        }else if (Build.MODEL == "rk3568"){
            retrievedValue.clearFocus()
            rfid = RS485Util(application)
            rfid!!.setCOM("/dev/ttyS7")
            rfid?.start()
            rfid!!.getData(object : RS485Util.OnGetDataListener {
                override fun onDataReceive(var1: String) {
                    runOnUiThread { retrievedValue.setText(HexUtil.ByteArrToHex(var1.toByteArray())) }
                }
            })
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus != null) {
                if (currentFocus!!.windowToken != null) {
                    imm.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.MODEL == "rk3399" || Build.MODEL == "rk3568"){
            rfid!!.stop()
        }
    }
}