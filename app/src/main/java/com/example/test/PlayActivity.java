package com.example.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.model.Controll;
import com.example.test.model.TempHumi;

import java.io.IOException;
import java.util.ResourceBundle;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView temp;
    private TextView humi;

    private Button btn_refresh;

    private Switch btn_power;

    Retrofit retrofit;

    String statePower;

    public static String SERVER_ADRESS = "http://www.dogfriends.site/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        temp = findViewById(R.id.temp_result);
        humi = findViewById(R.id.humi_result);

        findViewById(R.id.btn_refresh).setOnClickListener(this);

        btn_power = findViewById(R.id.btn_power);
        btn_power.setOnClickListener(this);
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

           case R.id.btn_power:
               checkPower();
               break;
        }
    }

    private  void checkPower(){

        statePower = String.valueOf(btn_power.isChecked());

        if(btn_power.isChecked()){
            Toast.makeText(PlayActivity.this, "DOGFRIENDS를 시작합니다", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(PlayActivity.this, "DOGFRIENDS를 종료합니다", Toast.LENGTH_SHORT).show();

        }

    }

    private void setControll(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ControllService controllService = retrofit.create(ControllService.class);
        String json = "";

        if(statePower == "true") {
            json = "{ " + statePower + "on" + " }";
        } else {
            json = "{ " + statePower + "off" + " }";
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        controllService.postPower(requestBody).enqueue(new Callback<Controll>(){
            @Override
            public void  onResponse(Call<Controll> call, Response<Controll> response){
                try {
                    Log.d("RetrofitTest", response.body().toString());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            @Override
            public  void onFailure(Call<Controll> call, Throwable t){

            }

        });

    }



    private void getTempHumi() {
        /*함수로 뺄 예정*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TempHumiService tempHumiService = retrofit.create(TempHumiService.class);
        tempHumiService.getTempHumi().enqueue(new Callback<TempHumi>() {
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

            }
        });
    }
    public interface TempHumiService {
        @GET("view")
        Call<TempHumi> getTempHumi();
    }

    public interface ControllService{
        @GET("ctrl/{state}")
        Call<Controll> getPower();

        @FormUrlEncoded
        @POST("ctrl/{state}")
        Call<Controll> postPower(@Body RequestBody requestBody);
    }

}


