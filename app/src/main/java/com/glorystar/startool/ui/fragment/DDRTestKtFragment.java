package com.glorystar.startool.ui.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;


import com.glorystar.startool.config.base.BaseFragemt;
import com.glorystar.startool.config.constant.AgeingDataBase;
import com.glorystar.startool.utils.db.Ageing;
import com.glorystar.startool.utils.db.AgeingDao;
import com.glorystar.startool.utils.db.AppDataBase;
import com.glorystar.startool.utils.tool.AppUtils;
import com.glorystar.startool.utils.tool.DateUtil;
import com.glorystar.startool.utils.tool.LiveDataBus;
import com.glorystar.startool.utils.tool.ShellUtils;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import com.glorystar.startool.R;


public class DDRTestKtFragment extends BaseFragemt {
    public String TAG = "DDRTestKtFragment";
    private static final String STRESS_APP_TEST = "stressapptest";
    private static final String ARM32_SUFFIX = "32bit";
    private static final String ARM64_SUFFIX = "64bit";

    private static final int COMMAND_UPDATE_LOG = 1;
    LinearLayout mContentContainer;
    TextView mLogTv;
    TextView mResultTv;
    TextView time_text;

    Long TIME;
    private static boolean isRooted = false;
    private static String sSuffix = ARM32_SUFFIX;
    private static int sMemory = 256;
    private Thread mStressAppTestThread = null;
    private CountDownTimer countDownTimer;
    private AgeingDao ageingDao;

    private int mTargetTime = 0;
    private String stTime = "";
    private Handler mHandler = new LogHandler(Looper.getMainLooper());

    private class LogHandler extends Handler {
        LogHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case COMMAND_UPDATE_LOG:

