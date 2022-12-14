package com.glorystar.startool.ui.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.glorystar.startool.config.constant.Constantdtas;
import com.glorystar.startool.utils.tool.LiveDataBus;
import com.glorystar.startool.utils.tool.WIFIConnectionManager;

public class WIFIAutoConnectionService extends Service {
    private static final String TAG = WIFIAutoConnectionService.class.getSimpleName();
    private static final String KEY_SSID = "KEY_SSID";
    private static final String KEY_PWD = "KEY_PWD";


    /**
     * wifi名
     */
    private String mSsid = "";
    /**
     * 密码
     */
    private String mPwd = "";


    /**
     * 负责不断尝试连接指定wifi
     */
    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            WIFIConnectionManager.getInstance(WIFIAutoConnectionService.this).connect(Constantdtas.WIFI_SSID, Constantdtas.WIFI_PASS);
            boolean connected = WIFIConnectionManager.getInstance(WIFIAutoConnectionService.this).isConnected(Constantdtas.WIFI_SSID);
            //  WIFIConnectionManager.getInstance(WIFIAutoConnectionService.this).wifiManager.connectionInfo.rssi.toString()) getExtraInfo()
            String SSID = WIFIConnectionManager.getInstance(WIFIAutoConnectionService.this).getWifiManager().getConnectionInfo().getSSID();

            if ( SSID.isEmpty()) {
                Log.d(TAG, "handleMessage: re-try in 5 seconds");
                mHandler.sendEmptyMessageDelayed(0, 5000);//5s循环
            } else {
                Log.d(TAG, "handleMessage: re-try in");
                LiveDataBus.get().with("WifiActivity").postValue("WIFI_STATE_ENABLED");

            }
            return true;
        }
    });

    /**
     * 连接指定wifi热点, 失败后5s循环
     *
     * @param context 用于启动服务的上下文
     * @param ssid    默认HUD-WIFI
     * @param pwd     (WPA加密)默认12345678
     */
    public static void start(Context context, String ssid, String pwd) {
        Intent starter = new Intent(context, WIFIAutoConnectionService.class);
        starter.putExtra(KEY_SSID, ssid).putExtra(KEY_PWD, pwd);
        context.startService(starter);
        Log.d(TAG, "start: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * @return always null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        mSsid = intent.getStringExtra(KEY_SSID);
        mPwd = intent.getStringExtra(KEY_PWD);
        mHandler.sendEmptyMessage(0);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


}
