package com.glorystar.startool.utils.tool;

import com.glorystar.startool.config.constant.Constant;


public class IntentJumpUtils {


    /**
     * 设置语言
     */
    public static void setLanguage(int value) {
        SharePreferences.getInstance().putInt(Constant.USERINFOR_LANGU, value);
    }


    /**
     * 获取语言
     */
    public static int getLanguage() {
        return SharePreferences.getInstance().getInt(Constant.USERINFOR_LANGU);
    }

}
