package com.glorystar.startool.utils.tool;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MemoryUtil {
    private static final String TAG = MemoryUtil.class.getName();
    private static final String MEM_INFO_PATH = "/proc/meminfo";
    public static final String MEMTOTAL = "MemTotal";
    public static final String MEMFREE = "MemFree";

    /**
     *
     * @param context
     * @return
     */
    public static String getTotalMemory(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            mAm.getMemoryInfo(info);
            return String.format("%.2f", info.totalMem / 1024 / 1024 / 1024f) +" GB";
        }else {
            return getMemInfoIype(context, MEMTOTAL);
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getMemoryFree(Context context) {
        return getMemInfoIype(context, MEMFREE);
    }

    /**
     *
     * @param context
     * @param type
     * @return
     */
    public static String getMemInfoIype(Context context, String type) {
        try {
            FileReader fileReader = new FileReader(MEM_INFO_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(type)) {
                    break;
                }
            }
            bufferedReader.close();
            String[] array = str.split("\\s+");

            int length = Integer.valueOf(array[1]).intValue() * 1024;
            return Formatter.formatFileSize(context, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param context
     * @return
     */
    public static String getInternalToatalSpace(Context context){
        String path = Environment.getDataDirectory().getPath();
        Log.d(TAG,"root path is "+path);
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        //long availableBlocks = statFs.getAvailableBlocks();
        //long useBlocks  = totalBlocks - availableBlocks;

        long rom_length = totalBlocks*blockSize;

        return Formatter.formatFileSize(context,rom_length);
    }

    /**
     * @param context
     * @return
     */
    public static String getInternalUseSpace(Context context){
        String path = Environment.getDataDirectory().getPath();
        Log.d(TAG,"root path is "+path);
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks  = totalBlocks - availableBlocks;

        long rom_length = useBlocks*blockSize;

        return Formatter.formatFileSize(context,rom_length);
    }
}
