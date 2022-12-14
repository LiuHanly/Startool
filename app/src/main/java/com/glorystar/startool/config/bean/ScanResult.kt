package com.glorystar.systemtest.config.bean

import android.bluetooth.BluetoothDevice

class ScanResult internal constructor(device: BluetoothDevice?, rssi: Int) {
    var device: BluetoothDevice? = null
    var rssi = 0

    init {
        this.device = device
        this.rssi = rssi
    }
}