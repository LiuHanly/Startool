package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class AddonActivity  : BaseActivity(), View.OnClickListener{
    lateinit var titleView: TitleKtView
    lateinit var relativeBarcode: RelativeLayout
    lateinit var relativeCamera: RelativeLayout
    lateinit var relativeRfid: RelativeLayout
    lateinit var relativeLight: RelativeLayout

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_addon
    }

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.addon_modules)
        titleView.action = this

        relativeBarcode = findViewById(R.id.relative_barcode)
        relativeCamera = findViewById(R.id.relative_camera)
        relativeRfid = findViewById(R.id.relative_rfid)
        relativeLight = findViewById(R.id.relative_light)

        relativeBarcode.setOnClickListener(this)
        relativeCamera.setOnClickListener(this)
        relativeRfid.setOnClickListener(this)
        relativeLight.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_barcode -> startActivity(Intent(application, BarcodeModulesActivity::class.java))
            R.id.relative_camera -> startActivity(Intent(application, CameraModulesActivity::class.java))
            R.id.relative_rfid -> startActivity(Intent(application, RfidModulesActivity::class.java))
            R.id.relative_light -> startActivity(Intent(application, LightStripsActivity::class.java))
        }
    }
}