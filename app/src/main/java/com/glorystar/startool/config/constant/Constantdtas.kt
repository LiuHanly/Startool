package com.glorystar.startool.config.constant

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.glorystar.startool.R
import com.glorystar.startool.config.base.MyApplication
import com.glorystar.startool.config.bean.AgingBean
import com.glorystar.startool.config.bean.liuchenData
import com.glorystar.startool.ui.activity.BluetoothActivity
import com.glorystar.startool.ui.activity.ButtonActivity
import com.glorystar.startool.ui.activity.CameraActivity
import com.glorystar.startool.ui.activity.GyroscopeActivity
import com.glorystar.startool.ui.activity.HDMIActivity
import com.glorystar.startool.ui.activity.RJ45Activity
import com.glorystar.startool.ui.activity.ScreenActivity
import com.glorystar.startool.ui.activity.ScreenShowActivity
import com.glorystar.startool.ui.activity.SdInterfaceActicity
import com.glorystar.startool.ui.activity.TrumpetActivity
import com.glorystar.startool.ui.activity.USBInterfaceActivity
import com.glorystar.startool.ui.activity.WifiActivity
import com.glorystar.startool.ui.testactivity.NewBluetoothActivity
import com.glorystar.startool.ui.testactivity.NewButtonActivity
import com.glorystar.startool.ui.testactivity.NewCameraActivity
import com.glorystar.startool.ui.testactivity.NewGpIOActivity
import com.glorystar.startool.ui.testactivity.NewGyroscopeActivity
import com.glorystar.startool.ui.testactivity.NewHDMIActivity
import com.glorystar.startool.ui.testactivity.NewRJ45Activity
import com.glorystar.startool.ui.testactivity.NewScreenActivity
import com.glorystar.startool.ui.testactivity.NewSdInterfaceActicity
import com.glorystar.startool.ui.testactivity.NewShowActivity
import com.glorystar.startool.ui.testactivity.NewWifiActivity
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.ToastUtil
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects


object Constantdtas {
    const val UPDATE_APP = "http://18.221.204.147/app_storage/demoApkInfo.json";
    const val STAROS_SETTING: String = "systemtest"
    const val WIFI_SSID = "GSCNAPK"
    const val WIFI_PASS = "12062106"

    const val CPUKTTESTFRAGMENT = "180000" //CPU老化时间    180000
    const val MEMORYTESTKTFRAGMENT = "600000"//低内存测试时间 "600000"
    const val DDRTESTKTFRAGMENT = "600000"    //DDR测试时间   "600000"
    const val VIDEOTESTKTFRAGMENT = "28800000" //视屏测试     "28800000"
    val AttaServedName = arrayOf("ENG", "中文")
    const val STAROS_ORIENTATION = "STARTOOL_CONFIG"
    val ORIENTATION = arrayOf("Landscape", "Portrait")
    val BARCODE = arrayOf("1D barcodes", "2D barcodes")
    val RFIDSUPPORT = arrayOf("Standard", "Version P")

