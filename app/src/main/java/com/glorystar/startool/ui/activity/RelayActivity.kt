package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.glorystar.relaylibrary.Sdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class RelayActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var btRelay1Open: Button
    lateinit var btRelay1Close: Button
    lateinit var btRelay2Open: Button
    lateinit var btRelay2Close: Button
    private var relaySdk: Sdk? = null

    override fun getLayoutId(): Int = R.layout.activity_relay

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.relay)
        titleView.action = this

        relaySdk = Sdk()

        btRelay1Open = findViewById(R.id.bt_relay1_open)
        btRelay1Close = findViewById(R.id.bt_relay1_close)
        btRelay2Open = findViewById(R.id.bt_relay2_open)
        btRelay2Close = findViewById(R.id.bt_relay2_close)

        btRelay1Open.setOnClickListener(this)
        btRelay1Close.setOnClickListener(this)
        btRelay2Open.setOnClickListener(this)
        btRelay2Close.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_relay1_open -> relaySdk!!.openRelay(1)
            R.id.bt_relay1_close -> relaySdk!!.closeRelay(1)
            R.id.bt_relay2_open -> relaySdk!!.openRelay(2)
            R.id.bt_relay2_close -> relaySdk!!.closeRelay(2)
        }
    }
}