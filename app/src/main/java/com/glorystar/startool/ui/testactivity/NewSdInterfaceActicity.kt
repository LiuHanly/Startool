package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.lzyzsd.circleprogress.DonutProgress
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.bean.GridItem
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder
import com.glorystar.startool.utils.tool.CompuUtils
import com.glorystar.startool.utils.tool.LiveDataBus
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigDecimal

class NewSdInterfaceActicity : BaseActivity() {

    var mstoragemanager: StorageManager? = null
    lateinit var listGridItem: MutableList<GridItem>
    var baserecycleradapter: BaseRecyclerAdapter<GridItem>? = null
    lateinit var through_button: Button
    lateinit var rct_progr: RecyclerView


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewSdInterfaceActicity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var Itemstatus = listGridItem.distinctBy { it.label }
            baserecycleradapter?.setNewData(Itemstatus)
            if(msg.what == 0){
                Log.e("volumeList","=Itemstatus==>${Itemstatus.size}")

            }

        }
    }


    override fun getLayoutId(): Int = R.layout.activity_new_sd_interface_acticity

    override fun initView(savedInstanceState: Bundle?) {
        listGridItem = arrayListOf();
        through_button = findViewById(R.id.through_button)
        rct_progr = findViewById(R.id.rct_progr)
        mstoragemanager = getSystemService(STORAGE_SERVICE) as StorageManager

        baserecycleradapter = object : BaseRecyclerAdapter<GridItem>(this, null, R.layout.mydonut_progress) {
                @SuppressLint("SetTextI18n")
                override fun convert(
                    holder: BaseRecyclerHolder?,
                    data: GridItem?,
                    position: Int,
                    isScrolling: Boolean,
                    selectedPosition: Int
                ) {
                    val textView = holder?.getView<TextView>(R.id.textView)
                    val imageview = holder?.getView<ImageView>(R.id.imageview)
                    val textMina = holder?.getView<TextView>(R.id.textMina)
                    val pgr_percent = holder?.getView<DonutProgress>(R.id.pgr_percent)
                    textView?.text = data?.label
                    val FreeBlocks = data?.FreeBlocks?.div(1024.0)?.div(100)
                    val totalBytes = data?.totalBytes?.div(1024)?.div(1024)?.div(1024.0)

                    val eunFreeBlocks = FreeBlocks?.let { BigDecimal(it) }
                    val euntotalBytes = totalBytes?.let { BigDecimal(it) }
                    val sunFreeBlocks = eunFreeBlocks?.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                    val suntotalBytes = euntotalBytes?.setScale(2, BigDecimal.ROUND_HALF_DOWN)

                    Log.e(
                        "BaseRecyclerAdapter",
                        "FreeBlocks---->" + data?.FreeBlocks + "totalBytes--->" + data?.totalBytes
                    )
                    pgr_percent?.progress =
                        CompuUtils.div(FreeBlocks?.toDouble()!!, totalBytes?.toDouble()!!, 1)
                            .times(100).toFloat()
                    textMina?.text = "" + sunFreeBlocks + "G" + "/" + suntotalBytes + "G"

                }

            }
        rct_progr.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rct_progr.adapter = baserecycleradapter
        setLinst()
        setLinstusb()
        through_button.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@NewSdInterfaceActicity, 8, 2, 1)

        }

        LiveDataBus.get().with("USBInterfaceActivity", String::class.java).observe(this, Observer {
            Log.e("NewSdInterfaceActicity","setLinst-->${it}")
            listGridItem.clear()
            setLinst()
            setLinstusb()
        })

    }
    @SuppressLint("NewApi")
    fun setLinst() {


        if (Build.VERSION.SDK_INT < 23) {

            try {
                val files: Array<File> = File("/mnt/external_sd").listFiles()
                val statFs = StatFs("/mnt/external_sd")
                val FreeBlocks = statFs.freeBlocksLong
                val totalBytes = statFs.totalBytes
                listGridItem.add(
                    GridItem(
                        "SD",
                        "status",
                        true,
                        true,
                        FreeBlocks,
                        totalBytes
                    )
                )
                handler.sendEmptyMessage(0)
            } catch (e: Exception) {
           //     listGridItem.clear()
            }

        } else {
            val volumeList: List<StorageVolume> =
                mstoragemanager?.getStorageVolumes() as List<StorageVolume>
            volumeList.forEach {
                val label: String = it.getDescription(this) //这个其实就是U盘的名称
                val status: String = it.getState()
                val isEmulated = it.isEmulated()            //是否是内部存储设备
                val isRemovable = it.isRemovable()
               //是否是可移除的外部存储设备
                var mPath = ""                                    //设备的路径
                try {
                    val myclass = Class.forName("android.os.storage.StorageVolume")
                    val getPath: Method = myclass.getMethod("getPath")
                    getPath.isAccessible = true
                    mPath = getPath.invoke(it).toString()

                    val statFs = StatFs(mPath)
                    val FreeBlocks = statFs.freeBlocksLong
                    val totalBytes = statFs.totalBytes

                    if (!isEmulated) {

                        if (label.contains("SD")) {
                            listGridItem.add(
                                GridItem(
                                    label,
                                    status,
                                    isEmulated,
                                    isRemovable,
                                    FreeBlocks,
                                    totalBytes
                                )
                            )

                            Log.e("volumeList","statFs----${listGridItem.size}------>")
                        }
                        handler.sendEmptyMessage(0)
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace();
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace();
                } catch (e: InvocationTargetException) {
                    e.printStackTrace();
                } catch (e: IllegalAccessException) {
                    e.printStackTrace();
                }


            }
        }


    }


    @SuppressLint("NewApi", "LogNotTimber")
    fun setLinstusb() {

        if (Build.VERSION.SDK_INT < 23) {
            try {
                val files: Array<File> = File("/mnt/usb_storage").listFiles()
                if (files.isNotEmpty()) {
                    if (files[0].name.contains("USB_DISK")) {
                        files.forEach {
                            it.listFiles().forEach {
                                val statFs = StatFs(it.path)
                                val FreeBlocks = statFs.freeBlocksLong
                                val totalBytes = statFs.totalBytes
                                listGridItem.add(
                                    GridItem(
                                        "USB",
                                        "status",
                                        true,
                                        true,
                                        FreeBlocks,
                                        totalBytes
                                    )
                                )
                            }
                        }
                    } else {
                        val statFs = StatFs("/mnt/usb_storage")
                        val FreeBlocks = statFs.freeBlocksLong
                        val totalBytes = statFs.totalBytes
                        listGridItem.add(
                            GridItem(
                                "USB",
                                "status",
                                true,
                                true,
                                FreeBlocks,
                                totalBytes
                            )
                        )
                    }
                }
                handler.sendEmptyMessage(1)
            } catch (e: Exception) {
             //   listGridItem.clear()
            }

        } else {
            val volumeList: List<StorageVolume> =
                mstoragemanager?.getStorageVolumes() as List<StorageVolume>
            volumeList.forEach {
                val label: String = it.getDescription(this) //这个其实就是U盘的名称
                val status: String = it.getState()
                val isEmulated = it.isEmulated()            //是否是内部存储设备
                val isRemovable = it.isRemovable()          //是否是可移除的外部存储设备
                var mPath = ""                                    //设备的路径
                try {
                    val myclass = Class.forName("android.os.storage.StorageVolume")
                    val getPath: Method = myclass.getMethod("getPath")
                    getPath.isAccessible = true
                    mPath = getPath.invoke(it).toString()

                    val statFs = StatFs(mPath)
                    val FreeBlocks = statFs.freeBlocksLong
                    val totalBytes = statFs.totalBytes
                    if (!isEmulated) {
                        if (!label.contains("SD")) {
                            listGridItem.add(
                                GridItem(
                                    label,
                                    status,
                                    isEmulated,
                                    isRemovable,
                                    FreeBlocks,
                                    totalBytes
                                )
                            )
                        }
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace();
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace();
                } catch (e: InvocationTargetException) {
                    e.printStackTrace();
                } catch (e: IllegalAccessException) {
                    e.printStackTrace();
                }


            }
            handler.sendEmptyMessage(1)
        }


    }



}