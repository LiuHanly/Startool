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
import com.glorystar.startool.utils.tool.RS232Util

class HIDRfidActivity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var retrievedValue: EditText
    lateinit var rfid: RS232Util

    override fun getLayoutId(): Int = R.layout.activity_hid_rfid
    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.rfid)
        titleView.action = this

        retrievedValue = findViewById(R.id.et_retrieved_value)

        rfid = RS232Util(application)
        rfid.setBaudrate(115200)
        if (Build.MODEL.startsWith("rk3399")) {
            rfid.setCOM("/dev/ttyUSB1")
        }else if(Build.MODEL.startsWith("rk3576_u")){
            rfid.setCOM("/dev/ttyS9")
        }



        rfid.start()
        rfid.getData { buffer, size ->
            runOnUiThread{
                val data = String(buffer,0, size)
                retrievedValue.setText(data)
            }
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
        rfid.stop()
    }
}