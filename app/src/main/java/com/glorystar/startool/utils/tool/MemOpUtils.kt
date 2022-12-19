package com.glorystar.startool.utils.tool

object MemOpUtils {

    @JvmStatic
    external fun malloc(size: Int): Int
    @JvmStatic
    external fun free()

    @JvmStatic
    external fun bluetooth()

    init {
        System.loadLibrary("native-lib")
    }


}