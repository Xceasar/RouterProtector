package com.example.eric.diyhttppractise;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceArpDetection extends Service {

    public Callback callback=null;
    public boolean isRun=true;

    private TextView tvShowArpStatus;
    private WifiManager wifiManager;
    private String presentBSSID;
    private String gateway;
    private String MacFromArpTable;
    private String resultToShow;
    private String checking="the arp detection is up";

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    public static interface Callback{
        void onDataChange(String Data,String running);
    }
    public ServiceArpDetection() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checking="the Arp-Detection is running";
        new Thread(){
            int i=0;
            @Override
            public void run() {
                super.run();
                while(isRun){
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
                                callback.onDataChange(resultToShow,checking);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //读取并比对
                    wifiManager=myWifiManager.getWifiManagerInstance(getApplicationContext());

                    presentBSSID=wifiManager.getConnectionInfo().getBSSID();
                    gateway=WifiUtil.intToIp(wifiManager.getDhcpInfo().gateway);
                    MacFromArpTable=do_exec(gateway,"cat /proc/net/arp");
                    resultToShow="\n gateway:"+gateway
                            +"\n BSSID:"+presentBSSID
                            +"\n gateway mac from arp-table:"+MacFromArpTable;
                    if(presentBSSID.equals(MacFromArpTable)){resultToShow=resultToShow+"\n the two mac is equal,there is no arp-spoffing.";}
                    else{resultToShow=resultToShow+"\n the two mac is NOT equal! There is  arp-spoofing in your network!!";}
                    i=0;
                    checking="the arp detection is up";

                }
                callback.onDataChange("the arp-spoofing detection is stoped.","the result of last scan:\n"+resultToShow);


            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //isRun=false;
        System.out.println("arp detection service onDestory!");
//        callback.onDataChange("the arp-spoofing detection is stoped.","the result of last scan:\n"+resultToShow);
        super.onDestroy();
    }

    class Binder extends android.os.Binder{

//        private static Binder binder;
        public ServiceArpDetection getServiceArpDetection(){
            return ServiceArpDetection.this;
        }
//        public ServiceArpDetection.Binder getBinderInstance(){
//
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
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
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("we find the gataway(ip:"+gateway+")'s mac in arp table:"
                           +matMac.group());
//
//                    Toast.makeText(this,"we find the gataway(ip:"+gateway+")'s mac in arp table:"
//                            +matMac.group(), Toast.LENGTH_LONG)
//                            .show();
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
