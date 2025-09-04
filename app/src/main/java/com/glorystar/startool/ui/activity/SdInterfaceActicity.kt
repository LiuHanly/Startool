package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
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

/**
 * SD卡测试
 * */
class SdInterfaceActicity : BaseActivity() {
    var mstoragemanager: StorageManager? = null
    lateinit var listGridItem: MutableList<GridItem>
    var baserecycleradapter: BaseRecyclerAdapter<GridItem>? = null
    lateinit var rct_progr: RecyclerView
    lateinit var failure_button: Button
    lateinit var through_button: Button


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, SdInterfaceActicity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.acticity_sd_interface
    }

    override fun initView(savedInstanceState: Bundle?) {
        rct_progr = findViewById(R.id.rct_progr)
        failure_button = findViewById(R.id.failure_button)
        through_button = findViewById(R.id.through_button)

        mstoragemanager = getSystemService(STORAGE_SERVICE) as StorageManager
        baserecycleradapter =
            object : BaseRecyclerAdapter<GridItem>(this, null, R.layout.mydonut_progress) {
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
                    val FreeBlocks = data?.FreeBlocks?.div(1024.0)?.div(1000)
                    val totalBytes = data?.totalBytes?.div(1024)?.div(1024)?.div(1024.00)

                    val eunFreeBlocks = FreeBlocks?.let { BigDecimal(it) }
                    val euntotalBytes = totalBytes?.let { BigDecimal(it) }
                    val sunFreeBlocks = eunFreeBlocks?.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                    val suntotalBytes = euntotalBytes?.setScale(2, BigDecimal.ROUND_HALF_DOWN)

                    try {
                        pgr_percent?.progress =
                            CompuUtils.div(FreeBlocks?.toDouble()!!, totalBytes?.toDouble()!!, 1)
                                .times(100).toFloat()
                    } catch (e: Exception) {

                    }
                    textMina?.text = "" + sunFreeBlocks + "G" + "/" + suntotalBytes + "G"
                }
            }
        rct_progr.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rct_progr.adapter = baserecycleradapter
        setLinst()

        failure_button.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@SdInterfaceActicity, 7, 1)

        }
        through_button.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@SdInterfaceActicity, 7, 2)

        }


        LiveDataBus.get().with("USBInterfaceActivity", String::class.java).observe(this, Observer {
            setLinst()
        })
    }

    @SuppressLint("NewApi")
    fun setLinst() {
        listGridItem = arrayListOf();

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

            } catch (e: Exception) {
                listGridItem.clear()
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
        }

        baserecycleradapter?.setNewData(listGridItem)
    }


}