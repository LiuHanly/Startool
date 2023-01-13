package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.MemoryUtil
import com.glorystar.startool.utils.tool.NetworkUtil
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import java.lang.Exception
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.experimental.and

class DeviceInfoActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var btSerialNumber: Button
    lateinit var btAndroidVersion: Button
    lateinit var btFirmwareVersion: Button
    lateinit var btEthernetMac: Button
    lateinit var btWifiMac: Button
    lateinit var btStorage: Button
    lateinit var btRam: Button

    override fun getLayoutId(): Int {
        return R.layout.activity_device_info
    }

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.device_info)
        titleView.action = this

        btSerialNumber = findViewById(R.id.bt_serial_get)
        btAndroidVersion = findViewById(R.id.bt_android_get)
        btFirmwareVersion = findViewById(R.id.bt_firmware_get)
        btEthernetMac = findViewById(R.id.bt_ethernet_get)
        btWifiMac = findViewById(R.id.bt_wifi_mac)
        btStorage = findViewById(R.id.bt_storage)
        btRam = findViewById(R.id.bt_ram)

        btSerialNumber.setOnClickListener(this)
        btAndroidVersion.setOnClickListener(this)
        btFirmwareVersion.setOnClickListener(this)
        btEthernetMac.setOnClickListener(this)
        btWifiMac.setOnClickListener(this)
        btStorage.setOnClickListener(this)
        btRam.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_serial_get ->{
                var serialNum: String? = ""
                serialNum = if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
                showDialog(this, R.drawable.ic_device_info, getString(R.string.Serial_number), serialNum)
            }
            R.id.bt_android_get ->{
                showDialog(this, R.drawable.ic_device_info, getString(R.string.Android_version), Build.VERSION.RELEASE)
            }
            R.id.bt_firmware_get ->{
                showDialog(this, R.drawable.ic_device_info, getString(R.string.firmware_version), Build.DISPLAY)
            }
            R.id.bt_ethernet_get ->{
                showDialog(this, R.drawable.ic_device_info, getString(R.string.ethernet_mac_address), NetworkUtil.getEthernetMac())
            }
            R.id.bt_wifi_mac ->{
                if (NetworkUtil.isConnected(this)) {
                    showDialog(
                        this,
                        R.drawable.ic_device_info,
                        getString(R.string.wifi_mac_addres),
                        NetworkUtil.getMacAddress()
                    )
                }else{
                    Toast.makeText(this, "Please connect Wi-Fi first", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.bt_storage ->{
                showDialog(this, R.drawable.ic_device_info, getString(R.string.internal_storage),
                    MemoryUtil.getInternalToatalSpace(this).toString())
            }
            R.id.bt_ram ->{
                showDialog(this, R.drawable.ic_device_info, getString(R.string.ram),MemoryUtil.getTotalMemory(this).toString())
            }
        }
    }

    private fun showDialog(context: Context?, iconId: Int, title: String, info: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_get_info, null)
        var ivIcon = view.findViewById<ImageView>(R.id.iv_info)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)
        val relativeHelp = view.findViewById<RelativeLayout>(R.id.relative_help)
        val goSetting = view.findViewById<TextView>(R.id.go_setting)

        if (title.equals(getString(R.string.wifi_mac_addres)) || title.equals(getString(R.string.ethernet_mac_address))){
            relativeHelp.visibility = View.VISIBLE
        }else{
            relativeHelp.visibility = View.GONE
        }
        goSetting.setOnClickListener {
            if (title.equals(getString(R.string.wifi_mac_addres))) {
                StatusNaviBarUitl.show(this)
                val wifiIntent = Intent("android.settings.WIFI_SETTINGS")
                startActivity(wifiIntent)
            }else if (title.equals(getString(R.string.ethernet_mac_address))){
                StatusNaviBarUitl.show(this)
                val ethernetIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(ethernetIntent)
            }
        }

        ivIcon.setImageResource(iconId)
        tvTitle.text = title
        tvInfo.text = info
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}