package com.example.eric.diyhttppractise;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArpDefence extends AppCompatActivity {
    private TextView tvShowArpStatus;
    private WifiManager wifiManager;
    private String presentBSSID;
    private String gateway;
    private String MacFromArpTable;
    private String resultToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arp_defence);

        tvShowArpStatus= (TextView) findViewById(R.id.tvShowArpStatus);
        wifiManager=myWifiManager.getWifiManagerInstance(getApplicationContext());

        presentBSSID=wifiManager.getConnectionInfo().getBSSID();
        gateway=WifiUtil.intToIp(wifiManager.getDhcpInfo().gateway);
        MacFromArpTable=do_exec(gateway,"cat /proc/net/arp");
        resultToShow=" gateway:"+gateway
                +"\n BSSID:"+presentBSSID
                +"\n gateway mac from arp-table:"+MacFromArpTable;
        if(presentBSSID.equals(MacFromArpTable)){resultToShow=resultToShow+"\n the two mac is equal,there is no arp-spoffing.";}
        tvShowArpStatus.setText(resultToShow);



    }
     String do_exec(String gateway,String cmd) {
        String s = "\n";
        Matcher matMac=null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";

                //使用正则表达式从结果字符串中抓取得我们想要的信息
                String regExIp = gateway.replaceAll(".","\\."); //匹配gateway地址
                String regExMac = "([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})"; //匹配gateway的Mac地址
                Pattern patIp = Pattern.compile(regExIp);
                Pattern patMac = Pattern.compile(regExMac);
                Matcher matIp = patIp.matcher(s);
                matMac = patMac.matcher(s);
                boolean rsIp = matIp.find();
                boolean rsMac=matMac.find();
                if(rsIp&&rsMac){
//                    if(){Toast.makeText(this,"we find the gataway in arp table:"+matIp.group()
//                            +" whose mac is:" +matMac.group(), Toast.LENGTH_LONG)
//                            .show();}
//                    else return "we cannot find the gatway:"+matIp.group()+"'s mac in arp table.";
                    Toast.makeText(this,"we find the gataway(ip:"+gateway+")'s mac in arp table:"
                            +matMac.group(), Toast.LENGTH_LONG)
                            .show();
                }
                //mat.toString()

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //text.setText(s);
        return matMac.group();
    }
}
