package com.cupster.blechat;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.cupster.blechat.model.EventMsg;
import com.cupster.blechat.model.RecentChat;
import com.cupster.blechat.utils.Configs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 工作service
 * Created by Cupster on 2018/8/7.
 */

public class WorkService extends Service {

    private static final String TAG = "WorkService";

    private final UUID My_UUID = UUID.fromString("f14e1e24-ffa6-4825-9930-bbb6da809075");
    private final String NAME = "bluetooth_socket";

    /**
     * Client监听线程
     */
    private ClientReadThread cRead = new ClientReadThread();
    /**
     * 连接目标 buletooth
     */
    private BluetoothDevice remoteDev;

    private BluetoothSocket clientSocket;
    private BluetoothAdapter adapter = null;
    /**
     * 客户端输入输出流
     */
    private OutputStream outputStreamClient = null;
    private InputStream inputStreamClient = null;
    /**
     * 服务端输入输出流
     */
    private OutputStream outputStreamServer = null;
    private InputStream inputStreamServer = null;
    /** 存放ble name:adress*/
    private HashMap<String, String> bleDevs = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        //及时回收
        if (inputStreamClient != null) {
            try {
                inputStreamClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStreamClient != null) {
            try {
                outputStreamClient.flush();
                outputStreamClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStreamServer != null) {
            try {
                inputStreamServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStreamServer != null) {
            try {
                outputStreamServer.flush();
                outputStreamServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //反注册广播监听
        unregisterBroadCast();
        super.onDestroy();
    }

    /**
     * 组件 事件处理
     *
     * @param msg
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = false, priority = 2)
    public void eventOpera(EventMsg msg) {
        //中断事件传递
        switch (msg.getOperaCode()) {
            case Configs.OPERATION_SEARCH://搜索
                startSearch();
                break;
            case Configs.OPERATION_CONNECT://链接
                bleConnect(msg);
                break;
            case Configs.OPERATION_SEND://发送
                if (outputStreamServer != null) {
                    serverSend2Client(msg.getMsg());
                } else {
                    Log.d(TAG, "eventOpera: outputStreamServer is null");
                }
                if (outputStreamClient != null) {
                    clientSend2Server(msg.getMsg());
                } else {
                    Log.d(TAG, "eventOpera:  outputStreamClient is null");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送组件消息
     *
     * @param msg
     */
    public void eventSend(EventMsg msg) {
        Log.e(TAG, "eventSend: ");
        EventBus.getDefault().post(msg);
    }

    /**
     * Client 发往服务端
     */
    private void clientSend2Server(final String msg) {
        try {
            outputStreamClient.write(msg.getBytes("utf-8"));
            outputStreamClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Server 发往客户端
     */
    private void serverSend2Client(final String msg) {
        try {
            outputStreamServer.write(msg.getBytes("utf-8"));
            outputStreamServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /******************************  蓝牙操作部分  *****************************/
    private void startSearch() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            eventSend(new EventMsg().setOperaCode(Configs.OPERATION_ASK_OPEN_BLE));
        } else {
            bleBegin();
        }
    }

    /**
     * 开始扫描
     */
    private void bleBegin() {
        registerBroadCast();
        new AcceptThread().start();
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();
        //TODO 设置关闭扫描，减少设备损耗,5.0+ 8-10秒停止扫描
    }

    /**
     * 与目标设备建立连接
     */
    private void bleConnect(EventMsg msg){
        String address = msg.getRecentChat().getLastwords();//mac
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        remoteDev = adapter.getRemoteDevice(address);
        try {
            clientSocket = remoteDev.createRfcommSocketToServiceRecord(My_UUID);
            Log.d(TAG, "eventOpera:  inited clientSocket");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d(TAG, "eventOpera: " + clientSocket.getRemoteDevice().getBondState());
            clientSocket.connect();
            outputStreamClient = clientSocket.getOutputStream();
            inputStreamClient = clientSocket.getInputStream();
            String link = adapter.getName()+"-Client接入";
            if (outputStreamClient != null) {
                outputStreamClient.write(link.getBytes("utf-8"));
                outputStreamClient.flush();
            }
            if (!cRead.isAlive()) {
                cRead.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索结束，发送设备集合
     */
    private void searchEnd() {
        Set<Map.Entry<String, String>> entries = bleDevs.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            eventSend(new EventMsg().setOperaCode(Configs.OPERATION_SEARCH_END).setRecentChat(
                    new RecentChat(entry.getKey(), "", entry.getValue(), R.mipmap.ic_launcher)));
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    /**
     * 解注册
     */
    private void unregisterBroadCast() {
        unregisterReceiver(receiver);
    }

    /**
     * 广播监听
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //扫描到新设备监听
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, "scanf got " + device.getName() + " - mac: " + device.getAddress());
                //存储
                bleDevs.put(TextUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName()
                        , device.getAddress());
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                //结束扫描监听
                Log.d(TAG, "scanf finish。。。");
                searchEnd();
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                //绑定状态监听
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {//绑定中...
                    Log.d(TAG, "bonding....");
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {//已绑定
                    if (!cRead.isAlive()) {
                        cRead.start();
                    }
                    try {
                        if (clientSocket != null) {
                            outputStreamClient = clientSocket.getOutputStream();
                            inputStreamClient = clientSocket.getInputStream();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "bonded");
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {//解除绑定
                    Log.d(TAG, "bond cancel");
                }
            }
        }
    };

    /**
     * 服务端 socket监听线程
     */
    private class AcceptThread extends Thread {
        private BluetoothServerSocket sSocket;
        private BluetoothSocket cSocket;

        public AcceptThread() {
            try {
                sSocket = adapter.listenUsingRfcommWithServiceRecord(NAME, My_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                cSocket = sSocket.accept();
                inputStreamServer = cSocket.getInputStream();
                outputStreamServer = cSocket.getOutputStream();
                while (true) {
                    byte[] buffer = new byte[1024];
                    int count = inputStreamServer.read(buffer);
                    String msg = new String(buffer, 0, count, "utf-8");
                    Log.d(TAG, "Accept run: " + msg);
                    eventSend(new EventMsg().setOperaCode(Configs.OPERATION_RECEIVE).setMsg(msg));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端Socket监听线程
     */
    public class ClientReadThread extends Thread {

        @Override
        public void run() {
            while (true) {
                String get = null;
                if (inputStreamClient != null) {
                    try {
                        byte[] in = new byte[1024];
                        int buff = inputStreamClient.read(in);
                        if (buff != -1) {
                            get = new String(in, 0, buff, "utf-8");
                            eventSend(new EventMsg().setOperaCode(Configs.OPERATION_RECEIVE).setMsg(get));
                            Log.d(TAG, "client read: " + get);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
