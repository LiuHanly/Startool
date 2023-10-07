package com.glorystar.startool.utils.tool;

import com.glorystar.startool.config.constant.Constant;


public class IntentJumpUtils {


    public static void setLanguage(int value) {
        SharePreferences.getInstance().putInt(Constant.USERINFOR_LANGU, value);
    }


    public static int getLanguage() {
        return SharePreferences.getInstance().getInt(Constant.USERINFOR_LANGU);
    }

}
