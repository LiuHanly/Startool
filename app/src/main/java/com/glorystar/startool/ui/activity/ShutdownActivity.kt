package com.glorystar.startool.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.MediaController
import android.widget.VideoView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.db.AgeingDao
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.Rootcommands
import com.glorystar.startool.utils.tool.SystemOperate

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//定时关机
class ShutdownActivity : BaseActivity() {
    var ado: AgeingDao? = null

    var stTime = ""
    lateinit var video_view: VideoView

    var countdowntimer: CountDownTimer? = null

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, ShutdownActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_shutdown

    override fun initView(savedInstanceState: Bundle?) {
        video_view = findViewById(R.id.video_view)
        playVideo()
        ado = AppDataBase.getDatabase(this@ShutdownActivity).getAgeingDao()
        stTime = DateUtil.stampToTime(DateUtil.timeStamp.toString())!!
        GlobalScope.launch {
            ado?.update(
                Ageing(
                    4, "ShutdownActivity",
                    AgeingDataBase.getInstance(this@ShutdownActivity).ageingDao.getAgeingID(3).AgeungStopTime,
                    stTime, "1", "0"
                )
            )
        }
        countdowntimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 240000) {
                    sines()


                }

            }

            override fun onFinish() {
                SystemOperate().rebootSystem()
            }
        }
        countdowntimer?.start()
    }

    private fun playVideo(): Boolean {

        val mc = MediaController(this)
        video_view!!.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        video_view!!.setVideoPath("android.resource://" + packageName.toString() + "/" + R.raw.loading)
        video_view!!.setOnCompletionListener {
            video_view!!.start()
        }
        video_view!!.setOnPreparedListener { }
        video_view!!.setOnErrorListener { mp, what, extra ->

            video_view!!.start()
            false
        }
        video_view!!.requestFocus()
        video_view!!.start()
        return true
    }

    fun sines() {
        val appPackageName = ""
        var cmd: String? = null
        cmd = if (Build.MODEL == "rk3128" || Build.MODEL == "rk3126c-041") {
            "echo 1 > /sys/class/backlight/rk28_bl/bl_power"
        } else {
            "echo 1 > /sys/class/backlight/backlight/bl_power"
        }
        cmd = "$cmd && am force-stop $appPackageName"
        try {
            Rootcommands.runRootCommand(cmd)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdowntimer?.cancel()
    }
}