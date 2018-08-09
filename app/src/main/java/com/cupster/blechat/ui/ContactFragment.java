package com.cupster.blechat.ui;

import android.Manifest;
import android.app.Fragment;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cupster.blechat.R;
import com.cupster.blechat.model.EventMsg;
import com.cupster.blechat.utils.Configs;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Cupster on 2018/8/7.
 */

public class ContactFragment extends Fragment {

    private static final String TAG = "ContactFragment";

    View fragmentView;
    RecyclerView recyclerView;
    private BluetoothGatt mBluetoothGatt;
    EditText input;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_contact, container, false);
        //re
        initView();
        return fragmentView;
    }

    private void initView() {
        recyclerView = fragmentView.findViewById(R.id.ble_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        fragmentView.findViewById(R.id.btn_ble_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBlePermission();
            }
        });
        Button btn = fragmentView.findViewById(R.id.btn_ble_search);
        btn.setOnClickListener(btn_listener);
        btn = fragmentView.findViewById(R.id.btn_ble_send);
        btn.setOnClickListener(btn_listener);
        input = fragmentView.findViewById(R.id.input_ble);


    }


    private View.OnClickListener btn_listener = new View.OnClickListener (){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.btn_ble_search:
                    EventBus.getDefault().post(new EventMsg().setOperaCode(Configs.OPERATION_SEARCH));
                    break;
                case R.id.btn_ble_send:
                    EventBus.getDefault().post(new EventMsg().setOperaCode(Configs.OPERATION_SEND).setMsg(input.getText().toString()));
                    break;
                default:
                    break;
            }
        }
    };


    public void checkBlePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            Log.i("tag", "已申请权限");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 如果请求被取消，则结果数组为空。
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("tag", "同意申请");
                } else {
                    Log.i("tag", "拒绝申请");
                }
                return;
            }
        }
    }

    /** GattCallback */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange: "+newState);
            if(BluetoothGatt.STATE_CONNECTED == newState){
                Log.d(TAG, "onConnectionStateChange: connected");
            }else if(BluetoothGatt.STATE_DISCONNECTED== newState){
                Log.d(TAG, "onConnectionStateChange: disconnected");
            }
//            else if(BluetoothGatt.STATE_CONNECTING){
//            }else if(BluetoothGatt.STATE_DISCONNECTING){
//            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> list = mBluetoothGatt.getServices();
            for (BluetoothGattService  bluetoothGattService :list){
                String str = bluetoothGattService.getUuid().toString();
                Log.d(TAG, "onServicesDiscovered: "+str);
                List<BluetoothGattCharacteristic> bgcs = bluetoothGattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristics: bgcs){
                    Log.d(TAG, "onServicesDiscovered: "+gattCharacteristics.getUuid());
                    if("0000ffe1-0000-1000-8000-00805f9b34fb".equals(gattCharacteristics.getUuid().toString())){
// TODO
//     link
                    }
                }
            }
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };



}