                    String line = (String) msg.obj;
                    Log.e("DDRTestFragment", "LogHandler()-->" + msg.what + "line-->" + line);
                    String pretty = line.substring(line.indexOf("(UTC)") + 6);
                    if (pretty.startsWith("Status: PASS")) {
                        mResultTv.setText(pretty);
                        //    mResultTv.setTextColor(getContext().getResources().getColor(R.color.white));
                    } else if (pretty.startsWith("Status: FAIL")) {
                        mResultTv.setText(pretty);
                        //    mResultTv.setTextColor(getContext().getResources().getColor(R.color.white));
                    }
                    if (pretty.contains("Error:")) {
                        pretty = "<font color=\"#ff0000\">" + pretty + "</font>";
                        //  markFailure();
                    }
                    mLogTv.append(Html.fromHtml(pretty));
                    mLogTv.append("\n");
                    int offset = mLogTv.getLineCount() * mLogTv.getLineHeight();
                    if (offset > mLogTv.getHeight()) {
                        mLogTv.scrollTo(0, offset - mLogTv.getHeight());
                    }
                    break;
            }
        }
    }

    Thread  thread = new Thread();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ageingDao = AppDataBase.Companion.getDatabase(getActivity()).getAgeingDao();
        stTime = DateUtil.INSTANCE.stampToTime("" + DateUtil.INSTANCE.getTimeStamp_L());


        ShellUtils.CommandResult result = ShellUtils.execCmd("ls -l", true);
        isRooted = (result.result >= 0 && TextUtils.isEmpty(result.errorMsg));
        Log.e("DDRTestFragment", "onCreate()");
        if (TextUtils.isEmpty(AppUtils.getProperty(
                "ro.product.cpu.abilist64", ""))) {
            sSuffix = ARM32_SUFFIX;
        } else {
            sSuffix = ARM64_SUFFIX;
        }

        switch (getTotalRam()) {
            case 1:
                sMemory = 128;
                break;
            case 2:
                sMemory = 256;
                break;
            case 3:
                sMemory = 256;
                break;
            case 4:
                sMemory = 512;
                break;
            default:
                sMemory = 256;
                break;
        }

    }


    @Override
    protected int getLayoutId() {
        return  R.layout.ddrtest_fragment;
    }

    @Override
    protected void initView(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mContentContainer = view.findViewById(R.id.container_content);
        mLogTv = view.findViewById(R.id.tv_log);
        mResultTv = view.findViewById(R.id.tv_result);
        time_text = view.findViewById(R.id.time_text);

        LiveDataBus.get().with("DDRTestKtFragment", String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String[] name = s.split(":");
                if (name[0].contains("start")) {
                 start();
                   // Log.e("DDRTestKtFragment",""+);
                   setGOms(Long.parseLong(name[1]));
                } else if (name[0].contains("ReaderThread")) {
                    mLogTv.append(Html.fromHtml(":" + (name[1])));
                    mLogTv.append("\n");

                } else if (name[0].contains("stop")) {
                    time_text.setText("完成");
                }


            }
        });
    }


    public void setGOms(Long thie) {
        countDownTimer = new CountDownTimer(thie, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long day = millisUntilFinished / (1000 * 24 * 60 * 60); //单位天

                long hour = (millisUntilFinished - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60);
                //单位时
                long minute = (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60);
                //单位分
                long second = (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;
                //单位秒
                time_text.setText(hour + "：" + minute + "：" + second + " /h");
                seton(millisUntilFinished);
                LiveDataBus.get().with("DDRTestKtFragment").postValue("ReaderThread:"+millisUntilFinished);

            }

            @Override
            public void onFinish() {
                LiveDataBus.get().with("DDRTestKtFragment").postValue("stop:0");
                LiveDataBus.get().with("AgeingKtActivity").postValue("DDRTestKtFragment:0");
            }
        };
        countDownTimer.start();
    }

    public synchronized void seton(Long millisUntilFinished) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ageingDao.update(new Ageing(2, "DDRTestKtFragment",
                        AgeingDataBase.getInstance(getContext()).getAgeingDao().getAgeingID(1).getAgeungStopTime(),
                        stTime, "2", millisUntilFinished.toString()));

            }
        }).start();
    }

    public void start() {
        mStressAppTestThread = new Thread(mStressAppTestRunnable);
        mStressAppTestThread.start();
        Log.e("DDRTestFragment", "start()");
        Log.e("DDRTestFragment", "onStartClicked");
        if (!binExist(STRESS_APP_TEST)) {
            if (!copyBin(STRESS_APP_TEST)) {
            }
        }
    }


    public void stop() {
        Log.e("DDRTestFragment", "stop()");
        if (mStressAppTestThread != null) {
            mStressAppTestThread.interrupt();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private boolean binExist(String name) {
        Log.e("DDRTestFragment", "binExist()");
        File bin = new File(AppUtils.getRootDir(getActivity())
                + File.separator + name);
        return bin.exists();
    }

    private boolean copyBin(String name) {
        Log.e("DDRTestFragment", "copyBin()");
        File desFile = new File(AppUtils.getRootDir(getActivity())
                + File.separator + name);
        if (copyAssetFile("bin/" + name + "_" + sSuffix, desFile)) {
            ShellUtils.CommandResult result1 = ShellUtils.execCmd(
                    "cp " + desFile.getAbsolutePath() + " /data/" + name,
                    true);
            Log.i(TAG, "copyBin, cp result: " + result1.toString());

            ShellUtils.CommandResult result2 = ShellUtils.execCmd(
                    "chmod 777 /data/" + name,
                    true);
            Log.i(TAG, "copyBin, chmod result: " + result2.toString());

            return result1.result >= 0 && TextUtils.isEmpty(result1.errorMsg)
                    && result2.result >= 0 && TextUtils.isEmpty(result2.errorMsg);
        }
        return false;
    }

    private boolean copyAssetFile(String from, File to) {
        Log.e("DDRTestFragment", "copyAssetFile");
        if (!TextUtils.isEmpty(from) && to != null) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = getResources().getAssets().open(from);
                os = new FileOutputStream(to);

                int count;
                byte[] bytes = new byte[1024];
                while ((count = is.read(bytes)) != -1) {
                    os.write(bytes, 0, count);
                }
                return true;
            } catch (IOException e) {
                Log.e(TAG, "copyFile, copy file error: " + e.getMessage());
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "copyFile, close file error: " + e.getMessage());
                }
            }
        }
        return false;
    }

    public int getTotalRam() {
        Log.e("DDRTestFragment", "getTotalRam");
        String path = "/proc/meminfo";
        String firstLine = "";
        int totalRam = 2; // default: 2GB
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            Log.e(TAG, "getTotalRam, error: " + e.getMessage());
        }
        if (!TextUtils.isEmpty(firstLine)) {
            totalRam = (int) Math.ceil((Float.valueOf(Float.parseFloat(firstLine) / (1024 * 1024))
                    .doubleValue()));
        }

        Log.i(TAG, "getTotalRam, total ram: " + totalRam + "GB");

        return totalRam;
    }

    private Runnable mStressAppTestRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "stressapptest run...");

            DataOutputStream dos = null;
            try {
                Process process = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(process.getOutputStream());

                String command = "/data/" + STRESS_APP_TEST + "  -s " + (mTargetTime - 10) + " -i 4 -C 4 -W --stop_on_errors -M " + sMemory + "\n";

                Log.e("mTargetTime", "mTargetTime--->" + mTargetTime);
                dos.write(command.getBytes(Charset.forName("utf-8")));
                dos.flush();

                String line;
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                while (true && (line = bufferedReader.readLine()) != null) {
                    Log.i("BufferedReader", "stressapptest: " + line + "----->");
                    Message msg = new Message();
                    msg.what = COMMAND_UPDATE_LOG;
                    msg.obj = line;
                    mHandler.sendMessage(msg);
                }

                dos.writeBytes("exit\n");
                dos.flush();
                Log.w(TAG, "stressapptest exit.");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                try {
                    if (dos != null) {
                        dos.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    };
}
