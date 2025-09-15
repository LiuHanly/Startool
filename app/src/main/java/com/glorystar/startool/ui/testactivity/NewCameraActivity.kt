package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.tool.StatusNaviBarUitl


class NewCameraActivity : BaseActivity() {

    lateinit var through_bu: Button
    lateinit var f_bu: Button
    var ImgUri = ""
    var VoidUri = ""

    lateinit var image_url: ImageView
    lateinit var RecyclerLayout: VideoView


    val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                Glide.with(this@NewCameraActivity).load(ImgUri).fitCenter().into(image_url)
            } else if (msg.what == 1) {
                start()
            } else if (msg.what == 2) {
                getFile()
            }


        }
    }


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewCameraActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_new_camera

    override fun initView(savedInstanceState: Bundle?) {
        through_bu = findViewById(R.id.through_bu)
        f_bu = findViewById(R.id.f_bu)
        image_url = findViewById(R.id.image_url)
        RecyclerLayout = findViewById(R.id.RecyclerLayout)

        through_bu.setOnClickListener {
            StatusNaviBarUitl.hide(this@NewCameraActivity)
            IntenAcitivity.getIntenAcitivity(this@NewCameraActivity, 5, 2, 1)
        }
        f_bu.setOnClickListener {
            getFile()
        }
        handler.sendEmptyMessage(2)


    }

    override fun onRestart() {
        super.onRestart()
        StatusNaviBarUitl.hide(this@NewCameraActivity)
        ImgUri = Constantdtas.getLatestImage(this).toString()
        //  VoidUri = Constantdtas.getLatestVideo(this).toString()
        VoidUri = Constantdtas.getVideosInfo(this).toString()
        if (ImgUri != "") {
            handler.sendEmptyMessage(0)
        }
        if (VoidUri != "") {
            handler.sendEmptyMessage(1)
        }


    }

    override fun onStop() {
        super.onStop()
        StatusNaviBarUitl.show(this@NewCameraActivity)
    }


    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    fun getFile() {

        if (hasCameraSupport()) {
            //启动系统相机
            val intent = Intent()
            intent.setClassName("com.android.camera2", "com.android.camera.CameraActivity")
            // intent.action = MediaStore.ACTION_IMAGE_CAPTURE
            startActivityForResult(intent, 1)
            //StatusNaviBarUitl.show(this@CameraActivity)
        } else {

            Toast.makeText(
                this@NewCameraActivity,
                this.getString(R.string.camerasupp),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun checkCameraFacing(facing: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return false
        }
        val cameraCount = Camera.getNumberOfCameras()
        println(cameraCount)
        val info = Camera.CameraInfo()
        for (i in 0 until cameraCount) {
            Camera.getCameraInfo(i, info)
            if (facing == info.facing) {
                return true
            }
        }
        return false
    }

    /**
     * 检查设备是否有前置摄像头
     *  @returntrue,有前置摄像头；false,前置摄像头
     *
     */
    fun hasFrontFacingCamera(): Boolean {
        val CAMERA_FACING_FRONT = 1
        return checkCameraFacing(CAMERA_FACING_FRONT)
    }


    private fun hasCameraSupport(): Boolean {
        return hasFrontFacingCamera() || hasBackFacingCamera()
    }

    fun hasBackFacingCamera(): Boolean {
        val CAMERA_FACING_BACK = 0
        return checkCameraFacing(CAMERA_FACING_BACK)
    }

    /**
     * 获取手机中所有视频的信息
     */


    private fun playVideo(): Boolean {

        val mc = MediaController(this)
        RecyclerLayout.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        RecyclerLayout.setVideoPath(VoidUri)
        RecyclerLayout.setOnCompletionListener {
            RecyclerLayout.start()
        }
        RecyclerLayout.setOnPreparedListener { }
        RecyclerLayout.setOnErrorListener { mp, what, extra ->

            RecyclerLayout.start()
            false
        }
        RecyclerLayout.requestFocus()
        RecyclerLayout.start()
        return true
    }

    private fun stopVideo() {
        RecyclerLayout.stopPlayback()
    }

    fun start() {
        playVideo()

    }

    fun stop() {
        stopVideo()
    }


}