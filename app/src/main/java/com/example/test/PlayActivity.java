package com.example.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.model.Control;
import com.example.test.model.ManualControl;
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

    private Switch btn_power;

//    public static String SERVER_ADRESS = "http://www.dogfriends.site";
    public static String SERVER_ADRESS = "http://192.168.1.3:8082/";

    private static Retrofit retrofit = null;
    ApiService apiService;

    int stateAutoControl = 0;
    int stateManualControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        temp = findViewById(R.id.temp_result);
        humi = findViewById(R.id.humi_result);
        state = findViewById(R.id.state_auto);

        findViewById(R.id.btn_refresh).setOnClickListener(this);
        findViewById(R.id.btn_up).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);

        btn_power = findViewById(R.id.btn_power);
        btn_power.setOnClickListener(this);

        //retrofit 생성 (url주소 빌드 한다)
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

            //  새로고침
           case R.id.btn_refresh:
                getTempHumi();
                break;

           // 자동제어 ON/OFF
           case R.id.btn_power:
               sendAuto();
               break;

           // 전진
           case R.id.btn_up:
                sendManual(3);
                break;

           // 후진
           case R.id.btn_down:
                sendManual(4);
                break;

           // 좌회전
           case R.id.btn_left:
                sendManual(5);
                break;

           // 우회전
           case R.id.btn_right:
                sendManual(6);
                break;
        }
    }


/////////////////////////////////////// 서비스 인터페이스 //////////////////////////////////////

    public interface ApiService {

        // 온습도
        @GET("view")
        Call<TempHumi> getTempHumi();

        // 자동제어 on/off
        @GET("android/setpower/")
        Call<Control> getAutoControl(@Query("power") int power);

        // 수동제어 (전, 후, 좌, 우)
        @GET("android/setmanual/")
        Call<ManualControl> getManualControl(@Query("manual") int manual);

    }

///////////////////////////////////////// 수동제어 /////////////////////////////////////////////
    public void sendManual(int i){

        if(btn_power.isChecked()){ // 전원켜진상태
            btn_power.setChecked(false);        // 스위치버튼을 off로 전환
            Call<Control> sendControl = apiService.getAutoControl(0); // 수동제어가 되면 자동제어 강제 종료

            sendControl.enqueue(new Callback<Control>() {

                @Override
                public void onResponse(Call<Control> call, Response<Control> response) {
                    if(response.isSuccessful()) {
    //                        Control power = response.body();
    //                        state.setText(String.valueOf(power.getPower()));
                        state.setText("OFF");
                    }
                    if ( DEBUG ) Log.v ( "TAG", "수동제어를 위해 자동제어 종료 isSuccessful" );
                }

                @Override
                public void onFailure(Call<Control> call, Throwable t) {
                    if ( DEBUG ) Log.w ( "TAG", "수동제어를 위해 자동제어 종료 onFailure" );
                }
            });

            Toast.makeText(PlayActivity.this, "자동제어를 종료하고 수동제어를 시작합니다", Toast.LENGTH_SHORT).show();

        } else {

            stateManualControl = i; // 전:3 후:4 좌:5 우:6
            Call<ManualControl> sendControl = apiService.getManualControl(stateManualControl); //

            sendControl.enqueue(new Callback<ManualControl>() {

                @Override
                public void onResponse(Call<ManualControl> call, Response<ManualControl> response) {
                    if (response.isSuccessful()) {
                        if (DEBUG) Log.v("TAG", "Control isSuccessful" + stateManualControl);
                    }
                }

                @Override
                public void onFailure(Call<ManualControl> call, Throwable t) {
                    if (DEBUG) Log.w("TAG", "Control onFailure");
                }

            });
        }

//        Toast.makeText(PlayActivity.this, "시작합니다", Toast.LENGTH_SHORT).show();

    }



/////////////////////////////////////////// 자동제어 ///////////////////////////////////////////
    public void sendAuto(){

        if(btn_power.isChecked()){ // 자동제어시작

            stateAutoControl = 1;
            Call<Control> sendControl = apiService.getAutoControl(stateAutoControl); // Call<ResponseBody> sendControl = apiService.getAutoControl(1);

            sendControl.enqueue(new Callback<Control>() {

                @Override
                public void onResponse(Call<Control> call, Response<Control> response) {
                    if(response.isSuccessful()) {
//                        Control power = response.body();
//                        state.setText(String.valueOf(power.getPower()));
                        state.setText("ON");
                    }
                    if ( DEBUG ) Log.v ( "TAG", "시작 isSuccessful" );
                }

                @Override
                public void onFailure(Call<Control> call, Throwable t) {
                    if ( DEBUG ) Log.w ( "TAG", "시작 실패" );
                }
            });

            Toast.makeText(PlayActivity.this, "시작합니다", Toast.LENGTH_SHORT).show();

        } else {    // 자동제어종료

            stateAutoControl = 0;
            Call<Control> sendControl = apiService.getAutoControl(stateAutoControl);    // Call<ResponseBody> sendControl = apiService.getAutoControl(0);

            sendControl.enqueue(new Callback<Control>() {

                @Override
                public void onResponse(Call<Control> call, Response<Control> response) {
                    if(response.isSuccessful()) {
//                        Control power = response.body();
//                        state.setText(String.valueOf(power.getPower()));
                        state.setText("OFF");

                    }
                    if ( DEBUG ) Log.v ( "TAG", "종료 isSuccessful" );
                }

                @Override
                public void onFailure(Call<Control> call, Throwable t) {
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

////////////////////////////////// 설명 ///////////////////////////////////

/*
palyActivity 속에 구현된 기능들 :
- 온습도를 서버로부터 전달 받음
- 자동제어 ON / OFF를  서버로 보냄
- 수동제어 (전, 후, 좌, 우) 서버로 보냄

각 메서드 설명
onCreate :
- palyActivityr가 실행될때 무조건 실행 됨

onClick :
- swich을 사용하여 각각의 버튼 클릭시 해당 함수가 구현 되도록 함.

ApiService 인터페이스 :
- 어떤 방식으로 서버에 데이터를 보내고 받을지 적혀있음
- Call<모델클레스명>   모델 클래스는 왼쪽 파일 리스트 model파일에 담겨 있음

sendManual, sendAuto, getTempHumi:
- Call<Control> sendControl = apiService.getAutoControl(stateControl); -> 서버로 stateControl값을 보냄
  => Call<ResponseBody> sendControl = apiService.getAutoControl(1);  -> 위와 같은 것임 stateControl대신 숫자를 써도 됨. 숫자가 뭘 의미하는지 헷갈릴까봐 stateControl라는 이름의 변수를 만든것임
- sendControl.enqueue(new Callback<Control>() -> 서버로부터 데이터가 올 경우 받는 곳임. 성공적으로 받으면 public void onResponse 실패면 public void onFailure를 탄다
*/
