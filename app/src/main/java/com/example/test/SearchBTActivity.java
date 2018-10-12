package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

public class SearchBTActivity extends Activity implements  AdapterView.OnItemClickListener{
    //블루투스 요청 액티비티 코드
    static final int REQUEST_ENABLE_BT = 10;
    BluetoothAdapter mBluetoothAdapter;

    //UI
    Button btnSearch;
    ListView listDevice;

    List<String> dataDevice;

    Set<BluetoothDevice> mDevices;
    List<BluetoothDevice> bluetoothDevices;
    int selectDevice;

    //----------------- 페어링된 목록에 디바이스 선택시 선택을 알림 -------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String strItem = (String)parent.getAdapter().getItem(position);
        int pos = strItem.indexOf("-");
        if(pos <= 0 ) return;
        String address = strItem.substring(pos + 3);
        Toast.makeText(SearchBTActivity.this, address + "선택", Toast.LENGTH_SHORT).show();
        stopFindDevice();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) { // savedInstanceState는 상태를 저장함 사용하여 값을 저장
        mayPermission();					// SDK 버전 맞춰줌
        super.onCreate(savedInstanceState);		// 저장되어있는 onCreate 상태를…
        setContentView(R.layout.activity_search_bt);
        //UI
        btnSearch = (Button)findViewById(R.id.btnSearch);
        listDevice = (ListView)findViewById(R.id.listDevice);
        bluetoothDevices = new ArrayList<>();
        selectDevice = -1;
        //블루투스 활성화
        checkBluetooth();

        btnSearch.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        checkBluetooth();
                    }
                }
        );
    }


    //블루투스 설정
    protected void checkBluetooth(){
        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
            searchDevices();
        }
//        if(mBluetoothAdapter == null){
//            //장치가 블루투스를 지원하지 않는 경우
//            finish();
//        }else{
//            //장치가 블루투스를 지원하는 경우
//            if(!mBluetoothAdapter.isEnabled()){
//                //블루투스가 비활성 상태인 경우
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }else{
//                searchDevices();
//            }
//        }
    }

    //----------------------------------------------------------------------------------------------
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    searchDevices();
                }else if(resultCode == RESULT_CANCELED){
                    //블루투스 비활성화 상태
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //----------------------------------------------------------------------------------------------
    protected void searchDevices(){
        initListView();
        mDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : mDevices){
            addDeviceToList(device.getName(), device.getAddress(), true) ;
        }
        startFindDevice();
    }


    //--------------------------------------------------------------------------------------------------
    protected void initListView(){
        dataDevice = new ArrayList<>();
        bluetoothDevices.clear();

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataDevice);
        listDevice.setAdapter(adapter2);
        listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = bluetoothDevices.get(position);
                try{
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[])null);
                    selectDevice = position;
                    Toast.makeText(SearchBTActivity.this, "장치와 페어링 시도하겠습니다.", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
    //-----------------------------------------------------------------------------------------------
    protected void addDeviceToList(String name, String address, boolean isPaired){
        String deviceInfo = name + "-" + address;
        ArrayAdapter adapter;
        if(!isPaired){
            dataDevice.add(deviceInfo);
        }
        adapter = (ArrayAdapter)listDevice.getAdapter();
        adapter.notifyDataSetChanged();
    }

    //------------------------------------------------------------------------------------
    protected void startFindDevice(){
        //검색 중지
        stopFindDevice();
        //디바이스 검색 시작
        Toast.makeText(SearchBTActivity.this, "장치 검색 시작", Toast.LENGTH_SHORT).show();
        mBluetoothAdapter.startDiscovery();
        registerReceiver(mBlueRecv, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mBlueRecv, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }
    protected void stopFindDevice(){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            unregisterReceiver(mBlueRecv);
        }
    }
    //---------------------------------------------------------------------------------------
    BroadcastReceiver mBlueRecv = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //인텐트에서 디바이스 정보 추출
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //페어링 된 디바이스가 아니라면
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);
                    addDeviceToList(device.getName(), device.getAddress(), false);
                }
            }else if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                //검색된 디바이스가 페어링 되었을 때 상태 변화
                BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(paired.getBondState() == BluetoothDevice.BOND_BONDED){
                    addDeviceToList(paired.getName(), paired.getAddress(), true);

                    if(selectDevice != -1){
                        bluetoothDevices.remove(selectDevice);
                        dataDevice.remove(selectDevice);
                        ArrayAdapter adapter = (ArrayAdapter)listDevice.getAdapter();
                        adapter.notifyDataSetChanged();
                        selectDevice = -1;

                        startActivity(new Intent(SearchBTActivity.this, ConnectActivity.class));
//                        Intent intentConnect = new Intent(SearchBTActivity.this, ConnectActivity.class);
//                        startActivity(intentConnect);

                    }
                }
            }
        }
    };
    //---------------------------------- SDK 버전 맞춰줌 ------------------------------------------
    public void mayPermission(){
        if(Build.VERSION.SDK_INT>=23){
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "ble_need", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                return;
            }else{

            }
        }else{

        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopFindDevice();
    }
    @Override
    protected void onStop(){
        super.onStop();
        stopFindDevice();
    }
    @Override
    protected  void onPause(){
        super.onPause();
        stopFindDevice();
    }
}