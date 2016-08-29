package com.example.eric.diyhttppractise;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.eric.diyhttppractise.WifiActivity;
public class TEST extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        View layout = LayoutInflater.from(this).inflate(
                            R.layout.custom_dialog_layout, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("请输入密码").setView(layout);
                    final EditText passowrdText = (EditText) layout
                            .findViewById(R.id.password_edittext);
                    builder.setPositiveButton("连接",
                            new DialogInterface.OnClickListener()
                            {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {
//                                    connetionConfiguration(wifiIndex, passowrdText
//                                            .getText().toString());
                                    System.out.println("success!!!!!!");
                                }
                            }).show();
    }
}
