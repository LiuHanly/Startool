package com.glorystar.startool.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.lzyzsd.circleprogress.ArcProgress
import com.github.lzyzsd.circleprogress.DonutProgress
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseFragemt
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.db.AgeingDao
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.tool.ArmFreqUtils
import com.glorystar.startool.utils.tool.CompuUtils
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.LiveDataBus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.*


@SuppressLint("LogNotTimber")
class CPUKtTestFragment : BaseFragemt() {
    protected var TAG: String? = "CPUKtTestFragment"
    var ado: AgeingDao? = null
    var stTime = ""
    private var mDelayCnt = DELAY_CNT_BASE
    private var mCpuRate = 100
    private var mCurFreq = 1680
    private var mMaxFreq = 1680
    private var mCurPercent = 0
    private var mCurMyPidPercent = 0
    protected var mAdapter: ArrayAdapter<String>? = null
    private var mFreqs: List<String>? = null
    private var mRandomFreqTimer: Timer? = null
    private var mCpuReaderThread: ReaderThread? = null
    var TIME: Long? = null
    lateinit var tv_freq_max: TextView
    lateinit var tv_freq: TextView
    lateinit var ext_text: TextView

    lateinit var tv_percent: TextView
    lateinit var time_text: TextView

    lateinit var pgr_percent: DonutProgress
    lateinit var pgr_freq: DonutProgress


    var countDownTimer: CountDownTimer? = null

    companion object {
        private const val MSG_RANDOM_SET_FREQ = 1001
        private var isRunning = true
        private var sIntervalTimeList: IntArray? = null
        private var sCpuRateList: IntArray? = null
        private const val DELAY_CNT_BASE = 10000
        private const val DELAY_CNT_STEP = 500
        private fun getPercentInRange(percent: Double): Double {
            return if (percent > 100.0) 100.0 else if (percent < 0.0) 0.0 else percent
        }
    }


    override val layoutId: Int
        get() = R.layout.cpu_fragment

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        ado = activity?.let { AppDataBase.getDatabase(it).getAgeingDao() }
        stTime = DateUtil.stampToTime(DateUtil.timeStamp_L.toString())!!
        tv_freq = view?.findViewById(R.id.tv_freq)!!
        tv_freq_max = view.findViewById(R.id.tv_freq_max)!!
        ext_text = view.findViewById(R.id.ext_text)!!
        tv_percent = view.findViewById(R.id.tv_percent)
        pgr_percent = view.findViewById(R.id.pgr_percent)
        time_text = view.findViewById(R.id.time_text)
        pgr_freq = view.findViewById(R.id.pgr_freq)!!

        val tCurCpuFreq = getCurCpuFreq()?.toLong()?.div(1000)

        tv_freq.text = "" + tCurCpuFreq
        tv_freq_max.text = "" + getMaxCpuFreq()?.toLong()?.div(1000)

        LiveDataBus.get().with("CPUKtTestFragment", String::class.java)
            .observe(this, androidx.lifecycle.Observer {
                val name = it.split(":")
                when (name[0]) {
                    "start" -> {
                        isRunning = true
                        start()
                        setGOms(name[1].toLong())
                    }
                    "ReaderThread" -> {
                        tv_percent.text = mCurPercent.toString() + ""
                        pgr_percent.progress = mCurPercent.toFloat()

                        val tCurCpuFreq = getCurCpuFreq()?.toLong()?.div(1000)
                        val tMaxCpuFreq = getMaxCpuFreq()?.toLong()?.div(1000)
                        val progress = CompuUtils.div(
                            tCurCpuFreq?.toDouble()!!,
                            tMaxCpuFreq?.toDouble()!!,
                            2
                        ) * 100



                        tv_freq.text = "" + tCurCpuFreq
                        pgr_freq.progress = progress.toFloat()
                        tv_freq_max.text = "" + getMaxCpuFreq()?.toLong()?.div(1000)

                    }
                    "stop" -> {
                        time_text.text = "完成"
                        stop()
                    }

                }
            })


