package com.glorystar.startool.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.glorystar.lightstrip.LightSdk
import com.glorystar.startool.R
import com.glorystar.startool.utils.tool.ColorPickerView
import com.glorystar.startool.utils.tool.HexUtil
import com.glorystar.startool.utils.tool.RS232Util
import com.glorystar.startool.utils.tool.Utils.runOnUiThread

object NebDialog {

    fun showDialog(context: Context?, rs232: RS232Util) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_port, null)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

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
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
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



    fun buttonsDialog(context: Context?) {
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
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun lightStripDialog(context: Context?) {
        var lightStatus = false
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
            } else {
                LightSdk.getInstance().openLight()
                ivLight.setImageResource(R.drawable.ic_light_on)
                lightStatus = true
            }
        }

        tvRed.setOnClickListener {
            LightSdk.getInstance().setLightColor(context!!.getColor(R.color.colorRed))
        }

        tvYellow.setOnClickListener {
            LightSdk.getInstance().setLightColor(context!!.getColor(R.color.colorYellow))
        }

        tvGreen.setOnClickListener {
            LightSdk.getInstance().setLightColor(context!!.getColor(R.color.colorAccent))
        }

        tvQing.setOnClickListener {
            LightSdk.getInstance().setLightColor(context!!.getColor(R.color.teal_200))
        }

        tvWhite.setOnClickListener {
            LightSdk.getInstance().setLightColor(context!!.getColor(R.color.white))
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