package com.glorystar.startool.ui.activity

import android.hardware.Camera
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
import com.glorystar.startool.ui.camera.CameraPreviewManager2

class DualCameraActivity : BaseActivity(), View.OnClickListener {
    var linear_back: LinearLayout? = null
    var textureView: AutoTexturePreviewView? = null
    var textureColorView: AutoTexturePreviewView? = null
    var cameraStatus = true
    var cameraColorStatus = true
    lateinit var btTurnOff: Button
    lateinit var btColorTurnOff: Button
    lateinit var rotate: ImageView
    lateinit var colorRotate: ImageView
    var rot = 90
    var colorRot = 90
    private var mCameraNum = 0

    override fun getLayoutId(): Int = R.layout.activity_dual_camera

    override fun initView(savedInstanceState: Bundle?) {
        linear_back = findViewById(R.id.linear_back)
        textureView = findViewById(R.id.autotexture_view)
        btTurnOff = findViewById(R.id.bt_turn_off)
        rotate = findViewById(R.id.iv_rotate)

        textureColorView = findViewById(R.id.autotexture_color_view)
        btColorTurnOff = findViewById(R.id.bt_color_turn_off)
        colorRotate = findViewById(R.id.iv_color_rotate)

        mCameraNum = Camera.getNumberOfCameras()

        linear_back?.setOnClickListener(this)
        btTurnOff.setOnClickListener(this)
        rotate.setOnClickListener(this)
        btColorTurnOff.setOnClickListener(this)
        colorRotate.setOnClickListener(this)

        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK)
        CameraPreviewManager.getInstance().startPreview(this, textureColorView, 640, 480)
        if (mCameraNum > 1) {
            CameraPreviewManager2.getInstance().cameraFacing =
                CameraPreviewManager.CAMERA_FACING_BACK
            CameraPreviewManager2.getInstance().startPreview(this, textureView, 640, 480)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.linear_back -> finish()
            R.id.bt_turn_off -> {
                if (CameraPreviewManager2.getInstance().mCamera != null) {
                    if (cameraStatus) {
                        btTurnOff.text = getString(R.string.turn_on)
                        CameraPreviewManager2.getInstance().mCamera.stopPreview()
                        cameraStatus = false
                    } else {
                        btTurnOff.text = getString(R.string.turn_off)
                        CameraPreviewManager2.getInstance().mCamera.startPreview()
                        cameraStatus = true
                    }
                } else {
                    Toast.makeText(this@DualCameraActivity, "No camera detected", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.iv_rotate -> {
                CameraPreviewManager2.getInstance().setRotate(rot)
                if (rot < 360) {
                    rot += 90
                } else {
                    rot = 90
                }
            }
            R.id.bt_color_turn_off -> {
                if (CameraPreviewManager.getInstance().mCamera != null) {
                    if (cameraColorStatus) {
                        btColorTurnOff.text = getString(R.string.turn_on)
                        CameraPreviewManager.getInstance().mCamera.stopPreview()
                        cameraColorStatus = false
                    } else {
                        btColorTurnOff.text = getString(R.string.turn_off)
                        CameraPreviewManager.getInstance().mCamera.startPreview()
                        cameraColorStatus = true
                    }
                } else {
                    Toast.makeText(this@DualCameraActivity, "No camera detected", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.iv_color_rotate -> {
                CameraPreviewManager.getInstance().setRotate(colorRot)
                if (colorRot < 360) {
                    colorRot += 90
                } else {
                    colorRot = 90
                }
            }
        }
    }
}