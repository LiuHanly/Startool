package com.glorystar.startool.utils.tool

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.util.rangeTo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.experimental.and

class NetworkUtils private constructor() {
    companion object {
        const val NETWORK_WIFI = 1 // wifi network
        const val NETWORK_4G = 4 // "4G" networks
        const val NETWORK_3G = 3 // "3G" networks
        const val NETWORK_2G = 2 // "2G" networks
        const val NETWORK_UNKNOWN = 5 // unknown network
        const val NETWORK_NO = -1 // no network
        private const val NETWORK_TYPE_GSM = 16
        private const val NETWORK_TYPE_TD_SCDMA = 17
        private const val NETWORK_TYPE_IWLAN = 18

        /**
         *
         * @param context
         */
        fun openWirelessSettings(context: Context) {
            if (Build.VERSION.SDK_INT > 10) {
                context.startActivity(
                    Intent(
                        Settings.ACTION_SETTINGS
                    )
                )
            } else {
                context.startActivity(
                    Intent(
                        Settings.ACTION_WIRELESS_SETTINGS
                    )
                )
            }
        }

        /**
         *
         * @param context
         * @return NetworkInfo
         */
        private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        /**
         *
         * @param context
         * @return `true`: 可用 <br></br>
         * `false`: 不可用
         */
        fun isAvailable(context: Context): Boolean {
            val info = getActiveNetworkInfo(context)
            return info != null && info.isAvailable
        }

        /**
         *
         * @param context
         * @return `true`: 是<br></br>
         * `false`: 否
         */
        fun isConnected(context: Context): Boolean {
            val info = getActiveNetworkInfo(context)
            return info != null && info.isConnected
        }

        /**
         *
         * @param context
         * @return `true`:
         * `false`:
         */
        fun is4G(context: Context): Boolean {
            val info = getActiveNetworkInfo(context)
            return (info != null && info.isAvailable
                    && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
        }

        /**
         *
         * @param context
         * @return `true`:
         * `false`:
         */
        fun isWifiConnected(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return (cm != null
                    && cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI)
        }

        /**
         * @param context
         * @return
         */
        fun getNetworkOperatorName(context: Context): String? {
            val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.networkOperatorName
        }

        /**
         *
         * @param context
         * @return
         */
        fun getNetWorkType(context: Context): Int {
            var netType = NETWORK_NO
            val info = getActiveNetworkInfo(context)
            if (info != null && info.isAvailable) {
                netType = if (info.type == ConnectivityManager.TYPE_WIFI) {
                    NETWORK_WIFI
                } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    when (info.subtype) {
                        NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G
                        NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G
                        NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NETWORK_3G
                            } else {
                                NETWORK_UNKNOWN
                            }
                        }
                    }
                } else {
                    NETWORK_UNKNOWN
                }
            }
            return netType
        }

        /**
         * @param context
         * @return
         */
        fun getNetWorkTypeName(context: Context): String {
            return when (getNetWorkType(context)) {
                NETWORK_WIFI -> "NETWORK_WIFI"
                NETWORK_4G -> "NETWORK_4G"
                NETWORK_3G -> "NETWORK_3G"
                NETWORK_2G -> "NETWORK_2G"
                NETWORK_NO -> "NETWORK_NO"
                else -> "NETWORK_UNKNOWN"
            }
        }



        /**
         *
         * @return
         */
        val macAddress: String?
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            get() {
                var mac: String? = null
                try {
                    val ip = localInetAddress
                    val b = NetworkInterface.getByInetAddress(ip)
                        .hardwareAddress
                    var buffer = StringBuffer()

                    b.forEach {
                        val append = buffer.append('-')
                        val str = Integer.toHexString((it and  0xFF.toByte()).toInt())
                        buffer.append(if (str.length == 1) "0$str" else str)
                    }



                    mac = buffer.toString().toUpperCase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return mac
            }

        /**
         *
         * @return
         */
        val localInetAddress: InetAddress?
            get() {
                var ip: InetAddress? = null
                try {
                    val en_netInterface = NetworkInterface
                        .getNetworkInterfaces()
                    while (en_netInterface.hasMoreElements()) {
                        val ni = en_netInterface
                            .nextElement() as NetworkInterface
                        val en_ip = ni.inetAddresses
                        while (en_ip.hasMoreElements()) {
                            ip = en_ip.nextElement()
                            ip = if (!ip.isLoopbackAddress
                                && ip.hostAddress.indexOf(":") == -1
                            ) break else null
                        }
                        if (ip != null) {
                            break
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
                return ip
            }
    }

    init {
        throw UnsupportedOperationException("u can't fuck me...")
    }


}