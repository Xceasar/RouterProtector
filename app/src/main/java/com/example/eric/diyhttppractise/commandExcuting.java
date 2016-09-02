package com.example.eric.diyhttppractise;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eric.diyhttppractise.WifiActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class commandExcuting extends Activity {
    TextView text;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_exec);

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

    String do_exec(String cmd) {
        String s = "\n";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        text.setText(s);
        return cmd;
    }
}
