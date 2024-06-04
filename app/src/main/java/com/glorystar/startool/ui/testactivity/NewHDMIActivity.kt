package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.PlayThread
import com.glorystar.startool.utils.tool.StatusNaviBarUitl

class NewHDMIActivity : BaseActivity() {

    lateinit var RecyclerLayout: VideoView
    lateinit var through_bu: Button
    lateinit var left_volime: ImageView
    lateinit var right_volime: ImageView

    var mChannelLeftPlayer: PlayThread? = null
    var mChannelRightPlayer: PlayThread? = null

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewHDMIActivity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    left_volime.visibility = View.GONE
                    right_volime.visibility = View.VISIBLE
                    setRunalie(2)

                }

                1 -> {
                    left_volime.visibility = View.VISIBLE
                    right_volime.visibility = View.GONE
                    setRunalie(1)
                }
            }
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_new_hdmiactivity

    override fun initView(savedInstanceState: Bundle?) {
        RecyclerLayout = findViewById(R.id.RecyclerLayout)
        through_bu = findViewById(R.id.through_bu)
        left_volime = findViewById(R.id.left_volime)
        right_volime = findViewById(R.id.right_volime)

        StatusNaviBarUitl.hide(this@NewHDMIActivity)
        start()
        through_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@NewHDMIActivity, 9, 2, 1)

        }

        mHandler.sendEmptyMessage(1)
        LiveDataBus.get().with("NewHDMIActivity", String::class.java).observe(this, {
            if (it.contains("channaeleft.wav")) {
                mHandler.sendEmptyMessage(0)
            } else if (it.contains("channelright.wav")) {
                mHandler.sendEmptyMessage(1)
            }


        })

    }

    override fun onDestroy() {
        super.onDestroy()
        mChannelLeftPlayer?.stopp()
        mChannelRightPlayer?.stopp()
        mChannelLeftPlayer == null
        mChannelRightPlayer == null
        stop()
    }

    private fun playVideo(): Boolean {

        val mc = MediaController(this)
        RecyclerLayout.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        RecyclerLayout.setVideoPath("android.resource://" + packageName.toString() + "/" + R.raw.testhdmi)
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

    fun setRunalie(Player: Int) {
        mChannelLeftPlayer?.stopp()
        mChannelRightPlayer?.stopp()
        mChannelLeftPlayer == null
        mChannelRightPlayer == null

        mChannelLeftPlayer = PlayThread(this, "channaeleft.wav")
        mChannelRightPlayer = PlayThread(this, "channelright.wav")

        mChannelLeftPlayer?.setChannel(true, false)
        mChannelRightPlayer?.setChannel(false, true)
        if (Player == 1) {
            mChannelLeftPlayer?.start()

        } else {

            mChannelRightPlayer?.start()
        }

    }


}