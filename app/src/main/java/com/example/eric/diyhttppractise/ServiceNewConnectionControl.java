package com.example.eric.diyhttppractise;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eric on 16/9/16.
 */
public class ServiceNewConnectionControl extends Service {

    public Callback callback=null;
    public boolean isRun=true;

    public String state="1111111";
    public String newMac=null;
    public String checking="the connection detection is up";

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public Callback getCallback() {
        return callback;
    }

    public static interface Callback{
        void onDataChange(String state,String checking,String NewMac);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder{
        public ServiceNewConnectionControl getServiceNewConnectionControl(){return ServiceNewConnectionControl.this;}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(){
            @Override
            public void run() {
                super.run();
                int i=0;
                while (isRun){
                    for(;i<3;i++){
                        try {
                            //休眠5s并输出一个个点点~
                            if(!isRun){
                                break;
                            }
                            sleep(1000);

                            System.out.println(i);//测试服务是否被销毁

                            checking+='.';
                            if(callback!=null){
                                callback.onDataChange(state,checking,newMac);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                    //读取并比对
//                    wifiManager=myWifiManager.getWifiManagerInstance(getApplicationContext());
//
//                    presentBSSID=wifiManager.getConnectionInfo().getBSSID();
//                    gateway=WifiUtil.intToIp(wifiManager.getDhcpInfo().gateway);
//                    MacFromArpTable=do_exec(gateway,"cat /proc/net/arp");
//                    resultToShow="\n gateway:"+gateway
//                            +"\n BSSID:"+presentBSSID
//                            +"\n gateway mac from arp-table:"+MacFromArpTable;
//                    if(presentBSSID.equals(MacFromArpTable)){resultToShow=resultToShow+"\n the two mac is equal,there is no arp-spoffing.";}
//                    else{resultToShow=resultToShow+"\n the two mac is NOT equal! There is  arp-spoofing in your network!!";}
//                    i=0;

                    //获取当前在线的主机mac信息
                    circuleGetCurrentMacInfo("http://192.168.1.1/userRpm/WlanStationRpm.htm?Page=1","http://192.168.1.1/userRpm/WlanStationRpm.htm?Page=1");
                    checking="the connection detection is up";

                }
                callback.onDataChange("","the service is stopped.","");

            }

        }.run();
    }
    public static String[] circuleGetCurrentMacInfo(String urlAddress,String referer) {
        String[] result = null;
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlAddress);
            connection = null;
            connection = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Cookie", "Authorization=Basic%20YWRtaW46YWRtaW4xMjM%3D; ChgPwdSubTag=");
        connection.setRequestProperty("Referer", referer);
        InputStream is = null;
        try {
            is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "gb2312");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            String s="\n";
            Matcher matMac=null;
            while ((line = br.readLine()) != null) {
                s += line + "\n";

                //使用正则表达式从结果字符串中抓取得我们想要的信息
                String regExMac = "([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})"; //匹配Mac地址
                Pattern patMac = Pattern.compile(regExMac);
                matMac = patMac.matcher(s);
                boolean rsMac=matMac.find();
                if(rsMac){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("we find the mac:"
                            +matMac.group());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }





        return result;
    }
}
