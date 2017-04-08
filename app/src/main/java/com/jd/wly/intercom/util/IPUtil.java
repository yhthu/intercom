package com.jd.wly.intercom.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.jd.wly.intercom.app.App;

public class IPUtil {
    public static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." + ((ipAdress >> 8) & 0xFF) + "." + ((ipAdress >> 16) & 0xFF) + "."
                + (ipAdress >> 24 & 0xFF);
    }

    //获取本地IP函数
    public static String getLocalIPAddress() {
        //获取wifi服务  
        WifiManager wifiManager = (WifiManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启  
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return formatIpAddress(ipAddress);
    }
}
