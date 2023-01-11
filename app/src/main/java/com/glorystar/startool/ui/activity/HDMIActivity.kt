package com.glorystar.startool.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.tool.StatusNaviBarUitl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * HDMI测试
 * */
class HDMIActivity : BaseActivity() {

    lateinit var failure_bu: Button
    lateinit var through_bu: Button
    lateinit var RecyclerLayout: VideoView

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, HDMIActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_hdmi
    }


    override fun initView(savedInstanceState: Bundle?) {
        RecyclerLayout = findViewById(R.id.RecyclerLayout)
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)
        StatusNaviBarUitl.hide(this@HDMIActivity)
        start()
        failure_bu.setOnClickListener {
            IntenAcitivity.getIntenAcitivity(this@HDMIActivity, 9, 1)
        }
        through_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@HDMIActivity, 9, 2)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    private fun playVideo(): Boolean {

        val mc = MediaController(this)
        RecyclerLayout.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        RecyclerLayout.setVideoPath("android.resource://" + packageName.toString() + "/" + R.raw.testvideo)
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