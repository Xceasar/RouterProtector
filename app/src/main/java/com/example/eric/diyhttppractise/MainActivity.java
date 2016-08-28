package com.example.eric.diyhttppractise;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private EditText et;
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
                startActivity(new Intent(MainActivity.this,AnotherMethod.class));
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //readNet("http://192.168.1.1");
                String newpass=et.getText().toString();
                readNet("http://192.168.1.1/userRpm/WlanSecurityRpm.htm?secType=3&pskSecOpt=2&pskCipher=3&pskSecret="+newpass+"&interval=3600&Save=%B1%A3+%B4%E6","1");

            }
        });

        findViewById(R.id.btnRebootRouter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rebootRouter("http://192.168.1.1/userRpm/SysRebootRpm.htm?Reboot=%D6%D8%C6%F4%C2%B7%D3%C9%C6%F7","2");
            }
        });
    }
    public void readNet(String address,String step)   {
        new AsyncTask<String,String,String>(){

            @Override
            protected String doInBackground(String... strings) {
               try {
                    URL url=new URL(strings[0]);
                    try {
//                        CookieManager cookieManager = new CookieManager();
//                        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//                        CookieHandler.setDefault(cookieManager);
//                        HttpCookie cookie = new HttpCookie("Authorization", "Basic%20YWRtaW46MzI2MzI2");
//                        cookie.setDomain("192.168.1.1");
//                        cookie.setPath("/");
//                        cookie.setVersion(0);
//                        try {
//                            cookieManager.getCookieStore().add(new URI("http://192.168.1.1/"), cookie);
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }



                        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Cookie","Authorization=Basic%20YWRtaW46YWRtaW4xMjM%3D; ChgPwdSubTag=");
                       // String choose=strings[1];
                        //if(choose.equals("1"))
                        //这里一使用if就报错,没找到原因!
                        {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/WlanSecurityRpm.htm");}
                        //if(strings[1].equals("2".toString())) {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/SysRebootRpm.htm");}

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
                //URLConnection connection=
                return null;

            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tv.setText(values[0]);
            }
        }.execute(address);
    }

    public void rebootRouter(String address,String step)   {
        new AsyncTask<String,String,String>(){

            @Override
            protected String doInBackground(String... strings) {
                try {
                    URL url=new URL(strings[0]);
                    try {
//                        CookieManager cookieManager = new CookieManager();
//                        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//                        CookieHandler.setDefault(cookieManager);
//                        HttpCookie cookie = new HttpCookie("Authorization", "Basic%20YWRtaW46MzI2MzI2");
//                        cookie.setDomain("192.168.1.1");
//                        cookie.setPath("/");
//                        cookie.setVersion(0);
//                        try {
//                            cookieManager.getCookieStore().add(new URI("http://192.168.1.1/"), cookie);
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }



                        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Cookie","Authorization=Basic%20YWRtaW46YWRtaW4xMjM%3D; ChgPwdSubTag=");
                        // String choose=strings[1];
                        //if(choose.equals("1"))
                        //这里一使用if就报错,没找到原因!
                        //{connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/WlanSecurityRpm.htm");}
                        //if(strings[1].equals("2".toString()))
                        {connection.setRequestProperty("Referer","http://192.168.1.1/userRpm/SysRebootRpm.htm");}

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
                //URLConnection connection=
                return null;

            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tv.setText(values[0]);
            }
        }.execute(address);
    }
}
