package com.glorystar.startool.config.bean

import android.bluetooth.BluetoothDevice
import java.util.*

class Device internal constructor(var device: BluetoothDevice, var rssi: Int) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Device) return false
        return device == o.device
    }

    override fun hashCode(): Int {
        return Objects.hash(device)
    }
}