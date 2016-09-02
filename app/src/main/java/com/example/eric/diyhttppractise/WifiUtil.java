
/**
 *
 */
package com.example.eric.diyhttppractise;

import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * @author passing
 *
 */
public class WifiUtil
{

    /**
     * 配置wifi
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public static WifiConfiguration createWifiInfo(String SSID,
                                                   String Password, int Type, WifiManager wifiManager,boolean isnew)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID=SSID;

        WifiConfiguration tempConfig=null;
        if(isnew){
            config.SSID = "\"" + SSID + "\"";
            tempConfig= isExsits(SSID, wifiManager,true);
        }else {tempConfig = isExsits(SSID, wifiManager,false);}

            if (tempConfig != null) {
                wifiManager.removeNetwork(tempConfig.networkId);
            }


        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 判断wifi是否存在
     *
     * @param SSID
     * @param wifiManager
     * @return
     */
    private static WifiConfiguration isExsits(String SSID,
                                              WifiManager wifiManager,boolean isnew)
    {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        if (isnew){
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\""+SSID+"\"")) {//已经恢复这里SSID的引号~
                    return existingConfig;
                }
            }
        }else {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 转换IP地址
     *
     * @param i
     * @return
     */
    public static String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }
}