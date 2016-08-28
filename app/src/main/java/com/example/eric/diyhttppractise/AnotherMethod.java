package com.example.eric.diyhttppractise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AnotherMethod extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_method);
        textView= (TextView) findViewById(R.id.textView2);
        textView.setMovementMethod(new ScrollingMovementMethod());



    }

}
