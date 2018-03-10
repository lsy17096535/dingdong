package com.intexh.bidong.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;



public class NetworkUtils {

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 获取当前网络连接的类型信息
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }


    /**
     * 获取当前的网络状态 ：没有网络 0：WIFI网络 1：4G网络 2：3G网络 3：2G网络 4：未知网络类型5：
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            netType = handlerNetType(context,nSubType);
        }
        return netType;
    }

    private static int handlerNetType(Context context, int nSubType) {
        int netType;
        switch (nSubType){
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                netType = 4;    //(2G)
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
            case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
            case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                netType = 3;    //(3G)
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                netType = 4;    //(4G)
                break;
            default:
                netType = 5;
            break;
        }

        return netType;
    }

    /** 
     * 将ip的整数形式转换成ip形式 
     *  
     * @param ipInt 
     * @return 
     */  
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();  
    }  
  
    /** 
     * 获取当前ip地址 
     *  
     * @param context 
     * @return 
     */  
    public static String getLocalIpAddress(Context context) {
        String ip = null;
        try {  
            // for (Enumeration<NetworkInterface> en = NetworkInterface  
            // .getNetworkInterfaces(); en.hasMoreElements();) {  
            // NetworkInterface intf = en.nextElement();  
            // for (Enumeration<InetAddress> enumIpAddr = intf  
            // .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
            // InetAddress inetAddress = enumIpAddr.nextElement();  
            // if (!inetAddress.isLoopbackAddress()) {  
            // return inetAddress.getHostAddress().toString();  
            // }  
            // }  
            // }  
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            ip =  int2ip(i);
        } catch (Exception ex) {
//            LogCatUtil.e("GET NET IP ADDRESS ERROR: ",(" 获取IP出错!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage()));
//            return null;
        }
         return ip;
    }

    //------------------------------------------------------------------------------------
  /*  case TelephonyManager.NETWORK_TYPE_1xRTT:
            return 4; // ~ 50-100 kbps
    case TelephonyManager.NETWORK_TYPE_CDMA:
            return 4; // ~ 14-64 kbps
    case TelephonyManager.NETWORK_TYPE_EDGE:
            return 4; // ~ 50-100 kbps
    case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return 3; // ~ 400-1000 kbps  ( 3G )电信
    case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return 3; // ~ 600-1400 kbps (3.5G)电信 属于3G过渡
    case TelephonyManager.NETWORK_TYPE_GPRS:
            return 4; // ~ 100 kbps
    case TelephonyManager.NETWORK_TYPE_HSDPA:
            return 3; // ~ 2-14 Mbps (3.5G )
    case TelephonyManager.NETWORK_TYPE_HSPA:
            return 3; // ~ 700-1700 kbps ( 3G )联通
    case TelephonyManager.NETWORK_TYPE_HSUPA:
            return 3; // ~ 1-23 Mbps ( 3.5G )
    case TelephonyManager.NETWORK_TYPE_UMTS:
            return 3; // ~ 400-7000 kbps (3G)联通
    case TelephonyManager.NETWORK_TYPE_EHRPD:
            return 3; // ~ 1-2 Mbps 3G(3G到4G的升级产物)
    case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return 3; // ~ 5 Mbps
    case TelephonyManager.NETWORK_TYPE_HSPAP:
            return 3; // ~ 10-20 Mbps ( 3G )
    case TelephonyManager.NETWORK_TYPE_IDEN:
            return 4; // ~25 kbps
    case TelephonyManager.NETWORK_TYPE_LTE:
            return 2; // ~ 10+ Mbps  (4G)
    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return 0; //没有网络
    default:
            return 5; //未知网络*/
}  