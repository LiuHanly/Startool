package com.glorystar.startool.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.Observer;


import com.github.lzyzsd.circleprogress.DonutProgress;
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
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.glorystar.startool.R;

public class DDRTestKtFragment extends BaseFragemt {

    private static final String TAG = "DDRTestKtFragment";
    private static final String STRESS_APP_TEST = "stressapptest";
    private static final String ARM32_SUFFIX = "32bit";
    private static final String ARM64_SUFFIX = "64bit";

    private static final int COMMAND_UPDATE_LOG = 1;
    private static final int COMMAND_UPDATE_PROGRESS = 2;

    LinearLayout mContentContainer;
    TextView mLogTv;
    TextView mResultTv;
    TextView time_text;
    ProgressBar mProgressBar;
    TextView mProgressText;

    private static boolean isRooted = false;
    private static String sSuffix = ARM32_SUFFIX;
    private static int sMemory = 256;

    private Thread mStressThread;
    private Process mStressProcess;
    private CountDownTimer countDownTimer;
    private AgeingDao ageingDao;
    private int mTargetTime = 1;
    private String stTime = "";
    private Handler mHandler = new LogHandler(Looper.getMainLooper());

    private ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private long lastDbUpdateTime = 0;
    private static final long DB_UPDATE_INTERVAL = 60 * 1000; // 60秒

    private Thread stressGuardThread;


    private DonutProgress pgr_freq;

    private TextView tv_freq_max;
    private TextView tv_freq;


    private final List<String> stressOutputLines = new ArrayList<>();

    private class LogHandler extends Handler {
        LogHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!isAdded() || getView() == null) return;

