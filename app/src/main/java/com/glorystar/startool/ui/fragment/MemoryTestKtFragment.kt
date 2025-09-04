package com.glorystar.startool.ui.fragment

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.github.lzyzsd.circleprogress.ArcProgress
import com.github.lzyzsd.circleprogress.DonutProgress
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseFragemt
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.db.AgeingDao
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.MemOpUtils.free
import com.glorystar.startool.utils.tool.MemOpUtils.malloc

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import java.util.*

class MemoryTestKtFragment : BaseFragemt() {


    private var mFreeMemory: Long = 0
    private var mUsedMemory: Long = 0
    private var mTotalMemory: Long = 0
    private var mFillPercent = 0
    private var mCountDown = 30
    private var mMallocMemoryTimer: Timer? = null
    private var mAm: ActivityManager? = null
    var countDownTimer: CountDownTimer? = null
    var stTime = ""
    var ado: AgeingDao? = null
    var TIME: Long? = null

    lateinit var pgr_freq: DonutProgress
    lateinit var time_text: TextView


    lateinit var tv_freq_max: TextView
    lateinit var tv_freq: TextView

    companion object {
        private var sMemoryFillPercentList: IntArray? = null
    }

    override val layoutId: Int
        get() = R.layout.emmctest_fragment

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        ado = activity?.let { AppDataBase.getDatabase(it).getAgeingDao() }
        stTime = DateUtil.stampToTime(DateUtil.timeStamp_L.toString())!!
        pgr_freq = view?.findViewById(R.id.pgr_freq)!!
        tv_freq = view?.findViewById(R.id.tv_freq)!!

        mAm = activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val systemMemInfo = ActivityManager.MemoryInfo()
        mAm!!.getMemoryInfo(systemMemInfo)
        mFreeMemory = systemMemInfo.availMem / 1024 / 1024
        mTotalMemory = systemMemInfo.totalMem / 1024 / 1024

        mUsedMemory = mTotalMemory - mFreeMemory
        tv_freq = view?.findViewById(R.id.tv_freq)!!
        tv_freq_max = view?.findViewById(R.id.tv_freq_max)!!
        time_text = view?.findViewById(R.id.time_text)!!

        tv_freq?.text = mFreeMemory.toString() + ""
        tv_freq_max?.text = mTotalMemory.toString() + ""



        LiveDataBus.get().with("MemoryTestKtFragment", String::class.java)
            .observe(this, androidx.lifecycle.Observer {
                val name = it.split(":")
                when (name[0]) {
                    "start" -> {
                        onstart()
                        setGOms(name[1].toLong())
                    }
                    "MemoryTestKtFragment" -> {
                        val systemMemInfo = ActivityManager.MemoryInfo()
                        mAm!!.getMemoryInfo(systemMemInfo)
                        mFreeMemory = systemMemInfo.availMem / 1024 / 1024
                        mTotalMemory = systemMemInfo.totalMem / 1024 / 1024
                        mUsedMemory = mTotalMemory - mFreeMemory

                        val progress = (mUsedMemory * 100 / mTotalMemory).toInt()
                        pgr_freq.progress = progress.toFloat()

                        tv_freq.text = mFreeMemory.toString() + ""
                        tv_freq_max.text = mTotalMemory.toString() + ""
                        Log.e("countDownTimer", " progress ->" + progress)
                    }
                    "stop" -> {
                        time_text.text = "完成"
                        onstop()
                    }

                }
            })
    }


    fun onstart() {

        mCountDown = 30
        mMallocMemoryTimer = Timer()
        mMallocMemoryTimer?.schedule(object : TimerTask() {
            @SuppressLint("LogNotTimber")
            override fun run() {

                Log.e(
                    "onStart()",
                    "mUsedMemory--->" + mUsedMemory + "mTotalMemory" + mTotalMemory + "mFillPercent" + mFillPercent
                )
                if (mUsedMemory * 100 / mTotalMemory < 80) {
                    malloc(10) // Malloc 10M memory
                } else {
                    if (mCountDown > 0) {
                        mCountDown--
                    } else {
                        if (mMallocMemoryTimer != null) {
                            mMallocMemoryTimer!!.cancel()
                            mMallocMemoryTimer = null
                        }
                    }
                }
            }

        }, 1000, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        onstop()

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
                LiveDataBus.get().with("MemoryTestKtFragment").postValue("MemoryTestKtFragment")
                GlobalScope.launch {
                    ado?.update(
                        Ageing(
                            1, "MemoryTestKtFragment",
                            AgeingDataBase.getInstance(context).ageingDao.getAgeingID(0).AgeungStopTime,
                            stTime, "2", millisUntilFinished.toString()
                        )
                    )
                }
            }

            /**
             * 倒计时结束后调用的
             */
            override fun onFinish() {
                onstop()
                time_text.text = "0:0:0 /h"
                LiveDataBus.get().with("MemoryTestKtFragment").postValue("stop")
                LiveDataBus.get().with("AgeingKtActivity").postValue("MemoryTestKtFragment:0")

            }
        }
        countDownTimer?.start()

    }

    fun onstop() {
        free()
        if (mMallocMemoryTimer != null) {
            mMallocMemoryTimer!!.cancel()
            mMallocMemoryTimer = null
        }
    }


}