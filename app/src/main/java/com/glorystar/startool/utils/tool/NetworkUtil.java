package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtil {

    private NetworkUtil() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    public static final int NETWORK_WIFI = 1; // wifi network
    public static final int NETWORK_4G = 4; // "4G" networks
    public static final int NETWORK_3G = 3; // "3G" networks
    public static final int NETWORK_2G = 2; // "2G" networks
    public static final int NETWORK_UNKNOWN = 5; // unknown network
    public static final int NETWORK_NO = -1; // no network
    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN = 18;

    /**
     *
     * @param context
     */
    public static void openWirelessSettings(Context context) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            context.startActivity(new Intent(
                    android.provider.Settings.ACTION_SETTINGS));
        } else {
            context.startActivity(new Intent(
                    android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /**
     *
     * @param context
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     *
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     *
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     *
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean is4G(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     *
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     *
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        int netType = NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI网络
                netType = NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NETWORK_2G;
                        break;
                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NETWORK_3G;
                        break;
                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NETWORK_4G;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NETWORK_3G;
                        } else {
                            netType = NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getNetWorkTypeName(Context context) {
        switch (getNetWorkType(context)) {
            case NETWORK_WIFI:
                return "NETWORK_WIFI";
            case NETWORK_4G:
                return "NETWORK_4G";
            case NETWORK_3G:
                return "NETWORK_3G";
            case NETWORK_2G:
                return "NETWORK_2G";
            case NETWORK_NO:
                return "NETWORK_NO";
            default:
                return "NETWORK_UNKNOWN";
        }
    }

    /**
     *
     * @return
     */
    /**
     *
     * @return
     */
    public static String getMacAddress() {
        String mac = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append('-');
                }

                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            mac = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();


        }

        return mac;
    }


    /**
     *
     * @return
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    public static  void getMacAddressTextView(TextView view ){
        String  text= "";

        if (getMacAddress()  !=null ){
            text = getMacAddress();
        }

        view.setText(text.toLowerCase());
    }

    public static String getEthernetMac(){
        String ethernetMac = null;
        try {
            NetworkInterface NIC = NetworkInterface.getByName("eth0");
            byte[] buf = NIC.getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                if (i != 0) {
                    buffer.append('-');
                }

                String str = Integer.toHexString(buf[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            ethernetMac = buffer.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ethernetMac;
    }
}