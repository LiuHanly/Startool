package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class RfidActivity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var retrievedValue: EditText

    override fun getLayoutId(): Int = R.layout.activity_rfid

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.rfid)
        titleView.action = this

        retrievedValue = findViewById(R.id.et_retrieved_value)

        retrievedValue.setShowSoftInputOnFocus(false)

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
}