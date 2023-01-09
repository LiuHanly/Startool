package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.glorystar.gsiosdk.GSIOSdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class GpioActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var btGpio1Open: Button
    lateinit var btGpio1Close: Button
    lateinit var btGpio2Open: Button
    lateinit var btGpio2Close: Button
    private var gsioSdk: GSIOSdk? = null

    override fun getLayoutId(): Int = R.layout.activity_gpio

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.gpio)
        titleView.action = this

        btGpio1Open = findViewById(R.id.bt_gpio1_open)
        btGpio1Close = findViewById(R.id.bt_gpio1_close)
        btGpio2Open = findViewById(R.id.bt_gpio2_open)
        btGpio2Close = findViewById(R.id.bt_gpio2_close)

        btGpio1Open.setOnClickListener(this)
        btGpio1Close.setOnClickListener(this)
        btGpio2Open.setOnClickListener(this)
        btGpio2Close.setOnClickListener(this)

        gsioSdk = GSIOSdk()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_gpio1_open -> gsioSdk!!.setA(1)
            R.id.bt_gpio1_close -> gsioSdk!!.setA(0)
            R.id.bt_gpio2_open -> gsioSdk!!.setB(1)
            R.id.bt_gpio2_close -> gsioSdk!!.setB(0)
        }
    }
}