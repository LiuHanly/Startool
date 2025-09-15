package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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
     * @param context
     */
    public static void openWirelessSettings(Context context) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /**
     * @param context
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean is4G(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * @param context
     * @return {@code true}:
     * {@code false}:
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * @param context
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        int netType = NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            System.err.println("getTypeName: " + info.getTypeName());
            System.err.println("getSubtype: " + info.getSubtype());
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
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase("WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000")) {
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

    public static int getNetWorkType(Context context, String type) {
        int netType = NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo(context);

        if (info != null && info.isAvailable()) {
            switch (info.getTypeName()) {
                case "Ethernet":
                    netType = 1;
                    break;
                case "WIFI":
                    netType = 2;
                    break;
                case "MOBILE":
                    netType = 3;
                    break;

            }
        }
        return netType;
    }


    /**
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

    public static int getNetWorkTypeInt(Context context) {
        return getNetWorkType(context, "");
    }


    /**
     *
     * @return
     */
    /**
     * @return
     */
    public static String getMacAddress() {
        String mac = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
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
     * @return
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) break;
                    else ip = null;
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


    public static void getMacAddressTextView(TextView view) {
        String text = "";

        if (getMacAddress() != null) {
            text = getMacAddress();
        }

        view.setText(text.toLowerCase());
    }

    public static void getMacAddressTextView(TextView view, String test) {
        String text = "";
        view.setText(test);
    }

    public static String getEthernetMac() {
        String ethernetMac = null;
        try {
            NetworkInterface NIC = NetworkInterface.getByName("dummy0");
            Enumeration<NetworkInterface> Inrwo = NetworkInterface.getNetworkInterfaces();
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


    //获取网线的mac地址
    public static String getEthernetMacAddress() throws SocketException {

        try {
            // 获取所有网络接口的列表
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // 检查接口名是否包含"eth"（这通常是Linux系统的以太网接口名）
                System.out.println("intf name:" + intf.getName());

                try {
                    BufferedReader br1 = new BufferedReader(new FileReader("/sys/class/net/" + intf.getName() + "/address"));
                    String line = br1.readLine();
                    if (line != null) {

                        System.err.println("line: " + intf.getName() + "--address--" + line.trim());
                    }
                } catch (IOException e) {

                }

                byte[] mac = intf.getHardwareAddress();
                if (mac != null) {

                    System.out.format("MAC Address of %s is: %s%n", intf.getName(), formatMac(mac));
                } else {


                    System.out.println("No MAC address for " + intf.getName());
                }

                if (intf.getName().contains("eth0")) {

                }
            }
        } catch (SocketException e) {
            System.err.println("Error getting network interfaces: " + e.getMessage());
        }

        String macAddress = "02:00:00:00:00:00";
        try {
            // 高通  dummy0  /sys/class/net/dummy0/address
            //RK  /sys/class/net/eth0/address
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/eth0/address"));
            String line = br.readLine();
            if (line != null) {
                macAddress = line.trim();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    // 获取4G mac 地址
    public static String get4GMacAddress() {
        String macAddress = "02:00:00:00:00:00";

        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/eth0/address"));
            String line = br.readLine();
            if (line != null) {
                macAddress = line.trim();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;

    }

    //获取WiFi mac 地址
    public static String getWifiMacAddress() {
        try {
            // 获取所有网络接口的列表
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // 检查接口名是否包含"eth"（这通常是Linux系统的以太网接口名）
                System.out.println("intf name:" + intf.toString());

                byte[] mac = intf.getHardwareAddress();
                if (mac != null) {
                    System.out.format("MAC Address of %s is: %s%n", intf.getName(), formatMac(mac));
                } else {
                    System.out.println("No MAC address for " + intf.getName());
                }

                if (intf.getName().contains("eth0")) {

                }
            }
        } catch (SocketException e) {
            System.err.println("Error getting network interfaces: " + e.getMessage());
        }


        String macAddress = "02:00:00:00:00:00";
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            String line = br.readLine();
            if (line != null) {
                macAddress = line.trim();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    // 格式化MAC地址为常见的格式（例如：01:23:45:67:89:AB）
    private static String formatMac(byte[] mac) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            buf.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return buf.toString();
    }

    /**
     * 获取以太网Mac地址
     *
     * @return
     */
    public static String getMacAddress1() {
        String mac = null;
        try {
            InetAddress ip = getLocalInetAddress("");
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
     * 获取IP地址
     *
     * @return
     */
    public static InetAddress getLocalInetAddress(String ips) {
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


}