package com.glorystar.startool.utils.tool;


import android.content.Context;
import android.content.Intent;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.util.Log;
import android.widget.Toast;


import com.glorystar.startool.ui.activity.BarcodeActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarcodeUtil {
    private static BarcodeUtil barcode = null;
    private OnGetDataListener onDataListener = null;
    private SerialReadThread thread = null;
    private SerialPort serialPort = null;
    private String COM = "/dev/ttyACM0";
    private long sleepTime = 500L;
    private int baudrate = 115200;
    //index：0-->honeywell, 1-->newland
    private boolean addEnter = false;
    //true:webpage, false:apk
    private boolean scanMode = false;
    private Context mContext;
    private SerialPortFinder finder;
    private List<String> comList = new ArrayList<>();
    private int ret = -1;

    public BarcodeUtil(Context context) {
        mContext = context;
    }

    public void getData(OnGetDataListener dataListener) {
        this.onDataListener = dataListener;
    }

    public void setCOM(String com){
        this.COM = com;
    }

    public int init() {
        finder = new SerialPortFinder();
        comList = Arrays.asList(finder.getAllDevicesPath());
        if (comList.contains(COM)){
            ret = 0;
        }else {
            Toast.makeText(mContext,"Please connect the module first",Toast.LENGTH_SHORT).show();
            ret = -1;
        }
        if (ret == 0) {
            try {
                this.serialPort = new SerialPort(new File(this.COM), this.baudrate, 0);
                ret = 1;
            } catch (IOException var2) {
                Toast.makeText(mContext,"Failed to open serial port",Toast.LENGTH_SHORT).show();
                var2.printStackTrace();
            } catch (SecurityException var3) {
                Toast.makeText(mContext,"Failed to open serial port: no read / write permission",Toast.LENGTH_SHORT).show();
                var3.printStackTrace();
            }
        }
        return ret;
    }

    public void start() {
        if (ret != 1){
            return;
        }
        this.thread = new SerialReadThread();
        SerialReadThread.isStop = false;
        this.thread.setSerialPort(this.serialPort);
        this.thread.setSleepTime(this.sleepTime);
        this.thread.start();
        this.thread.setOnDataReceiveListener(new SerialReadThread.OnDataReceiveListener() {
            public void onDataReceive(byte[] buffer, int size) {
                String str = new String(buffer, 0, size).toString();
                if (onDataListener != null) {
                    if (!str.startsWith("BEPBEP") && !str.startsWith("SCNLED")) {
                        if (scanMode) {
                            String info;
                            if (addEnter) {
                                info = str + "\n";
                            } else {
                                info = str;
                            }
                            Intent intent = new Intent();
                            intent.setAction("com.glorystar.android.BARCODE_DATA");
                            intent.putExtra("barcode_data", info);
                            mContext.sendBroadcast(intent);
                        } else {
                            onDataListener.onDataReceive(str);
                        }
                    }
                }
            }
        });
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.shutdownThread();
        }

    }

    public interface OnGetDataListener {
        void onDataReceive(String var1);
    }

    public void setLightMode(int mode){
        System.out.println(mode);
        if (BarcodeActivity.Companion.getIndex() == 0){
            Log.i("honeywell mode---->",mode+"");
            if (mode == 0){//honeywell light open
                thread.sendCmds(thread.HexToByteArr("164D0D53434E4C4544312E"));
            }else if (mode == 1){//honeywell light auto
                thread.sendCmds(thread.HexToByteArr("164D0D53434E4C4544332E"));
            }else if (mode == 2){//honeywell light close
                thread.sendCmds(thread.HexToByteArr("164D0D53434E4C4544302E"));
            }
        }else if (BarcodeActivity.Companion.getIndex() == 1){
            Log.i("newland mode---->",mode+"");
            if (mode == 0){//newland light open
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303230303031303B303030363030303B"));
            }else if (mode == 1){//newland light auto
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303230303030303B303030363030303B"));
            }else if (mode == 2){//newland light close
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303230303032303B303030363030303B"));
            }
        }
    }

    public void setScanningMode(boolean mode){
        if (mode){
            scanMode = true;
        }else {
            scanMode = false;
        }
    }

    public void setBeep(boolean beep){
        if (BarcodeActivity.Companion.getIndex() == 0){
            if (beep){
                thread.sendCmds(thread.HexToByteArr("164D0D424550424550312E"));
            }else {
                thread.sendCmds(thread.HexToByteArr("164D0D424550424550302E"));
            }
        }else if (BarcodeActivity.Companion.getIndex() == 1){
            if (beep){
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303230333031303B303030363030303B"));
            }else {
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303230333030303B303030363030303B"));
            }
        }
    }

    public void setAutoEnter(boolean enter){
        addEnter = enter;
    }

    public void setContinueScan(boolean open){
        if (open){
            if (BarcodeActivity.Companion.getIndex() == 0){
                thread.sendCmds(thread.HexToByteArr("164D0D50415053504E2E"));
            }else if (BarcodeActivity.Companion.getIndex() == 1){
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303330323032303B303030363030303B"));
            }
        }else {
            if (BarcodeActivity.Companion.getIndex() == 0){
                thread.sendCmds(thread.HexToByteArr("164D0D5452474D4F44332E"));
            }else if (BarcodeActivity.Companion.getIndex() == 1){
                thread.sendCmds(thread.HexToByteArr("4E4C53303030363031303B303330323031303B303030363030303B"));
            }
        }
    }
}
