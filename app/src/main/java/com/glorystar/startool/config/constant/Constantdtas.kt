package com.glorystar.startool.config.constant

import android.app.Activity
import android.content.res.AssetManager
import android.os.Build
import android.os.Environment
import android.util.Log
import com.glorystar.startool.R
import com.glorystar.startool.config.base.MyApplication
import com.glorystar.startool.config.bean.AgingBean
import com.glorystar.startool.ui.activity.*
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.ToastUtil
import java.io.*

object Constantdtas {
    const val STAROS_SETTING: String = "systemtest"
    const val WIFI_SSID = "GSCNAPK"
    const val WIFI_PASS = "12062106"

    const val CPUKTTESTFRAGMENT = "180000" //CPU老化时间  "600000"
    const val MEMORYTESTKTFRAGMENT = "600000"//低内存测试时间 "600000"
    const val DDRTESTKTFRAGMENT = "600000"    //DDR测试时间   "600000"
    const val VIDEOTESTKTFRAGMENT = "28800000" //视屏测试     "28800000"


    fun getGreAgingDate(): MutableList<AgingBean> {
        var minItemBean: MutableList<AgingBean> = ArrayList<AgingBean>()

        MyApplication.context?.resources?.getString(R.string.wifi_name)
            ?.let { AgingBean(0, R.mipmap.wifixxxhdpi, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.network_name)
            ?.let { AgingBean(0, R.mipmap.internet, it, false) }?.let { minItemBean.add(it) }


        MyApplication.context?.resources?.getString(R.string.bluetooth_name)
            ?.let { AgingBean(0, R.mipmap.bluetooth, it, false) }?.let { minItemBean.add(it) }


        MyApplication.context?.resources?.getString(R.string.hdmi_name)
            ?.let { AgingBean(0, R.mipmap.hdmi, it, false) }?.let { minItemBean.add(it) }


        MyApplication.context?.resources?.getString(R.string.usb_name)
            ?.let { AgingBean(0, R.mipmap.usb, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.sdport_name)
            ?.let { AgingBean(0, R.mipmap.sdcard, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.screen_display_name)
            ?.let { AgingBean(0, R.mipmap.screendisplay, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.touch_name)
            ?.let { AgingBean(0, R.mipmap.touch, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.speaker_name)
            ?.let { AgingBean(0, R.mipmap.speaker, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.gyroscope_name)
            ?.let { AgingBean(0, R.mipmap.orientation, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.buttons_name)
            ?.let { AgingBean(0, R.mipmap.button, it, false) }?.let { minItemBean.add(it) }

        MyApplication.context?.resources?.getString(R.string.photo_recording_name)
            ?.let { AgingBean(0, R.mipmap.camera, it, false) }?.let { minItemBean.add(it) }

        return minItemBean
    }




    fun getInString(context: Activity): List<String> {

        val mAssetManger: AssetManager = context.assets
        val bufferedReader1 =
            BufferedReader(InputStreamReader(mAssetManger.open("liucheng.txt"), "utf-8"))
        val lines1 = bufferedReader1.readLines()
        return  lines1
    }

    fun geiActivity(context: Activity, position: String) {
        when (position) {
            "屏幕显示" -> ScreenShowActivity.toStartActivity(context)
            "触摸" -> ScreenActivity.toStartActivity(context)
            "无线网络" -> WifiActivity.toStartActivity(context)
            "蓝牙" -> BluetoothActivity.toStartActivity(context)
            "相机微米" -> CameraActivity.toStartActivity(context)
            "网络" -> RJ45Activity.toStartActivity(context)
            "SD卡" -> SdInterfaceActicity.toStartActivity(context)
            "USB" -> USBInterfaceActivity.toStartActivity(context)
            "HDMI" -> HDMIActivity.toStartActivity(context)
            "扬声器" -> TrumpetActivity.toStartActivity(context)
            "陀螺仪" -> GyroscopeActivity.toStartActivity(context)
            "按钮" -> ButtonActivity.toStartActivity(context)
        }
    }



    /**
     *检测是否插入网线
     * */
    fun checkLan(): Boolean {
        var p: Process? = null
        var `in`: BufferedReader? = null
        try {
            p = Runtime.getRuntime().exec("cat /sys/class/net/eth0/carrier")
            `in` = BufferedReader(InputStreamReader(p.inputStream))
            var line: String? = null
            val value = ""
            while (`in`.readLine().also { line = it } != null) {
                Log.i("tag", "carrier is $line")
                if ("0" == line) {
                    return false
                }
                if ("1" == line) {
                    return true
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("tag", e.message!!)
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }


    /**
     * 写入内容到SD卡中的txt文本中
     * str为内容
     */
    fun writeSDFile(str: String?) {
        try {
            val testname = MyApplication.Testname
            //+ DateUtil.stampToTime(DateUtil.timeStamp.toString())
            val name =
                "${testname + Build.SERIAL + DateUtil.stampToTies(DateUtil.timeStamp.toString())}.txt"
            val path = Environment.getExternalStorageDirectory().toString() + "/log"
            val file1 = File(path)
            if (!file1.exists()) {
                file1.mkdirs()
            }
            val file = File(path, name)
            if (file.exists()) {
                file.delete()

            }

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                try {

                    str?.let {
                        Log.e("writeSDFile", "写入失败-->$it")
                        file.writeText(it)

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.e("writeSDFile", "写入失败-->$e")
            }
            ToastUtil.showMsg(
                "日志已保存至： ${
                    Environment.getExternalStorageDirectory().toString()
                }/log 文件夹下"
            )
        } catch (e: Exception) {
            Log.e("writeSDFile", "写入失败-->$e")
        }


    }

    fun setTime(millisUntilFinished: Long): String {
        val day: Long = millisUntilFinished / (1000 * 24 * 60 * 60) //单位天
        val hour: Long = (millisUntilFinished - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60)
        //单位时
        val minute: Long =
            (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60)
        //单位分
        val second: Long =
            (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000
        //单位秒
        return ("$hour：$minute：$second")
    }


}