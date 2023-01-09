package com.glorystar.startool.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.glorystar.startool.config.bean.GridItem
import com.glorystar.startool.utils.tool.LiveDataBus
import java.util.*

class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_MEDIA_MOUNTED) {
            println("usb插入")
            LiveDataBus.get().with("USBInterfaceActivity").postValue("Intent.ACTION_MEDIA_MOUNTED")
        } else if (intent.action == Intent.ACTION_MEDIA_UNMOUNTED) {
            println("usb拔出")
            LiveDataBus.get().with("USBInterfaceActivity").postValue("Intent.ACTION_MEDIA_UNMOUNTED")
        }else if (intent.action ==Intent.ACTION_HEADSET_PLUG){
            println("耳机"+Intent.ACTION_HEADSET_PLUG)
            LiveDataBus.get().with("TrumpetActivity").postValue("ACTION_HEADSET_PLUG")
        }


    }

    companion object {
        var usbData = ArrayList<GridItem>()
    }

    fun addUsbPath() {

    }


}

