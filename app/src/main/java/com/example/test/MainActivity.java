package com.example.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;





public class MainActivity extends AppCompatActivity {
    //버튼 변수 선언
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //자동모드 페이지 접속
        button1 =  (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intentAuto = new Intent(MainActivity.this, AutoFullscreenActivity.class);
                startActivity(intentAuto);
            }
        });

        //수동모드 페이지 접속
        button2 =  (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intentManual = new Intent(MainActivity.this, ManualFullscreenActivity.class);
                startActivity(intentManual);
            }
        });

        //카메라모드 접속
        button3 =  (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intentCamera = new Intent(MainActivity.this, CameraFullscreenActivity.class);
                startActivity(intentCamera);
            }
        });

        //로그인 웹페이지 열기(나중에 로그인 웹 하면 띄우고 그 안에서 회원가입페이지 띄울예정)
        button4 =  (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        // 블루투스 연결하기 페이지
        button5 =  (Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intentConnect = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(intentConnect);
            }
        });

//        버튼 클릭시 웹페이지 열림. 나중에 회원가입페이지 열때 사용 예정
//        button4 = (Button)findViewById(R.id.button4);
//        button4.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent loginpage = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
//                startActivity(loginpage);
//            }
//        });
    }
}
