package com.example.eric.diyhttppractise;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommandExcuting extends Activity {
    TextView text;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_exec);

        text = (TextView) findViewById(R.id.text);

        Button btn_ls = (Button) findViewById(R.id.btn_ls);
        btn_ls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                do_exec("ls /mnt/sdcard");
            }
        });
        Button btn_cat = (Button) findViewById(R.id.btn_cat);
        btn_cat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                do_exec("cat /proc/net/route");
            }
        });

        Button btn_cat_arp = (Button) findViewById(R.id.btn_cat_arp);
        btn_cat_arp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                do_exec("cat /proc/net/arp");
            }
        });
        Button btn_rm = (Button) findViewById(R.id.btn_rm);
        btn_rm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                do_exec("rm /mnt/sdcard/1.jpg");
            }
        });
        Button btn_sh = (Button) findViewById(R.id.btn_sh);
        btn_sh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                do_exec("/system/bin/sh /mnt/sdcard/test.sh 123");
            }
        });
    }

   public String do_exec(String cmd) {
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
                String regExIp = "\\b(([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\b"; //匹配IP地址
                String regExMac = "([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})"; //匹配Mac地址
                Pattern patIp = Pattern.compile(regExIp);
                Pattern patMac = Pattern.compile(regExMac);
                Matcher matIp = patIp.matcher(s);
                matMac = patMac.matcher(s);
                boolean rsIp = matIp.find();
                boolean rsMac=matMac.find();
                if(rsIp&&rsMac){
                    Toast.makeText(this,"we find the ip addr:"+matIp.group()
                            +" whose mac is:" +matMac.group(), Toast.LENGTH_LONG)
                            .show();
                }
                //mat.toString()

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        text.setText(s);
        return matMac.group();
    }
}
