package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.wandersnail.commons.util.ToastUtils
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.bean.Device
import com.glorystar.startool.config.bluetooth.BTManager
import com.glorystar.startool.config.bluetooth.DiscoveryListener
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


/**
 * 蓝牙测试
 * */
class BluetoothActivity : BaseActivity() {

    var baserecycleradapter: BaseRecyclerAdapter<Device>? = null
    private val devList: MutableList<Device> = arrayListOf()
    private var mBluetoothAdapter: BluetoothAdapter? = null

    lateinit var RecyclerLayout: RecyclerView
    lateinit var failure_bu: Button
    lateinit var through_bu: Button


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, BluetoothActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_bluetooth
    }

    @SuppressLint("NewApi")
    override fun initView(savedInstanceState: Bundle?) {
        RecyclerLayout = findViewById(R.id.RecyclerLayout)
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)

        BTManager.isDebugMode = true
        BTManager.getInstance().addDiscoveryListener(discoveryListener)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        baserecycleradapter =
            object : BaseRecyclerAdapter<Device>(this, devList, R.layout.device_item) {
                override fun convert(
                    holder: BaseRecyclerHolder?,
                    data: Device?,
                    position: Int,
                    isScrolling: Boolean,
                    selectedPosition: Int
                ) {
                    val mIconIv = holder?.getView<ImageView>(R.id.iv_icon)
                    mIconIv?.setImageResource(R.mipmap.bluetoothxxxhdpi)
                    val mMainTv = holder?.getView<TextView>(R.id.tv_main)
                    val name = data?.device?.name
                    val address = data?.device?.address
                    if (name == null) {
                        mMainTv?.text = address
                    } else {
                        mMainTv?.text = name
                    }

                }

            }
        RecyclerLayout.adapter = baserecycleradapter

        failure_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@BluetoothActivity, 4, 1)

        }
        through_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@BluetoothActivity, 4, 2)

        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("Main", "onResume")
        if (BTManager.getInstance().isInitialized) {
            if (BTManager.getInstance().isBluetoothOn) {
                doStartDiscovery()
            } else {

//                if (Build.VERSION.RELEASE.contains("7")) {
//                    mBluetoothAdapter?.enable()
//                    doStartDiscovery()
//                } else {

                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 1)
                //    }
            }
        }
    }   


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        print("data-->${data.toString()}")
        when (resultCode) {
            1 -> {
                print("data-->${data.toString()}")
            }
        }

    }

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onDiscoveryStart() {

            invalidateOptionsMenu()
        }

        override fun onDiscoveryStop() {

            invalidateOptionsMenu()
        }

        override fun onDiscoveryError(errorCode: Int, errorMsg: String) {
            when (errorCode) {
                DiscoveryListener.ERROR_LACK_LOCATION_PERMISSION -> {
                }
                DiscoveryListener.ERROR_LOCATION_SERVICE_CLOSED -> {
                }
                DiscoveryListener.ERROR_SCAN_FAILED -> ToastUtils.showShort("搜索出错：$errorCode")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onDeviceFound(device: BluetoothDevice, rssi: Int) {
            devList.add(Device(device, rssi))
            baserecycleradapter?.notifyDataSetChanged()
        }
    }


    private fun doStartDiscovery() {
        BTManager.getInstance().startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        BTManager.getInstance().stopDiscovery()

    }

}