package com.example.eric.diyhttppractise;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by eric on 16/9/1.
 */
public class myWifiManager {
    private myWifiManager(){

    }
    private static WifiManager wifiManager;
    public static WifiManager getWifiManagerInstance(Context context){
        if(wifiManager==null){
            wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager;
    }
}