            switch (msg.what) {

                case COMMAND_UPDATE_LOG:
                    String line = (String) msg.obj;
                    String displayLine = line;

                    // 如果输出包含 (UTC) 截取后面内容
                    if (line.contains("(UTC)")) {
                        displayLine = line.substring(line.indexOf("(UTC)") + 6);
                    }
                    stressOutputLines.add(displayLine);

                    // 输出到日志区
                    if (displayLine.contains("Error:")) {
                        displayLine = "<font color=\"#ff0000\">" + displayLine + "</font>";
                    }
                    mLogTv.append(Html.fromHtml(displayLine + "\n"));
                    //     mLogTv.post(() -> mLogTv.setSelection(mLogTv.getText().length()));
                    break;

                case COMMAND_UPDATE_PROGRESS:
                    float progress = (float) msg.obj;
                    pgr_freq.setProgress(progress);
                    break;

            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ageingDao = AppDataBase.Companion.getDatabase(getActivity()).getAgeingDao();
        stTime = DateUtil.INSTANCE.stampToTime("" + DateUtil.INSTANCE.getTimeStamp_L());

        ShellUtils.CommandResult result = ShellUtils.execCmd("ls -l", true);
        isRooted = (result.result >= 0 && TextUtils.isEmpty(result.errorMsg));

        if (TextUtils.isEmpty(AppUtils.getProperty("ro.product.cpu.abilist64", ""))) {
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
        return R.layout.ddrtest_fragment;
    }

    @Override
    protected void initView(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mContentContainer = view.findViewById(R.id.container_content);
        mLogTv = view.findViewById(R.id.tv_log);
        mResultTv = view.findViewById(R.id.tv_result);
        time_text = view.findViewById(R.id.time_text);

        pgr_freq = view.findViewById(R.id.pgr_freq);
        tv_freq_max = view.findViewById(R.id.tv_freq_max);
        tv_freq = view.findViewById(R.id.tv_freq);

        if (mProgressBar != null) mProgressBar.setMax(100);
        mLogTv.setMovementMethod(new ScrollingMovementMethod());

        LiveDataBus.get().with("DDRTestKtFragment", String.class)
                .observe(this, s -> {
                    String[] parts = s.split(":");
                    if (parts[0].contains("start")) {
                        start();
                        setGOms(DB_UPDATE_INTERVAL);
                        // setGOms(Long.parseLong(parts[1]));
                    } else if (parts[0].contains("ReaderThread")) {
                        mLogTv.append(Html.fromHtml(":" + parts[1]));
                        mLogTv.append("\n");
                    } else if (parts[0].contains("stop")) {
                        String finalStatus = null;


                        for (int i = stressOutputLines.size() - 1; i >= 0; i--) {
                            String line = stressOutputLines.get(i);
                            if (line.startsWith("Status:")) {
                                finalStatus = line;
                                break;
                            }
                        }
                        Log.e(TAG, "LiveDataBus error==stop"+finalStatus);
                        if (finalStatus != null) {
                            Log.e(TAG, "LiveDataBus error==stop"+ finalStatus.startsWith("Status: PASS"));
                            if (finalStatus.startsWith("Status: PASS")) {
                                time_text.setTextColor(Color.GREEN);
                                time_text.setText("PASS");
                            } else if (finalStatus.startsWith("Status: FAIL")) {
                                time_text.setTextColor(Color.RED);
                                time_text.setText("FAIL");
                            }

                        }

                    }
                });
    }

    public void setGOms(Long thisTime) {
        countDownTimer = new CountDownTimer(thisTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minute = (millisUntilFinished / (1000 * 60)) % 60;
                long second = (millisUntilFinished / 1000) % 60;
                time_text.setText(hour + ":" + minute + ":" + second + " /h");

                long[] memInfo = getMemoryUsedMB();
                int memPercent = (int) ((memInfo[0] * 100) / memInfo[1]);
                //  pgr_freq.setProgress(progress);
                // 更新进度条和文本
                pgr_freq.setProgress(memPercent);
                String text = memPercent + "% (" + memInfo[0] + " MB / " + memInfo[1] + " MB)";
                tv_freq.setText(memInfo[0] + "");
                tv_freq_max.setText(memInfo[1] + "");

                updateDatabaseAsync(millisUntilFinished);
                LiveDataBus.get().with("DDRTestKtFragment").postValue("ReaderThread:" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                LiveDataBus.get().with("DDRTestKtFragment").postValue("stop:0");
                LiveDataBus.get().with("AgeingKtActivity").postValue("DDRTestKtFragment:0");
            }
        };
        countDownTimer.start();
    }

    private void updateDatabaseAsync(long remainTime) {
        long current = System.currentTimeMillis();
        if (current - lastDbUpdateTime < DB_UPDATE_INTERVAL) return;
        lastDbUpdateTime = current;

        dbExecutor.execute(() -> {
            try {
                ageingDao.update(new Ageing(
                        2,
                        "DDRTestKtFragment",
                        current + "",
                        stTime,
                        "2",
                        String.valueOf(remainTime)
                ));
            } catch (Exception e) {
                Log.e(TAG, "DB update error", e);
            }
        });
    }

    public void start() {
        if (!binExist(STRESS_APP_TEST)) copyBin(STRESS_APP_TEST);

        sMemory = calculateFixedStressMemoryFor70Percent();
        startStressProcess();
        startStressGuard();
    }

    public void stop() {
        if (countDownTimer != null) countDownTimer.cancel();
        stopStress();
        stopStressGuard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stop();
    }

    private boolean binExist(String name) {
        File bin = new File(AppUtils.getRootDir(getActivity()) + File.separator + name);
        return bin.exists();
    }

    private boolean copyBin(String name) {
        File desFile = new File(AppUtils.getRootDir(getActivity()) + File.separator + name);
        if (copyAssetFile("bin/" + name + "_" + sSuffix, desFile)) {
            ShellUtils.execCmd("cp " + desFile.getAbsolutePath() + " /data/" + name, true);
            ShellUtils.execCmd("chmod 777 /data/" + name, true);
            return true;
        }
        return false;
    }

    private boolean copyAssetFile(String from, File to) {
        try (InputStream is = getResources().getAssets().open(from);
             OutputStream os = new FileOutputStream(to)) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = is.read(buffer)) != -1) os.write(buffer, 0, count);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "copyAssetFile error", e);
        }
        return false;
    }

    private void startStressProcess() {
        mStressThread = new Thread(() -> {
            try {
                mStressProcess = Runtime.getRuntime().exec("su");
                DataOutputStream dos = new DataOutputStream(mStressProcess.getOutputStream());

                String command = "/data/" + STRESS_APP_TEST + " -s " + (mTargetTime - 10)
                        + " -i 4 -C 4 -W --stop_on_errors -M " + sMemory + "\n";


                Log.i(TAG, "Starting stressapptest memory=" + sMemory + " MB");
                mHandler.obtainMessage(COMMAND_UPDATE_LOG, "Starting stressapptest memory=" + sMemory + " MB").sendToTarget();

                dos.write(command.getBytes(Charset.forName("utf-8")));
                dos.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(mStressProcess.getInputStream()));
                String line;
                while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
                    mHandler.obtainMessage(COMMAND_UPDATE_LOG, line).sendToTarget();
                }

                dos.writeBytes("exit\n");
                dos.flush();
                dos.close();

            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) Log.e(TAG, "stress io error", e);
            } finally {
                if (mStressProcess != null) {
                    mStressProcess.destroy();
                    mStressProcess = null;
                }
            }
        });
        mStressThread.start();
    }

    private void stopStress() {
        if (mStressThread != null) mStressThread.interrupt();
        if (mStressProcess != null) {
            mStressProcess.destroy();
            mStressProcess = null;
        }
    }

    private void startStressGuard() {
        stressGuardThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                int memPercent = getCurrentMemoryUsagePercent();
                if (memPercent < 68) restartStress();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        });
        stressGuardThread.start();
    }

    private void stopStressGuard() {
        if (stressGuardThread != null) stressGuardThread.interrupt();
    }

    private void restartStress() {
        stopStress();
        startStressProcess();
    }

    public int calculateFixedStressMemoryFor70Percent() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
            long memTotal = 0, memAvailable = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemTotal:"))
                    memTotal = Long.parseLong(line.replaceAll("\\D+", ""));
                if (line.startsWith("MemAvailable:"))
                    memAvailable = Long.parseLong(line.replaceAll("\\D+", ""));
                if (memTotal > 0 && memAvailable > 0) break;
            }

            long currentUsed = memTotal - memAvailable;
            long targetUsed = (long) (memTotal * 0.99);
            long needAllocate = targetUsed - currentUsed;
            if (needAllocate < 512 * 1024) needAllocate = 512 * 1024;

            long safeMax = memAvailable - 50 * 1024;
            if (needAllocate > safeMax) needAllocate = safeMax;

            return (int) (needAllocate / 1024);
        } catch (Exception e) {
            e.printStackTrace();
            return 1024;
        }
    }

    private long[] getMemoryUsedMB() {
        long memTotal = 0, memAvailable = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemTotal:"))
                    memTotal = Long.parseLong(line.replaceAll("\\D+", ""));
                if (line.startsWith("MemAvailable:"))
                    memAvailable = Long.parseLong(line.replaceAll("\\D+", ""));
                if (memTotal > 0 && memAvailable > 0) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long used = memTotal - memAvailable;
        return new long[]{used / 1024, memTotal / 1024};
    }

    private int getCurrentMemoryUsagePercent() {
        long[] mem = getMemoryUsedMB();
        if (mem[1] == 0) return 0;
        return (int) ((mem[0] * 100) / mem[1]);
    }

    private int getTotalRam() {
        String firstLine = "";
        int totalRam = 2;
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"), 8192)) {
            firstLine = br.readLine().split("\\s+")[1];
        } catch (Exception e) {
            Log.e(TAG, "getTotalRam error", e);
        }
        if (!TextUtils.isEmpty(firstLine))
            totalRam = (int) Math.ceil(Float.parseFloat(firstLine) / (1024 * 1024));
        return totalRam;
    }
}