package com.example.eric.diyhttppractise;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private EditText et;
    private String newPassword;
    private WifiManager wifiManager;
    private WifiInfo currentWifiInfo;
    private String ssid;
    private int wifiIndex;
    private List<ScanResult> wifiList;
    private List<WifiConfiguration> wifiConfigurationlist;
    private ClipboardManager myClipboard;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et= (EditText) findViewById(R.id.newPass);

        tv= (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.btnAnother).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,WifiActivity.class));
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //readNet("http://192.168.1.1");
                newPassword=et.getText().toString();
                ssid=currentWifiInfo.getSSID();
                readNet("http://192.168.1.1/userRpm/WlanSecurityRpm.htm?secType=3&pskSecOpt=2&pskCipher=3&pskSecret="+newPassword+"&interval=3600&Save=%B1%A3+%B4%E6","1");

            }
        });

        findViewById(R.id.btnRebootRouter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssid=currentWifiInfo.getSSID();
                //重启路由器
                readNet("http://192.168.1.1/userRpm/SysRebootRpm.htm?Reboot=%D6%D8%C6%F4%C2%B7%D3%C9%C6%F7","2");
                //更新路由器连接信息并自动重连
            }
        });

        findViewById(R.id.btnAddMac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNet("http://192.168.1.1/userRpm/WlanMacFilterRpm.htm?Mac=00-1D-0F-11-22-33&Desc=&entryEnabled=1&Changed=0&SelIndex=0&Page=1&Save=%B1%A3+%B4%E67","3");
            }
        });

        findViewById(R.id.btnReconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPassword=et.getText().toString();
                //ssid="\""+"Kaze"+"\"";
                tv.setText("当前要修改密码的wifi网络ssid为:"+ssid);
                new UpdateWifiConfigration().execute(ssid,newPassword);
            }
        });

        findViewById(R.id.btnStartCommand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CommandExcuting.class));
            }
        });

        //复制密码到剪切板:
        findViewById(R.id.btnCopyPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                String text =et.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
            }
        });

        findViewById(R.id.btnAntiArpSpoofing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ArpDefence.class));
            }
        });

        findViewById(R.id.btnDelMac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNet("http://192.168.1.1/userRpm/WlanMacFilterRpm.htm?Del=7&Page=1","4");
            }
        });

        wifiManager=myWifiManager.getWifiManagerInstance(getApplicationContext());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        currentWifiInfo = wifiManager.getConnectionInfo();

        wifiConfigurationlist=wifiManager.getConfiguredNetworks();

        tv.setText("wifi已连接..."

                +"\n 当前网络：" + currentWifiInfo.getSSID()//增加了显示的信息
                + "\n ip:" + WifiUtil.intToIp(currentWifiInfo.getIpAddress())
                + "\n Frequency:" + currentWifiInfo.getFrequency() + "MHz"
                + "\n linkspeed:" + currentWifiInfo.getLinkSpeed() + "MBps"
                + "\n MAC:" + currentWifiInfo.getMacAddress()
                + "\n BSSID:" +currentWifiInfo.getBSSID()
                + "\n SupplicantState:" + currentWifiInfo.getSupplicantState().toString()
                + "\n DNS1:"+ WifiUtil.intToIp(wifiManager.getDhcpInfo().dns1)
                + "\n DNS2:"+ WifiUtil.intToIp(wifiManager.getDhcpInfo().dns2)
                + "\n gateway:"+ WifiUtil.intToIp(wifiManager.getDhcpInfo().gateway)
                + "\n netmask:"+ WifiUtil.intToIp(wifiManager.getDhcpInfo().netmask)
                + "\n server address:"+ WifiUtil.intToIp(wifiManager.getDhcpInfo().serverAddress)
                +" \n networkId of get(0):"+ wifiConfigurationlist.get(0).networkId
        );
        ssid=currentWifiInfo.getSSID();
        newPassword=et.getText().toString();
    }

    public void readNet(String address, String step)   {
        new AsyncTask<String,String,String>(){

            @Override
            protected String doInBackground(String... strings) {
               try {
                    URL url=new URL(strings[0]);
                    try {

                        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Cookie","Authorization=Basic%20YWRtaW46YWRtaW4xMjM%3D; ChgPwdSubTag=");
                        String choose=strings[1];

                        //1-修改密码:
                        if(choose.equals("1")) {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/WlanSecurityRpm.htm");}
                        //2-重启路由器:
                        if(choose.equals("2")) {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/SysRebootRpm.htm");}
                        //添加mac
                        if(choose.equals("3")) {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/WlanMacFilterRpm.htm?Add=Add&Page=1");}
                        //删除mac
                        if(choose.equals("4")) {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/WlanMacFilterRpm.htm");}
                        //由于并没有POST数据,这里先注释掉
//                        connection.setDoOutput(true);
//                        connection.setRequestMethod("POST");
//                        connection.setChunkedStreamingMode(0);
//                        OutputStreamWriter osw=new OutputStreamWriter(connection.getOutputStream(),"utf-8");
//                        BufferedWriter bw=new BufferedWriter(osw);
//                        bw.write("");
//                        bw.flush();

                        InputStream is=connection.getInputStream();
                        InputStreamReader isr=new InputStreamReader(is,"gb2312");
                        BufferedReader br=new BufferedReader(isr);
                        String result="result:";
                        while(br.readLine()!=null){
                            result=result+br.readLine();
                        }
//                        byte[] b=new byte[100];
//                        is.read(b,0,50);
//                        String sss=new String(b,"utf-8");

                        publishProgress(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tv.setText(values[0]);
            }
        }.execute(address,step);//这里注意不要少传参数了
    }
public class UpdateWifiConfigration extends AsyncTask<String,WifiConfiguration,String>{
    @Override
    protected String doInBackground(String... strings) {
        wifiManager.startScan();//扫描
        // 获取扫描结果SSID到字符串数组中
        wifiList = wifiManager.getScanResults();
        int index=0;
        String ssid = strings[0];//获取当前网络的ssid


        // 连接配置好指定ID的网络
               WifiConfiguration config = WifiUtil.createWifiInfo(
               ssid, strings[1], 3, wifiManager,false);
        wifiManager.startScan();//扫描

        String result=null;
        wifiConfigurationlist=wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : wifiConfigurationlist) {
            if (existingConfig.SSID.equals(ssid)) {
                result=existingConfig.SSID;
                //config.networkId=existingConfig.networkId;
                break;
            }
        }


        //config.networkId=wifiConfigurationlist.get(index).networkId;
        publishProgress(config);

        int networkId = wifiManager.addNetwork(config);//这条语句执行完后,就重新连接上了
        //publishProgress(networkId);

        if (null != config) {
            for (WifiConfiguration existingConfig : wifiConfigurationlist) {
                if (existingConfig.SSID.equals(ssid)) {
                    result=existingConfig.SSID;
                    //networkId=existingConfig.networkId;
                    break;
                }
            }
            boolean ifSucceed=wifiManager.enableNetwork(networkId, true);

            System.out.println(result);
            return ssid;
        }
        return null;

    }

    @Override
    protected void onProgressUpdate(WifiConfiguration... values) {
        super.onProgressUpdate(values);
        tv.setText(" 要修改的ssid:"+values[0].SSID
        +"\n 密码:"+values[0].preSharedKey
        +"\n 当前ssid的networkId:"+values[0].networkId
        );

    }

}

}
