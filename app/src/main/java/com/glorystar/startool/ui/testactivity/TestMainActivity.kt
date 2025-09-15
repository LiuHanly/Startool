package com.glorystar.startool.ui.testactivity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.config.constant.IntenAcitivity.PeriodicSync
import com.glorystar.startool.utils.tool.NetworkUtil
import com.glorystar.startool.utils.tool.NetworkUtil.getMacAddressTextView
import com.glorystar.startool.utils.tool.ToastUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal

class TestMainActivity : BaseActivity() {

    var Serial_number: TextView? = null
    var Model: TextView? = null
    var Android_version: TextView? = null
    var Build_number: TextView? = null
    var IP_address: TextView? = null
    var Inter_shared_storage: TextView? = null
    var Inter_storage: TextView? = null

    private var mAm: ActivityManager? = null
    var initData = 2
    lateinit var through_bu: Button

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, TestMainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }

    }

    //  activity_test_main
    override fun getLayoutId(): Int = R.layout.activity_test_main
    override fun initView(savedInstanceState: Bundle?) {
    //    ToastUtil.showMsg("Enter the factory mode.")
        val mAssetManger: AssetManager = assets
        val bufferedReader1 =
            BufferedReader(InputStreamReader(mAssetManger.open("liucheng.txt"), "utf-8"))
        val lines1 = bufferedReader1.readLines()
        PeriodicSync(lines1, this@TestMainActivity)

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
        }catch (e:Exception){

        }

        Inter_shared_storage?.text = mTotalMemory.toString() + "G"



        Model?.text = Build.MODEL
        Android_version?.text = Build.VERSION.RELEASE

        Build_number?.text = Build.DISPLAY
        try {
            getMacAddressTextView(
                IP_address, when (NetworkUtil.getNetWorkTypeInt(this)) {
                    1 -> NetworkUtil.getEthernetMacAddress()
                    2 -> NetworkUtil.getWifiMacAddress()
                    3 -> NetworkUtil.get4GMacAddress()
                    else -> "02:00:00:00:00:00"
                }
            )
        } catch (e: Exception) {
        }
        through_bu.setOnClickListener {


            //  val intent = Intent(Intent.ACTION_MASTER_CLEAR)
         //   performFactoryReset(this)
            // Log.e("TestMainActivity","TestMainActivity-------------->")
          IntenAcitivity.getIntenAcitivity(this@TestMainActivity, 0, initData,0)
        }

    }

}