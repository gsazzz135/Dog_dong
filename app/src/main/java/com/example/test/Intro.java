package com.example.test;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Intro 화면에서 Main화면으로 자동으로 이동
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent transition = new Intent(Intro.this, MainActivity.class);
                startActivity(transition);
                finish();
            }
        }, 3000);       // 3000 = 3초
    }
}
