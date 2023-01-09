package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.glorystar.gsiosdk.GSIOSdk
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class LightStripsActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var btRed: Button
    lateinit var btYellow: Button
    lateinit var btGreen: Button
    lateinit var linearRed: LinearLayout
    lateinit var linearYellow: LinearLayout
    lateinit var linearGreen: LinearLayout
    private var ioSdk: GSIOSdk? = null
    private var green = false
    private var yellow = false
    private var red = false

    override fun getLayoutId(): Int = R.layout.activity_light_strips

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.light_strips)
        titleView.action = this

        ioSdk = GSIOSdk()

        btRed = findViewById(R.id.bt_red)
        btYellow = findViewById(R.id.bt_yellow)
        btGreen = findViewById(R.id.bt_green)
        linearRed = findViewById(R.id.linear_red)
        linearYellow = findViewById(R.id.linear_yellow)
        linearGreen = findViewById(R.id.linear_green)

        btRed.setOnClickListener(this)
        btYellow.setOnClickListener(this)
        btGreen.setOnClickListener(this)
        linearRed.setOnClickListener(this)
        linearYellow.setOnClickListener(this)
        linearGreen.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_red -> {
                btYellow.visibility = View.VISIBLE
                linearYellow.visibility = View.GONE
                btGreen.visibility = View.VISIBLE
                linearGreen.visibility = View.GONE
                btRed.visibility = View.GONE
                linearRed.visibility = View.VISIBLE
                ioSdk!!.red = 1
            }
            R.id.bt_yellow -> {
                btYellow.visibility = View.GONE
                btGreen.visibility = View.VISIBLE
                linearGreen.visibility = View.GONE
                btRed.visibility = View.VISIBLE
                linearRed.visibility = View.GONE
                linearYellow.visibility = View.VISIBLE
                ioSdk!!.setYellow(1)
            }
            R.id.bt_green -> {
                btYellow.visibility = View.VISIBLE
                linearYellow.visibility = View.GONE
                btGreen.visibility = View.GONE
                btRed.visibility = View.VISIBLE
                linearRed.visibility = View.GONE
                linearGreen.visibility = View.VISIBLE
                ioSdk!!.green = 1
            }
            R.id.linear_red -> {
                linearRed.visibility = View.GONE
                btRed.visibility = View.VISIBLE
                ioSdk!!.red = 0
            }
            R.id.linear_yellow -> {
                linearYellow.visibility = View.GONE
                btYellow.visibility = View.VISIBLE
                ioSdk!!.setYellow(0)
            }
            R.id.linear_green -> {
                linearGreen.visibility = View.GONE
                btGreen.visibility = View.VISIBLE
                ioSdk!!.green = 0
            }
        }
    }
}