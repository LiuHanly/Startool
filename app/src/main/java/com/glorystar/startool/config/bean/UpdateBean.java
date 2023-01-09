package com.glorystar.startool.config.bean;

public class UpdateBean {

    //{"apkUrl":"http://139.196.216.138/app_storage/app-release_v8.1.2.apk","apkVersion":"8.1.0","updateInfo":"1.修复RK3128平台锁定launcher并隐藏上下栏，断电重启后，出现SystemUI报错的问题
    //2.更新版本至8.1.2"}

    private String apkUrl;
    private String apkVersion;
    private String updateInfo;

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }
}
