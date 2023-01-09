package com.glorystar.startool.ui.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.glorystar.startool.utils.tool.OKHttpUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadService extends IntentService {
    private String url, apkDir, name;
    private File downromfile;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //正在下载中
                    if (msg.obj.equals(apkDir)){
                        Intent intent = new Intent();
                        intent.setAction("com.glorystar.intent.APK_DEMO_UPDATE_PERCENT");
                        intent.putExtra("percent", msg.arg1);
                        intent.putExtra("filePath", apkDir);
                        sendBroadcast(intent);
                    }
                    break;
                case 1:
                    if (msg.obj.equals(apkDir)){
                        Intent intent2 = new Intent();
                        intent2.setAction("com.glorystar.intent.APK_DEMO_UPDATE_PERCENT");
                        intent2.putExtra("percent", msg.arg1);
                        intent2.putExtra("filePath", apkDir);
                        sendBroadcast(intent2);
                    }
                    break;
            }
            return true;
        }
    });

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction().equals("glorystar.intent.action.DOWNLOAD_DEMO_APK")){
            url = intent.getStringExtra("downloadUrl");
            name = intent.getStringExtra("name");
            File file = new File(getExternalCacheDir().getAbsolutePath(),name.toLowerCase()+"_update.apk");
            apkDir = String.valueOf(file);
            handleActionFoo(url,apkDir);
            System.out.println("apkDir--->"+apkDir);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        try {
            DownRom(param1, param2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送消息进行更新进度条
    public void sendMessage(int what,int mprogress,String file) {
        Message msg0 = mHandler.obtainMessage();
        msg0.what = what;
        msg0.arg1 = mprogress;
        msg0.obj = file;
        mHandler.sendMessage(msg0);
    }

    private void DownRom(String param1, String param2) {
        int oldProcess = 0;
        InputStream in = null;
        RandomAccessFile savedFile = null;
        downromfile = new File(param2);
        long currentLength = 0;
        if(downromfile.exists()){
            //如果文件存在的话，得到文件的大小
            currentLength =downromfile.length();
        }
        long totalLength = OKHttpUtil.getInstance().getTotalLength(param1);
        if (totalLength == currentLength && totalLength != 0){
            sendMessage(1,100, param2);
        }
        OkHttpClient client = OKHttpUtil.getInstance().client;
        Request request=new Request.Builder()
                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.90 Safari/537.36 2345Explorer/9.3.2.17331")
                .addHeader("RANGE","bytes="+currentLength+"-")
                .url(param1)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                in = response.body().byteStream();
                savedFile = new RandomAccessFile(downromfile,"rw");
                savedFile.seek(currentLength);
                byte[] b = new byte[2048];
                int total = 0;
                int len;
                while ((len = in.read(b))!=-1){
                    total += len;
                    savedFile.write(b,0,len);
                    int mProgress = (int)((total+currentLength) * 100 / totalLength);
                    if (oldProcess <= mProgress - 1) {
                        oldProcess = mProgress;
                        sendMessage(0, mProgress, param2);
                    }
                    if (mProgress == 100){
                        sendMessage(1,100, param2);
                    }
                }
            }
            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
