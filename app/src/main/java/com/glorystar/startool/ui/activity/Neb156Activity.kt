package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

import android.content.DialogInterface




class Neb156Activity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var btButtons: Button

    override fun getLayoutId(): Int = R.layout.activity_neb156

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_neb156)
        titleView.action = this

        btButtons = findViewById(R.id.bt_buttons)

        btButtons.setOnClickListener {
            buttonsDialog(this@Neb156Activity)
        }
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

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }
}