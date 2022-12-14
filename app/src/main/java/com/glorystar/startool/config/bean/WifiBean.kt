package com.glorystar.systemtest.config.bean

class WifiBean : Comparable<WifiBean?> {
    var wifiName: String? = null
    var level = 0
    var state //已连接  正在连接  未连接 三种状态
            : String? = null
    var capabilities //加密方式
            : String? = null

    override fun toString(): String {
        return "WifiBean{" +
                "wifiName='" + wifiName + '\'' +
                ", level='" + level + '\'' +
                ", state='" + state + '\'' +
                ", capabilities='" + capabilities + '\'' +
                '}'
    }


    override fun compareTo(other: WifiBean?): Int {
        val level1 = level
        val level2 = other?.level
        //level1 - level2  //倒序
        return level2?.minus(level1)!! //升序
    }


}