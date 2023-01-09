package com.glorystar.startool.config.bean;

import com.glorystar.startool.utils.tool.OKHttpUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineData implements UpdateModel {
    @Override
    public void getApkData(String url, final UpdateData updateData) {
        OKHttpUtil.getInstance().doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateData.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String apkJson = response.body().string();
                    System.out.println(apkJson);
                    Gson gson = new Gson();
                    UpdateBean apk_data = gson.fromJson(apkJson, UpdateBean.class);
                    updateData.apkInfo(apk_data);
                }else {
                    updateData.onFail();
                }
            }
        });
    }
}
