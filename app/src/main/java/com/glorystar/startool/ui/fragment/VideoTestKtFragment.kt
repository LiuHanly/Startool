package com.glorystar.startool.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseFragemt
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.ui.activity.AgeingTestReportActivity
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.db.AgeingDao
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.LiveDataBus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VideoTestKtFragment : BaseFragemt() {
   var mPath: String? = null
    val TAG = "VideoTestKtFragment"
    var countDownTimer: CountDownTimer? = null
    var mVideoView: VideoView? = null
    var TIME: Long? = null
    var   stTime =""
    var ado: AgeingDao? = null
    lateinit var time_text :TextView

    override val layoutId: Int
        get() = R.layout.videotestktfragment

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        ado = activity?.let { AppDataBase.getDatabase(it).getAgeingDao() }
        stTime   = DateUtil.stampToTime(DateUtil.timeStamp_L.toString())!!
        time_text = view?.findViewById(R.id.time_text)!!

        mVideoView = view?.findViewById(R.id.video_view)
        LiveDataBus.get().with("VideoTestKtFragment",String::class.java).observe(this, Observer {
          val name = it.split(":")
            when(name[0]){
                "start" -> {
                    playVideo()
                    setGOms(name[1].toLong())
                }
                "stop" ->{
                    time_text.text = "完成"
                    stop()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }


    fun setGOms(thie: Long) {
        countDownTimer = object : CountDownTimer(thie, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

                val day = millisUntilFinished / (1000 * 24 * 60 * 60) //单位天
                val hour =
                    (millisUntilFinished - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60)
                //单位时
                val minute =
                    (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60)
                //单位分
                val second =
                    (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000
                //单位秒
                time_text.text = "$hour:$minute:$second /h"
                TIME = millisUntilFinished

                GlobalScope.launch {
                    ado?.update(
                        Ageing(3, "VideoTestKtFragment",
                        AgeingDataBase.getInstance(context).ageingDao.getAgeingID(2).AgeungStopTime,
                        stTime, "2", millisUntilFinished.toString())
                    )
                }
            }

            /**
             * 倒计时结束后调用的
             */
            override fun onFinish() {
                stop()
                time_text.text = "完成"
                LiveDataBus.get().with("AgeingKtActivity").postValue("VideoTestKtFragment:0")
                AgeingTestReportActivity.toStartActivity(activity!!)

            }
        }
        countDownTimer?.start()

    }


    private fun playVideo(): Boolean {

        val mc = MediaController(activity)
        mVideoView!!.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        mVideoView!!.setVideoPath("android.resource://" + context?.packageName.toString() + "/" + R.raw.testvideo)
        mVideoView!!.setOnCompletionListener {
            Log.d(TAG, "onCompletion, restart")
            mVideoView!!.start()
        }
        mVideoView!!.setOnPreparedListener { }
        mVideoView!!.setOnErrorListener { mp, what, extra ->

            mVideoView!!.start()
            false
        }
        mVideoView!!.requestFocus()
        mVideoView!!.start()
        return true
    }

    private fun stopVideo() {
        mVideoView!!.stopPlayback()
    }

    fun start() {
        playVideo()

    }

    fun stop() {
        stopVideo()
    }

    override fun onStop() {
        super.onStop()
        countDownTimer?.cancel()
        if (TIME!=null){
            LiveDataBus.get().with("AgeingKtActivity").postValue("VideoTestKtFragment:$TIME")
        }
    }




}