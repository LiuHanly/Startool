package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.activity.WifiActivity
import com.glorystar.startool.ui.broadcast.WIFIStateReceiver
import com.glorystar.startool.ui.service.WIFIAutoConnectionService
import com.glorystar.startool.ui.view.ColorGradientProgressBar
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import com.glorystar.startool.utils.tool.WIFIConnectionManager

class NewWifiActivity : BaseActivity() {

    lateinit var mWIFIStateReceiver: WIFIStateReceiver
    var networkId = 0
    lateinit var colorgradgressbar: ColorGradientProgressBar
    lateinit var ap_name: TextView
    lateinit var Android_version: TextView
    lateinit var text_Model: TextView
    lateinit var failure_bu: Button
    lateinit var through_bu: Button
    lateinit var button5: TextView

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewWifiActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    val rssi =
                        WIFIConnectionManager.getInstance(this@NewWifiActivity).wifiManager.connectionInfo.rssi
                    networkId =
                        WIFIConnectionManager.getInstance(this@NewWifiActivity).wifiManager.connectionInfo.networkId
                    colorgradgressbar.setProgress(
                        100 - Math.abs(
                            WIFIConnectionManager.getInstance(
                                this@NewWifiActivity
                            ).wifiManager.connectionInfo.rssi.toFloat()
                        )
                    )
                    ap_name.text =
                        WIFIConnectionManager.getInstance(this@NewWifiActivity).wifiManager.connectionInfo.ssid
                    Android_version.text = "$rssi bdm"

                    if (inetse % 5 == 0) {
                        if (rssi < -100) {
                            WIFIConnectionManager.getInstance(this@NewWifiActivity).connect(
                                Constantdtas.WIFI_SSID, Constantdtas.WIFI_PASS
                            );
                        }
                    }

                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_new_wifi
    override fun initView(savedInstanceState: Bundle?) {
        colorgradgressbar = findViewById(R.id.colorgradgressbar)
        ap_name = findViewById(R.id.ap_name)
        Android_version = findViewById(R.id.Android_version)
        text_Model = findViewById(R.id.text_Model)

        through_bu = findViewById(R.id.through_bu)

        getFile()
        mWIFIStateReceiver = WIFIStateReceiver(this)

        registerReceiver(
            mWIFIStateReceiver,
            IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        )
        WIFIAutoConnectionService.start(
            this@NewWifiActivity,
            Constantdtas.WIFI_SSID,
            Constantdtas.WIFI_PASS
        )
        LiveDataBus.get().with("WifiActivity", String::class.java).observe(this, Observer {
              when (it) {
                  "WIFI_STATE_ENABLED" -> {
                      text_Model.text = "Connected"
                      ap_name.text =
                          WIFIConnectionManager.getInstance(this).wifiManager.connectionInfo.ssid
                      handler.post(updateThread)
                  }
                  else -> {
                      text_Model.text = "unconnected"
                  }
              }
          })

        through_bu.setOnClickListener {
            StatusNaviBarUitl.hide(this@NewWifiActivity)
            IntenAcitivity.getIntenAcitivity(this@NewWifiActivity, 3, 2,1)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateThread)

        WIFIConnectionManager.getInstance(this).disconnect()
        WIFIConnectionManager.getInstance(this).closeWifi()

        unregisterReceiver(mWIFIStateReceiver)
    }

    override fun onStop() {
        super.onStop()
        StatusNaviBarUitl.show(this@NewWifiActivity)

    }

    override fun onRestart() {
        super.onRestart()
        StatusNaviBarUitl.hide(this@NewWifiActivity)
    }

    fun getFile() {
        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    var inetse = 0
    val handler = Handler()
    val updateThread: Runnable = object : Runnable {
        override fun run() {
            val message = Message()
            message.arg1 = 0
            mHandler.dispatchMessage(message)
            //在run方法内部，执行postDelayed或者是post方法
            inetse++
            handler.postDelayed(this, 1000)
            //do something
            //每隔1s循环执行run方法
        }
    }
}