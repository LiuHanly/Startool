package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.camera.AutoTexturePreviewView
import com.glorystar.startool.ui.camera.CameraPreviewManager

class CamerasActivity : BaseActivity(),View.OnClickListener {
    var linear_back: LinearLayout? = null
    var textureView: AutoTexturePreviewView? = null
    var cameraStatus = true
    lateinit var btTurnOff: Button
    lateinit var rotate: ImageView
    var rot = 90

    override fun getLayoutId(): Int = R.layout.activity_cameras

    override fun initView(savedInstanceState: Bundle?) {
        linear_back = findViewById(R.id.linear_back)
        textureView = findViewById(R.id.autotexture_view)
        btTurnOff = findViewById(R.id.bt_turn_off)
        rotate = findViewById(R.id.iv_rotate)

        linear_back?.setOnClickListener(this)
        btTurnOff.setOnClickListener(this)
        rotate.setOnClickListener(this)

        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK)
        CameraPreviewManager.getInstance().startPreview(this, textureView, 640, 480)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.linear_back -> finish()
            R.id.bt_turn_off -> {
                if (CameraPreviewManager.getInstance().mCamera != null) {
                    if (cameraStatus) {
                        btTurnOff.text = getString(R.string.turn_on)
                        CameraPreviewManager.getInstance().mCamera.stopPreview()
                        cameraStatus = false
                    } else {
                        btTurnOff.text = getString(R.string.turn_off)
                        CameraPreviewManager.getInstance().mCamera.startPreview()
                        cameraStatus = true
                    }
                } else {
                    Toast.makeText(this@CamerasActivity, "No camera detected", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.iv_rotate -> {
                CameraPreviewManager.getInstance().setRotate(rot)
                if (rot < 360) {
                    rot += 90
                } else {
                    rot = 90
                }
            }
        }
    }
}