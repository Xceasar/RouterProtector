package com.example.eric.diyhttppractise;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.demo.R;
import com.example.eric.diyhttppractise.WifiUtil;


public class WifiActivity extends Activity implements OnClickListener {

    private Button scan_button;

    private TextView wifi_result_textview;

    private WifiManager wifiManager;

    private WifiInfo currentWifiInfo;// 当前所连接的wifi

    private List<ScanResult> wifiList;// wifi列表

    private String[] str;

    private int wifiIndex;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_layout);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        setupViews();
        initListener();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        openWifi();//打开了wifi,从而让后面的wifiManager获取到信息
        currentWifiInfo = wifiManager.getConnectionInfo();
        wifi_result_textview.setText(" 当前网络：" + currentWifiInfo.getSSID()//增加了显示的信息
                + "\n ip:" + WifiUtil.intToIp(currentWifiInfo.getIpAddress()) + "\n Frequency:" + currentWifiInfo.getFrequency() + "MHz"
                + "\n linkspeed:" + currentWifiInfo.getLinkSpeed() + "MBps" + "\n MAC:" + currentWifiInfo.getMacAddress() + "\n SupplicantState:"
                + currentWifiInfo.getSupplicantState().toString()

        );
        new ScanWifiThread().start();//
        super.onResume();
    }

    public void setupViews() {
        scan_button = (Button) findViewById(R.id.scan_button);
        wifi_result_textview = (TextView) findViewById(R.id.wifi_result_textview);
        //super.setupViews();
    }

    public void initListener() {
        scan_button.setOnClickListener(this);
        // super.initListener();
        //----------------------takecare-----------------
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                lookUpScan();
                break;
        }
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 扫描wifi线程
     *
     * @author passing
     */
    class ScanWifiThread extends Thread {

        @Override
        public void run() {
            while (true) {
                currentWifiInfo = wifiManager.getConnectionInfo();
                startScan();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * 扫描wifi
     */
    public void startScan() {
        wifiManager.startScan();//扫描
        // 获取扫描结果
        wifiList = wifiManager.getScanResults();
        str = new String[wifiList.size()];
        String tempStr = null;
        for (int i = 0; i < wifiList.size(); i++) {
            tempStr = wifiList.get(i).SSID;//获取ssid信息
            if (null != currentWifiInfo
                    && tempStr.equals(currentWifiInfo.getSSID()))//判断是否是当前所连接的wifi
            {
                tempStr = tempStr + "(已连接)";
            }
            str[i] = tempStr;//装入数据到字符串数组
        }
    }

    /**
     * 弹出框 查看扫描结果
     */
    public void lookUpScan() {
        Builder builder = new Builder(WifiActivity.this);
        builder.setTitle("Availible Wifi List");
        builder.setItems(str, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                wifiIndex = which;
                handler.sendEmptyMessage(3);
            }
        });
        builder.show();
    }

    /**
     * 获取网络ip地址
     *
     * @author passing
     */
    class RefreshSsidThread extends Thread {

        @Override
        public void run() {
            boolean flag = true;
            while (flag) {
                currentWifiInfo = wifiManager.getConnectionInfo();
                if (null != currentWifiInfo.getSSID()
                        && 0 != currentWifiInfo.getIpAddress()) {
                    flag = false;
                }
            }
            handler.sendEmptyMessage(4);
            super.run();
        }
    }

    /**
     * 连接网络
     *
     * @param index
     * @param password
     */
    public void connetionConfiguration(int index, String password) {
        progressDialog = ProgressDialog.show(WifiActivity.this, "正在连接...",
                "请稍候...");
        new ConnectWifiThread().execute(index + "", password);
    }

    /**
     * 连接wifi
     *
     * @author passing
     */
    class ConnectWifiThread extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            int index = Integer.parseInt(params[0]);
            if (index > wifiList.size()) {
                return null;
            }
            // 连接配置好指定ID的网络
            WifiConfiguration config = WifiUtil.createWifiInfo(
                    wifiList.get(index).SSID, params[1], 3, wifiManager);

            int networkId = wifiManager.addNetwork(config);
            if (null != config) {
                wifiManager.enableNetwork(networkId, true);
                return wifiList.get(index).SSID;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
            if (null != result) {
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(1);
            }
            super.onPostExecute(result);
        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    wifi_result_textview.setText("正在获取ip地址...");
                    new RefreshSsidThread().start();
                    break;
                case 1:
                    Toast.makeText(WifiActivity.this, "连接失败！", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 3:
                    View layout = LayoutInflater.from(WifiActivity.this).inflate(
                            R.layout.custom_dialog_layout, null);
                    Builder builder = new Builder(WifiActivity.this);
                    builder.setTitle("请输入密码").setView(layout);
                    final EditText passowrdText = (EditText) layout
                            .findViewById(R.id.password_edittext);
                    builder.setPositiveButton("连接",
                            new DialogInterface.OnClickListener()

                            {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    connetionConfiguration(wifiIndex, passowrdText
                                            .getText().toString());
                                }
                            }).show();
                    break;
                case 4:
                    Toast.makeText(WifiActivity.this, "连接成功！", Toast.LENGTH_SHORT)
                            .show();
                    wifi_result_textview.setText("当前网络："
                            + currentWifiInfo.getSSID() + " ip:"
                            + WifiUtil.intToIp(currentWifiInfo.getIpAddress()));
                    break;
            }
            super.handleMessage(msg);
        }

    };

}

//3、辅助类：WifiUtil.java

