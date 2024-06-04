package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.vodlibrary.VodLib

class Vod101RelayActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var btRelay1Open: Button
    lateinit var btRelay1Close: Button
    lateinit var btRelay2Open: Button
    lateinit var btRelay2Close: Button
    lateinit var btRelay3Open: Button
    lateinit var btRelay3Close: Button

    override fun getLayoutId(): Int = R.layout.activity_vod101_relay

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.relay)
        titleView.action = this
        Log.e("Vod101RelayActivity","Vod101RelayActivity***--->")

        btRelay1Open = findViewById(R.id.bt_relay1_open)
        btRelay1Close = findViewById(R.id.bt_relay1_close)
        btRelay2Open = findViewById(R.id.bt_relay2_open)
        btRelay2Close = findViewById(R.id.bt_relay2_close)
        btRelay3Open = findViewById(R.id.bt_relay3_open)
        btRelay3Close = findViewById(R.id.bt_relay3_close)

        btRelay1Open.setOnClickListener(this)
        btRelay1Close.setOnClickListener(this)
        btRelay2Open.setOnClickListener(this)
        btRelay2Close.setOnClickListener(this)
        btRelay3Open.setOnClickListener(this)
        btRelay3Close.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_relay1_open -> VodLib.openRelay(1)
            R.id.bt_relay1_close -> VodLib.closeRelay(1)
            R.id.bt_relay2_open -> VodLib.openRelay(2)
            R.id.bt_relay2_close -> VodLib.closeRelay(2)
            R.id.bt_relay3_open -> VodLib.openRelay(3)
            R.id.bt_relay3_close -> VodLib.closeRelay(3)
        }
    }
}