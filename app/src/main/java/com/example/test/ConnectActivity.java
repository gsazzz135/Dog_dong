package com.example.test;

        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Set;
        import java.util.UUID;

        import android.app.AlertDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;

        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.example.test.R.*;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;

    BluetoothDevice mRemoteDevice;

    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter =  '\n';

    List<String> serchListItems;

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    EditText mEditReceive, mEditSend;
    Button mButtonSend;


    // 1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_connect);


        mEditReceive = (EditText)findViewById(id.receiveText);
        mEditSend = (EditText)findViewById(id.sendText);

        mButtonSend = (Button)findViewById(id.sendButton);
        mButtonSend.setOnClickListener(this);

        checkBluetooth();
    }

    // 2
    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        }
        else { // 블루투스 지원

            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }

    // 3
    void selectDevice() {

        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Dialog", "취소");
                Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // GGG : 블루투스 페어링 버튼 추가
        builder.setNeutralButton("dogfriend가 보이지 않을 경우 click", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                bluetoothSherchView();
            }
        });

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(item == mPairedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "취소하셨습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else { // 연결할 장치를 선택한 경우;
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 3-1 DOGFRIENDS가 페어링 안돼있다면 블루투스 기기 검색창으로 이동 안내 팝업
    void bluetoothSherchView(){

        AlertDialog.Builder oDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

        oDialog.setTitle("DOGFRIENDS가 페어링 되어 있지 않습다.");
        oDialog.setMessage("DOGFRIENDS의 전원을 켜주시고 블루투스 검색을 해주세요.");
        oDialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Dialog", "취소");
                Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        oDialog.setNeutralButton("블루투스 검색", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Dialog", "블루투스 검색");
                Intent intentSearch = new Intent(ConnectActivity.this, SearchBTActivity.class);
                startActivity(intentSearch);
            }
        });
        oDialog.setCancelable(false);   // 뒤로가기 금지
        oDialog.show();
    }

    // 3-2 페어링 된 도그프렌즈 선택시 블루투스 연결
    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);                                //getDeviceFromBondedList : 페어링된 블루투스 목록 가져옴
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            Toast.makeText(getApplicationContext(), "블투스가 연결 되었습니다", Toast.LENGTH_LONG).show();         //EEEE

            beginListenForData();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ERROR!!!");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "닫기", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

            //Toast.makeText(getApplicationContext(), e.getMessage() + "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            //finish();  // App 종료
        }
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //----------------------------- 데이터 송수신 ----------------------------------
    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.

        mWorkerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteAvailable = mInputStream.available();   // 수신 데이터 확인
                        if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes = new byte[byteAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0; i<byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];

                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "utf-8");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable(){

                                        @Override
                                        public void run() {
                                            mEditReceive.setText(mEditReceive.getText().toString() + data+ mStrDelimiter);
                                        }

                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {    // 데이터 수신 중 오류 발생.
                        Toast.makeText(getApplicationContext(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
                        finish();            // App 종료.
                    }
                }
            }

        });
        mWorkerThread.start();
    }

    void sendData(String msg) {
        msg += mStrDelimiter;  // 문자열 종료표시 (\n)
        try{

            mOutputStream.write(msg.getBytes());  // 문자열 전송.
        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생",
                    Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    BluetoothDevice getDeviceFromBondedList(String name) {  // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }






//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//
//        if(id == R.id.action_settings){
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v){
        sendData(mEditSend.getText().toString());
        mEditSend.setText("");
    }

    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

}

