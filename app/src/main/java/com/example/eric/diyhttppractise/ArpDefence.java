package com.example.eric.diyhttppractise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.eric.diyhttppractise.ServiceArpDetection;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArpDefence extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    private TextView tvShowArpStatus;
    private TextView tvShowRunning;
    private WifiManager wifiManager;
    private String presentBSSID;
    private String gateway;
    private String MacFromArpTable;
    private String resultToShow;
    private Intent intent;
    ServiceArpDetection.Binder binder=null;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvShowRunning.setText(msg.getData().getString("running"));
            tvShowArpStatus.setText(msg.getData().getString("data"));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arp_defence);

        intent=new Intent(this,ServiceArpDetection.class);
        tvShowArpStatus= (TextView) findViewById(R.id.tvShowArpStatus);
        tvShowRunning= (TextView) findViewById(R.id.tvShowRunning);

        findViewById(R.id.btnBindArpDetection).setOnClickListener(this);
        findViewById(R.id.btnUnbindArpDetection).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnBindArpDetection:
                //先启动服务
                startService(intent);
                //之后绑定
                if(binder==null)
                {bindService(intent,this, Context.BIND_AUTO_CREATE);}

                break;
            case R.id.btnUnbindArpDetection:
                if(binder!=null){
                    unbindService(this);
                    binder=null;
                }
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        //
        if(binder==null){
            binder= (ServiceArpDetection.Binder) iBinder;
            binder.getServiceArpDetection().setCallback(new ServiceArpDetection.Callback() {
                @Override
                public void onDataChange(String Data, String running) {
                    Message msg=new Message();
                    Bundle b=new Bundle();
                    b.putString("data",Data);
                    b.putString("running",running);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            });
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        binder=null;
        System.out.println("Sercice disconnected!");

    }


    @Override
    protected void onPause() {
        super.onPause();
        //在这里解绑service,防止其被销毁
        if(binder!=null){
            unbindService(this);
            binder=null;
        }
    }
}
