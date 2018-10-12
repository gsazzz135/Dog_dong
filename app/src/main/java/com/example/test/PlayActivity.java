package com.example.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView temp;
    private TextView humi;
    private Button btn_refresh;

    Retrofit retrofit;
    RetroService retroService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        temp = findViewById(R.id.temp_result);
        humi = findViewById(R.id.humi_result);

        findViewById(R.id.btn_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btnGuest:
//                startActivity(new Intent(this, ReplyActivity.class));
//                finish();
//                break;

           case R.id.btn_refresh:
                getTempHumi();
                break;
        }
    }

    private void getTempHumi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.dogfriends.site/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetroService service = retrofit.create(RetroService.class);
        service.getTempHumi().enqueue(new Callback<TempHumi>() {
            @Override
            public void onResponse(Call<TempHumi> call, Response<TempHumi> response) {
                if(response.isSuccessful()) {
                    TempHumi tempHumi = response.body();
                    Toast.makeText(getApplicationContext(), "새로고침", Toast.LENGTH_LONG).show();
                    temp.setText(String.valueOf(tempHumi.getTemp()));
                    humi.setText(String.valueOf(tempHumi.getHumi()));
                }
            }

            @Override
            public void onFailure(Call<TempHumi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }
    public interface RetroService {

        static final String DOMAIN_URL = "";

        @GET("view")
        Call<TempHumi> getTempHumi();
    }
}
