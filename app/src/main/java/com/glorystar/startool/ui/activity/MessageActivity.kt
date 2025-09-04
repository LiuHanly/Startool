package com.glorystar.startool.ui.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.config.constant.StudentDataBase
import com.glorystar.startool.utils.db.Student
import com.glorystar.startool.utils.tool.NetworkUtil.getMacAddressTextView
import java.math.BigDecimal

class MessageActivity : BaseActivity() {


    var Serial_number: TextView? = null
    var Model: TextView? = null
    var Android_version: TextView? = null
    var Build_number: TextView? = null
    var IP_address: TextView? = null
    var Inter_shared_storage: TextView? = null
    var Inter_storage: TextView? = null

    lateinit var failure_bu: Button

    lateinit var through_bu: Button

    private var mAm: ActivityManager? = null
    var initData = 2

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, MessageActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int = R.layout.activity_message

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)

        mAm = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val systemMemInfo = ActivityManager.MemoryInfo()
        mAm!!.getMemoryInfo(systemMemInfo)

        val mTotalMemory = systemMemInfo.totalMem / 1024 / 1024 / 1000.00

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalCounts = statFs.blockCountLong //总共的block数
        val size = statFs.blockSizeLong //每格所占的大小，一般是4KB==
        val totalROMSize = totalCounts * size //内部存储总大小
        val euntotalBytes = totalROMSize.div(1024).div(1024).div(1024.00).let { BigDecimal(it) }
        val suntotalBytes = euntotalBytes.setScale(2, BigDecimal.ROUND_HALF_DOWN)


        Serial_number = findViewById(R.id.Serial_number)
        Model = findViewById(R.id.Model)
        Android_version = findViewById(R.id.Android_version)
        Build_number = findViewById(R.id.Build_number)
        IP_address = findViewById(R.id.IP_address)
        Inter_shared_storage = findViewById(R.id.Inter_shared_storage)
        Inter_storage = findViewById(R.id.Inter_storage)
        Inter_storage?.text = "$suntotalBytes G"
        Serial_number?.setTextColor(Color.GREEN)


        try {
            val Serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
            if (Serial.contains("GS", true) && Serial.toCharArray().size == 14) {
                Serial.toCharArray().forEach {
                    if (!it.isLetterOrDigit()) {
                        Serial_number?.setTextColor(Color.RED)
                        initData = 1
                    }
                }

            } else {
                Serial_number?.setTextColor(Color.RED)
                initData = 1
            }
            Serial_number?.text = Serial
        } catch (e: Exception) {

        }

        Inter_shared_storage?.text = mTotalMemory.toString() + "G"



        Model?.text = Build.MODEL
        Android_version?.text = Build.VERSION.RELEASE

        Build_number?.text = Build.DISPLAY
        try {
            getMacAddressTextView(IP_address)
        } catch (e: Exception) {
        }
        failure_bu?.setOnClickListener {
            IntenAcitivity.getIntenAcitivity(this@MessageActivity, 0, 1)

        }
        through_bu?.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@MessageActivity, 0, initData)

        }

    }


}