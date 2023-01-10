package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class CameraModulesActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var relativeHdrCamera: RelativeLayout
    lateinit var relativeDualCamera: RelativeLayout

    override fun getLayoutId(): Int = R.layout.activity_camera_modules

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.camera)
        titleView.action = this

        relativeHdrCamera = findViewById(R.id.relative_hdr_camera)
        relativeDualCamera = findViewById(R.id.relative_dual_camera)

        relativeHdrCamera.setOnClickListener(this)
        relativeDualCamera.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_hdr_camera -> startActivity(Intent(this@CameraModulesActivity, CamerasActivity::class.java))
            R.id.relative_dual_camera -> startActivity(Intent(this@CameraModulesActivity, DualCameraActivity::class.java))
        }
    }
}