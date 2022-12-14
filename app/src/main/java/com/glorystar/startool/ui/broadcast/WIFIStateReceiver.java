package com.glorystar.startool.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.glorystar.startool.utils.tool.WIFIConnectionManager;

import java.util.List;

public class WIFIStateReceiver  extends BroadcastReceiver {
    private static final String TAG = WIFIStateReceiver.class.getName();
    private Context mContext;
    List<ScanResult> scanResults;

    public WIFIStateReceiver(Context context) {
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            return;
        }
        scanResults =  WIFIConnectionManager.getInstance(mContext).getWifiManager().getScanResults();
        for (int i = 0 ; i < scanResults.size();i++) {
            Log.e(TAG,"scanResults:----"+(scanResults.get(i)).SSID);
        }
//        if (!WIFIConnectionManager.getInstance(mContext).isConnected(Constant.WIFI_SSID)  ) {
//
//            WIFIConnectionManager.getInstance(mContext).connect(Constant.WIFI_SSID, Constant.WIFI_PASS);
//
//            LiveDataBus.get().with("WifiActivity").postValue("WIFI_SSID");
//        }else {
//            LiveDataBus.get().with("WifiActivity").postValue("WIFI_STATE_ENABLED");
//        }
    }


}
