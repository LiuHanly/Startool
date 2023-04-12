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
import java.math.BigInteger

class RfidActivity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var retrievedValue: EditText
    private var rfid: RS485Util? = null
    private val TYPE_ID = "02"
    private val TYPE_IC = "01"

    override fun getLayoutId(): Int = R.layout.activity_rfid

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.rfid)
        titleView.action = this

        retrievedValue = findViewById(R.id.et_retrieved_value)

        if (Build.MODEL == "rk3399") {
            rfid = RS485Util(application)
            rfid!!.setCOM("/dev/ttyUSB1")
            rfid?.start()
            rfid!!.getData { buffer, size ->
                runOnUiThread {
                    val data = ByteArray(size)
                    for (i in 0 until size) {
                        data[i] = buffer[i]
                    }
                    retrievedValue.setText(HexUtil.ByteArrToHex(data))
                }
            }
        }else if (Build.MODEL == "rk3568"){
            rfid = RS485Util(application)
            rfid!!.setCOM("/dev/ttyS7")
            rfid?.start()
            rfid!!.getData { buffer, size ->
                runOnUiThread {
                    val data = ByteArray(size)
                    for (i in 0 until size) {
                        data[i] = buffer[i]
                    }
                    var hexData = HexUtil.ByteArrToHex(data).replace(" ","")
                    println(hexData)
                    var cardType = hexData.substring(4,6)
                    var cardData: String? = null
                    if (cardType == TYPE_ID){
                        cardData = hexData.substring(8, hexData.length - 4)
                    } else if (cardType == TYPE_IC){
                        cardData = hexData.substring(6, hexData.length - 4)
                    }
                    retrievedValue.setText(cardData?.let { BigInteger(it, 16).toString() })
                }
            }
        }else {
            retrievedValue.requestFocus()
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