package com.cupster.blechat;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.cupster.blechat.adapter.RecyclerViewAdapter;
import com.cupster.blechat.model.EventMsg;
import com.cupster.blechat.model.RecentChat;
import com.cupster.blechat.ui.ChatFragment;
import com.cupster.blechat.ui.ContactFragment;
import com.cupster.blechat.utils.Configs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * Created by Cupster on 2018/8/6.
 */

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    BottomNavigationBar bottomNavigationBar;
    int lastPosition = 0;

    ChatFragment chatFragment;
    ContactFragment contactFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Toolbar 设置
        toolbar = findViewById(R.id.home_toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        initView();
        //EventBus 注册
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启服务
        Intent intent = new Intent(this, WorkService.class);
        startService(intent);
        //动态获取权限
        checkBlePermission();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            chatFragment.setOnClickListener(listener_item);
        }catch (Exception e){
            Log.d(TAG, "onResume: adapter is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private void initView(){
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abc_description, R.string.abc_description_format);
        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);

        //底部栏初始化
        bottomNavigationBar = findViewById(R.id.home_tabbar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_launcher_foreground, "Home").setBadgeItem(new TextBadgeItem().setText("3").setHideOnSelect(true)).setInactiveIconResource(R.mipmap.ic_launcher))
                .addItem(new BottomNavigationItem(R.drawable.ic_launcher_foreground, "Contact").setInactiveIconResource(R.mipmap.ic_launcher))
                .addItem(new BottomNavigationItem(R.drawable.ic_launcher_foreground, "Find").setBadgeItem(new ShapeBadgeItem().setShape(ShapeBadgeItem.SHAPE_OVAL).setShapeColor(Color.RED)).setInactiveIconResource(R.mipmap.ic_launcher))
                .addItem(new BottomNavigationItem(R.drawable.ic_launcher_foreground, "Setting").setInactiveIconResource(R.mipmap.ic_launcher))
                .setFirstSelectedPosition(lastPosition)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(tab_listener);
        //fragment初始化
        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        setDefaultFrag();
    }

    /**
     * 设置默认fragment
     */
    private void setDefaultFrag() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.home_frag_layout, chatFragment);

        transaction.commit();
        bottomNavigationBar.selectTab(0);
    }

    /**
     * 底部导航栏监听
     */
    private BottomNavigationBar.OnTabSelectedListener tab_listener = new BottomNavigationBar.OnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {
            Log.d(TAG, "onTabSelected: " + position);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            switch (position) {
                case 0:
                    if (chatFragment == null) {
                        chatFragment = new ChatFragment();
                    }
                    transaction.replace(R.id.home_frag_layout, chatFragment);
                    chatFragment.setOnClickListener(listener_item);
                    break;
                case 1:
                    if (contactFragment == null) {
                        contactFragment = new ContactFragment();
                    }
                    transaction.replace(R.id.home_frag_layout, contactFragment);
                    checkBlePermission();//动态获取权限
                    break;
                case 2:

                    break;
                case 3:

                    break;
                default:
                    break;
            }
            transaction.commit();//最后要提交

        }

        @Override
        public void onTabUnselected(int position) {
            Log.d(TAG, "onTabUnselected: " + position);
        }

        @Override
        public void onTabReselected(int position) {
            Log.d(TAG, "onTabReselected: " + position);
        }
    };


    /**
     * 接收事件，执行响应操作
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void eventOpera(EventMsg msg) {
        switch (msg.getOperaCode()) {
            case Configs.OPERATION_ASK_OPEN_BLE:
                openBLE();
                break;
            case Configs.OPERATION_SEARCH_END:
                if (chatFragment!=null){
                    ArrayList<RecentChat> arr = chatFragment.getData();
                    arr.add(msg.getRecentChat());
                    chatFragment.setData(arr);
                    chatFragment.updateData();
                    chatFragment.setOnClickListener(listener_item);
                }
                break;
            case Configs.OPERATION_RECEIVE://TODO 接收到消息 ，等待显示处理
                if(msg.getMsg().contains("-Client接入")){
                    Toast.makeText(this.getApplicationContext(),"Client接入",Toast.LENGTH_SHORT).show();
                    String[] split = msg.getMsg().split("-");
                    Intent intent = new Intent(this,ChatActivity.class);
                    intent.putExtra("name",split[0]);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 监听点击
     */
    RecyclerViewAdapter.OnItemClickListener listener_item = new RecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            //递送 链接目标
            eventSend(new EventMsg().setOperaCode(Configs.OPERATION_CONNECT).setRecentChat(chatFragment.getData().get(position)));

            Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
            intent.putExtra("name",chatFragment.getData().get(position).getName());
            HomeActivity.this.startActivity(intent);
        }
    };
    /**
     * 发送事件
     * @param msg
     */
    public void eventSend(EventMsg msg) {
        EventBus.getDefault().post(msg);
    }



    private  final int BLE23 = 23;
    private void checkBlePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkBlePermission: ask permission");
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },BLE23);
        }else{
            Log.d(TAG, "checkBlePermission: already granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case BLE23:
                if (grantResults.length  > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: now granted");
                }else{
                    Log.d(TAG, "onRequestPermissionsResult: refused");
                    //TODO 权限拒绝处理,二次请求权限,show dialog
                }
                break;
            default:
                break;
        }
    }

    private void openBLE(){
        //默认120秒.intent.putExtra(ba.duration , 300)
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent ,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:  resultCode="+resultCode);
        if (requestCode == 1 && resultCode!=0){
            eventSend(new EventMsg().setOperaCode(Configs.OPERATION_SEARCH));
        }

    }
}