    fun getGreAgingDate(context: Activity): MutableList<AgingBean> {
        var minItemBean: MutableList<AgingBean> = ArrayList<AgingBean>()

        context?.resources?.getString(R.string.wifi_name)
            ?.let { AgingBean(3, R.mipmap.wifixxxhdpi, it, false) }?.let { minItemBean.add(it) }


        context?.resources?.getString(R.string.network_name)
            ?.let { AgingBean(6, R.mipmap.internet, it, false) }?.let { minItemBean.add(it) }


        context?.resources?.getString(R.string.bluetooth_name)
            ?.let { AgingBean(4, R.mipmap.bluetooth, it, false) }?.let { minItemBean.add(it) }


        context?.resources?.getString(R.string.hdmi_name)
            ?.let { AgingBean(9, R.mipmap.hdmi, it, false) }?.let { minItemBean.add(it) }


        context?.resources?.getString(R.string.usb_name)
            ?.let { AgingBean(8, R.mipmap.usb, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.sdport_name)
            ?.let { AgingBean(7, R.mipmap.sdcard, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.screen_display_name)
            ?.let { AgingBean(1, R.mipmap.screendisplay, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.touch_name)
            ?.let { AgingBean(2, R.mipmap.touch, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.speaker_name)
            ?.let { AgingBean(10, R.mipmap.speaker, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.gyroscope_name)
            ?.let { AgingBean(12, R.mipmap.orientation, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.buttons_name)
            ?.let { AgingBean(13, R.mipmap.button, it, false) }?.let { minItemBean.add(it) }

        context?.resources?.getString(R.string.photo_recording_name)
            ?.let { AgingBean(5, R.mipmap.camera, it, false) }?.let { minItemBean.add(it) }

        return minItemBean
    }


    fun getInString(context: Activity): List<String> {

        val mAssetManger: AssetManager = context.assets
        val bufferedReader1 =
            BufferedReader(InputStreamReader(mAssetManger.open("liucheng.txt"), "utf-8"))
        val lines1 = bufferedReader1.readLines()
        return lines1
    }

    fun getInString(context: Activity, code: Int): List<String> {

        val mAssetManger: AssetManager = context.assets
        val bufferedReader1 =
            BufferedReader(InputStreamReader(mAssetManger.open("newliucheng.txt"), "utf-8"))
        val lines1 = bufferedReader1.readLines()
        return lines1
    }

    fun geiActivity(context: Activity, position: Int) {
        Log.e("geiActivity", "---->$position ----->${context.componentName}")
        when (position) {
            1 -> ScreenShowActivity.toStartActivity(context)
            2 -> ScreenActivity.toStartActivity(context)
            3 -> WifiActivity.toStartActivity(context)
            4 -> BluetoothActivity.toStartActivity(context)
            5 -> CameraActivity.toStartActivity(context)
            6 -> RJ45Activity.toStartActivity(context)
            7 -> SdInterfaceActicity.toStartActivity(context)
            8 -> USBInterfaceActivity.toStartActivity(context)
            9 -> HDMIActivity.toStartActivity(context)
            10 -> TrumpetActivity.toStartActivity(context)
            12 -> GyroscopeActivity.toStartActivity(context)
            13 -> ButtonActivity.toStartActivity(context)
        }
    }

    fun geiActivity(context: Activity, position: Int, code: Int) {
        Log.e("geiActivity", "---->$position --->$code -->${context.componentName}")

        when (position) {
            1 -> NewShowActivity.toStartActivity(context)
            2 -> NewScreenActivity.toStartActivity(context)
            3 -> NewWifiActivity.toStartActivity(context)
            4 -> NewBluetoothActivity.toStartActivity(context)
            5 -> NewCameraActivity.toStartActivity(context)
            6 -> NewRJ45Activity.toStartActivity(context)
            7 -> NewSdInterfaceActicity.toStartActivity(context)
            9 -> NewHDMIActivity.toStartActivity(context)
            10 -> NewGyroscopeActivity.toStartActivity(context)
            12 -> NewGpIOActivity.toStartActivity(context)
            13 -> NewButtonActivity.toStartActivity(context)


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
    fun writeSDFile(str: String?, fileName: String) {
        println("writeSDFile--->")
        try {
            val testname = MyApplication.Testname
            val Serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }

            //+ DateUtil.stampToTime(DateUtil.timeStamp.toString())
            val name =
                "${fileName}_${testname + Serial + DateUtil.stampToTies(DateUtil.timeStamp.toString())}.txt"
            val path = MyApplication.context?.getExternalFilesDir("log_${Serial}").toString()
            //  val path = "${MyApplication.context?.filesDir}/log_${Build.SERIAL}"
            println("writeSDFile--->${path}")
            // val path = Environment.getExternalStorageDirectory().toString() + "/log"
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
                        Log.e("writeSDFile", "写入-->$it")
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
                    path
                } 文件夹下"
            )
        } catch (e: Exception) {

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

    // 获取最新的图片
    fun getLatestImage(context: Context): String? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"
        val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
        Log.e("latestImagePath", "${cursor}")
        if (cursor != null && cursor.moveToFirst()) {
            val latestImagePath =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

            cursor.close()
            return latestImagePath
        }
        return null
    }

    // 获取最新的视频
    fun getLatestVideo(context: Context): String? {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC"
        val cursor = context.contentResolver.query(
            uri, arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,    // 视频文件路径（内部存储路径）
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN
            ), null, null, sortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val latestVideoPath = cursor.getString(columnIndex)
            /*     cursor.columnNames
                 val latestVideoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))*/

            Log.e("getLatestVideo", "${latestVideoPath}")
            cursor.close()
            return latestVideoPath
        }
        return null
    }


    fun getVideosInfo(context: Context): String? {
        val contentResolver: ContentResolver = context.getContentResolver()
        val videoColumns = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )
        //      两种方法均可
//		Cursor cursor=
//	    this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoColumns,
            null,
            null,
            null
        )
        var videoCursor: MutableList<liuchenData> = arrayListOf()
        if (cursor != null) {
            //   val video = arrayListOf<Map(String,String>()

            while (cursor.moveToNext()) {
                val _id: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val filePath: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                val title: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                val mime_type: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))

                val mime_Time: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                var updatebean = liuchenData(mime_Time.toInt(), filePath)
                videoCursor.add(updatebean)

            }
            videoCursor.sortedByDescending { it.id }
        }
        return if (videoCursor.size > 0) videoCursor[videoCursor.size - 1].name else null
    }

}