        LiveDataBus.get().with("CPUKtTestFragment").postValue("ReaderThread:0")


    }

    fun start() {

        mHandler.postDelayed({
            val cores = ArmFreqUtils.getNumberOfCPUCores()
            mCpuRate = 10000

            for (i in 0 until cores) {
                Thread {
                    var dec: Int
                    while (isRunning) {
                        dec = mDelayCnt * 500
                        while (dec > 0) {
                            dec--
                        }
                        try {
                            Log.d(TAG, "start, CPU cores:")
                            Thread.sleep(1)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }.start()


            }
        }, 1000)

        if (mCpuReaderThread == null) {
            mCpuReaderThread = ReaderThread()
            mCpuReaderThread!!.start()
        }
    }

    fun stop() {
        isRunning = false

        if (mCpuReaderThread != null) {
            mCpuReaderThread!!.cancel()
            try {
                mCpuReaderThread!!.join()
            } catch (ignored: InterruptedException) {
            }
            mCpuReaderThread = null
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        stop()
        if (TIME != null) {
            LiveDataBus.get().with("AgeingKtActivity").postValue("CPUKtTestFragment:$TIME")
        }
    }

    fun setGOms(thie: Long) {
        countDownTimer = object : CountDownTimer(thie, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                Log.e("countDownTimer", millisUntilFinished.toString())
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
                TIME = millisUntilFinished
                time_text.text = "$hour:$minute:$second /h"

                GlobalScope.launch {
                    ado?.update(
                        Ageing(
                            0, "CPUKtTestFragment",
                            AgeingDataBase.getInstance(context).ageingDao.getAgeingID(0).AgeingStarTime,
                            stTime,
                            "2", millisUntilFinished.toString()
                        )
                    )
                }

            }

            /**
             * 倒计时结束后调用的
             */
            override fun onFinish() {
                stop()
                time_text.text = "0:0:0 /h"
                LiveDataBus.get().with("AgeingKtActivity").postValue("CPUKtTestFragment:0")
                LiveDataBus.get().with("CPUKtTestFragment").postValue("stop")
            }
        }
        countDownTimer?.start()

    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }


    private inner class ReaderThread : Thread() {
        private var totalCpuReader: BufferedReader? = null
        private var myPidCpuReader: BufferedReader? = null
        private var totalJiffies: Long = 0
        private var totalJiffiesBefore: Long = 0
        private var jiffies: Long = 0
        private var jiffiesBefore: Long = 0
        private var jiffiesMyPid: Long = 0
        private var jiffiesMyPidBefore: Long = 0
        override fun run() {
            while (true) {
                if (!currentThread().isInterrupted) {
                    openCpuReaders()
                    read()
                    closeCpuReaders()
                    try {
                        currentThread()
                        sleep(1000)
                        continue
                    } catch (var2: InterruptedException) {
                    }
                }
                return
            }
        }

        fun cancel() {
            interrupt()
        }


        private fun openCpuReaders() {
            if (totalCpuReader == null) {
                try {
                    totalCpuReader = BufferedReader(FileReader("/proc/stat"))
                } catch (var3: FileNotFoundException) {
                    Log.w(TAG, "Could not open '/proc/stat' - " + var3.message)
                }
            }
            if (myPidCpuReader == null) {
                try {
                    myPidCpuReader = BufferedReader(
                        FileReader(
                            "/proc/"
                                    + Process.myPid() + "/stat"
                        )
                    )
                } catch (var2: FileNotFoundException) {
                    Log.w(
                        TAG, "Could not open '/proc/" + Process.myPid() + "/stat' - "
                                + var2.message
                    )
                }
            }
        }

        private fun read() {
            var cpuData: Array<String>
            if (totalCpuReader != null) {
                try {
                    cpuData = totalCpuReader!!.readLine().split("[ ]+".toRegex(), 9).toTypedArray()
                    jiffies = cpuData[1].toLong() + cpuData[2].toLong() + cpuData[3].toLong()
                    totalJiffies =
                        jiffies + cpuData[4].toLong() + cpuData[6].toLong() + cpuData[7].toLong()
                } catch (var8: IOException) {
                    Log.w(
                        "CpuUsageDataModule", "Failed reading total cpu data - "
                                + var8.message
                    )
                }
            }
            if (myPidCpuReader != null) {
                try {
                    cpuData = myPidCpuReader!!.readLine().split("[ ]+".toRegex(), 18).toTypedArray()
                    jiffiesMyPid =
                        cpuData[13].toLong() + cpuData[14].toLong() + cpuData[15].toLong() + cpuData[16].toLong()
                } catch (var7: IOException) {
                    Log.w(
                        "CpuUsageDataModule", "Failed reading my pid cpu data - "
                                + var7.message
                    )
                }
            }
            if (totalJiffiesBefore > 0L) {
                val totalDiff = totalJiffies - totalJiffiesBefore
                val jiffiesDiff = jiffies - jiffiesBefore
                val jiffiesMyPidDiff = jiffiesMyPid - jiffiesMyPidBefore
                mCurPercent = getPercentInRange(
                    (100.0f * jiffiesDiff.toFloat()
                            / totalDiff.toFloat()).toDouble()
                ).toInt()
                mCurMyPidPercent = getPercentInRange(
                    (100.0f * jiffiesMyPidDiff.toFloat()
                            / totalDiff.toFloat()).toDouble()
                ).toInt()
                LiveDataBus.get().with("CPUKtTestFragment").postValue("ReaderThread:0")
                Log.e(
                    TAG,
                    "mCurPercent-->" + mCurPercent + "mCurMyPidPercent-->" + mCurMyPidPercent
                )

            }
            totalJiffiesBefore = totalJiffies
            jiffiesBefore = jiffies
            jiffiesMyPidBefore = jiffiesMyPid
        }

        private fun closeCpuReaders() {
            try {
                if (totalCpuReader != null) {
                    totalCpuReader!!.close()
                    totalCpuReader = null
                }
                if (myPidCpuReader != null) {
                    myPidCpuReader!!.close()
                    myPidCpuReader = null
                }
            } catch (ignored: IOException) {
            }
        }
    }

    // 实时获取CPU当前频率（单位KHZ）
    fun getCurCpuFreq(): String? {
        var result = "N/A"
        try {
            val fr = FileReader(
                "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
            )
            val br = BufferedReader(fr)
            val text = br.readLine()
            result = text.trim { it <= ' ' }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun getMaxCpuFreq(): String? {
        var result = ""
        val cmd: ProcessBuilder
        try {
            val args = arrayOf(
                "/system/bin/cat",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            )
            cmd = ProcessBuilder(*args)
            val process: java.lang.Process? = cmd.start()
            val `in`: InputStream? = process?.getInputStream()
            val re = ByteArray(24)
            while (`in`?.read(re) !== -1) {
                result = result + String(re)
            }
            `in`.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            result = "N/A"
        }
        return result.trim { it <= ' ' }
    }

}