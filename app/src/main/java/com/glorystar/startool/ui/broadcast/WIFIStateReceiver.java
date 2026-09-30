package com.glorystar.startool.ui.broadcast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


import androidx.core.app.ActivityCompat;

import com.glorystar.startool.config.constant.Constant;
import com.glorystar.startool.utils.tool.LiveDataBus;
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
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        scanResults =  WIFIConnectionManager.getInstance(mContext).getWifiManager().getScanResults();
//        for (int i = 0 ; i < scanResults.size();i++) {
//            Log.e(TAG,"scanResults:----"+(scanResults.get(i)).SSID);
//        }
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
