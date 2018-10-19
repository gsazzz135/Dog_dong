package com.example.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.model.Power;
import com.example.test.model.TempHumi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.example.test.BuildConfig.DEBUG;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView temp;
    private TextView humi;
    private TextView state;

    private Button btn_refresh;

    private Switch btn_power;

    public static String SERVER_ADRESS = "http://www.dogfriends.site/";

    private static Retrofit retrofit = null;
    ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        temp = findViewById(R.id.temp_result);
        humi = findViewById(R.id.humi_result);
        state = findViewById(R.id.state_auto);

        findViewById(R.id.btn_refresh).setOnClickListener(this);

        btn_power = findViewById(R.id.btn_power);
        btn_power.setOnClickListener(this);

        //retrofit 생성 (url주소 빌드 한다
        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
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
               sendPower();
               break;
        }
    }


/////////////////////////////////////// 서비스 인터페이스 //////////////////////////////////////

    public interface ApiService {

        @GET("view")
        Call<TempHumi> getTempHumi();

        @GET("android/setpower/")
        Call<Power> getComment(@Query("power") int power);

    }

/////////////////////////////////////////// 자동제어 ///////////////////////////////////////////
    public void sendPower(){

        int statePower = 0;

        if(btn_power.isChecked()){ // 전원켜짐

            statePower = 1;
            Call<Power> comment = apiService.getComment(statePower); // Call<ResponseBody> comment = apiService.getComment(1);

            comment.enqueue(new Callback<Power>() {

                @Override
                public void onResponse(Call<Power> call, Response<Power> response) {
                    if(response.isSuccessful()) {
//                        Power power = response.body();
//                        state.setText(String.valueOf(power.getPower()));
                        state.setText("ON");
                    }
                    if ( DEBUG ) Log.v ( "TAG", "시작 isSuccessful" );
                }

                @Override
                public void onFailure(Call<Power> call, Throwable t) {
                    if ( DEBUG ) Log.w ( "TAG", "시작 실패" );
                }
            });

            Toast.makeText(PlayActivity.this, "시작합니다", Toast.LENGTH_SHORT).show();

        } else {    // 전원 꺼짐

            statePower = 0;
            Call<Power> comment = apiService.getComment(statePower);    // Call<ResponseBody> comment = apiService.getComment(0);

            comment.enqueue(new Callback<Power>() {

                @Override
                public void onResponse(Call<Power> call, Response<Power> response) {
                    if(response.isSuccessful()) {
//                        Power power = response.body();
//                        state.setText(String.valueOf(power.getPower()));
                        state.setText("OFF");

                    }
                    if ( DEBUG ) Log.v ( "TAG", "종료 isSuccessful" );
                }

                @Override
                public void onFailure(Call<Power> call, Throwable t) {
                    if ( DEBUG ) Log.w ( "TAG", "종료 실패" );
                }
            });

            Toast.makeText(PlayActivity.this, "종료합니다", Toast.LENGTH_SHORT).show();

        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////// 온습도 ///////////////////////////////////////////////
    private void getTempHumi() {

        apiService.getTempHumi().enqueue(new Callback<TempHumi>() {           //enqueue() : 비동기적으로 요청을 보내고 응답이 다시 올 때 콜백으로 앱에 알림(빽그라운드스레드에서 실행 처리)
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

///////////////////////////////////////////////////////////////////////////////////////////////////


}


