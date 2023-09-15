package com.glorystar.startool.utils.tool;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKHttpUtil {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public OkHttpClient client = null;
    private static OKHttpUtil instance;

    public static OKHttpUtil getInstance() {
        if (instance == null){
            synchronized (OKHttpUtil.class) {
                if (instance == null) {
                    instance = new OKHttpUtil();
                }
            }
        }
        return instance;
    }

    public OKHttpUtil(){
       client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(new SSLSocketClient().getSSLSocketFactory(), new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })//配置
                .hostnameVerifier(new SSLSocketClient().getHostnameVerifier())//配置    //忽略验证证书
                .build();
    }

    public void doGet(String url, Callback callback){
        OkHttpClient okHttpClient = getInstance().client;
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public long getTotalLength(String url){
        Request request = new Request.Builder()
                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.90 Safari/537.36 2345Explorer/9.3.2.17331")
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()){
                long totalLength = response.body().contentLength();
                response.body().close();
                return totalLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
        if (client == null || tag == null) return;
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /** 取消所有请求请求 */
    public void cancelAll() {
        if (client == null) return;
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}
