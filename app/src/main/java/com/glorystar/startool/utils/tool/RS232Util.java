package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.serialport.SerialPort;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

public class RS232Util {
    private static RS232Util rs232 = null;
    private OnGetDataListener onDataListener = null;
    private SerialReadThread thread = null;
    private SerialPort serialPort = null;
    private String COM = "/dev/ttyUSB0";
    private long sleepTime = 500L;
    private int baudrate = 9600;
    private Context mContext;

    public RS232Util(Context context) {
        mContext = context;
    }

    public void getData(OnGetDataListener dataListener) {
        this.onDataListener = dataListener;
    }

    public static RS232Util init(Context context) {
        if (rs232 == null) {
            rs232 = new RS232Util(context);
        }
        return rs232;
    }

    public void start() {
        try {
            this.serialPort = new SerialPort(new File(this.COM), this.baudrate, 0);
            Toast.makeText(mContext,"Serial port opened successfully",Toast.LENGTH_SHORT).show();
        } catch (IOException var2) {
            var2.printStackTrace();
            Toast.makeText(mContext,"Failed to open serial port",Toast.LENGTH_SHORT).show();
        } catch (SecurityException var3) {
            var3.printStackTrace();
            Toast.makeText(mContext,"Failed to open serial port: no read / write permission",Toast.LENGTH_SHORT).show();
        }

        this.thread = new SerialReadThread();
        SerialReadThread.isStop = false;
        this.thread.setSerialPort(this.serialPort);
        this.thread.setSleepTime(this.sleepTime);
        this.thread.start();
        this.thread.setOnDataReceiveListener(new SerialReadThread.OnDataReceiveListener() {
            public void onDataReceive(String str) {
                if (onDataListener != null) {
                    onDataListener.onDataReceive(str);
                }
            }
        });
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.shutdownThread();
            if (serialPort != null){
                Toast.makeText(mContext,"Serial port closed successfully",Toast.LENGTH_SHORT).show();
                serialPort = null;
            }
        }

    }

    public interface OnGetDataListener {
        void onDataReceive(String var1);
    }
}
