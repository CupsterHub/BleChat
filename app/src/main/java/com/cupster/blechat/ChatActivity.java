package com.cupster.blechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cupster.blechat.adapter.ChatRecyclerAdapter;
import com.cupster.blechat.model.EventMsg;
import com.cupster.blechat.model.RecentChat;
import com.cupster.blechat.utils.Configs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cupster on 2018/8/7.
 */

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RecyclerView chat_recycler;
    private ImageButton btn_face;
    private ImageButton btn_add;
    private EditText et_input;
    private ImageButton btn_send;
    private FrameLayout frame_emoji;
    private ProgressDialog progress;

    /** 消息容器 */
    private ArrayList<RecentChat> msgData = new ArrayList<>();
    private ChatRecyclerAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //初始化聊天界面控件
        initView();
        //
        initData();
        //注册 EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.chat_title);

        chat_recycler = findViewById(R.id.chat_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        chat_recycler.setLayoutManager(manager);
        btn_face = findViewById(R.id.chat_msg_face);
        btn_face.setOnClickListener(btn_listener);
        btn_add = findViewById(R.id.chat_msg_add);
        btn_add.setOnClickListener(btn_listener);
        et_input = findViewById(R.id.chat_msg_edit);
        btn_send = findViewById(R.id.chat_msg_send);
        btn_send.setOnClickListener(btn_listener);
        frame_emoji = findViewById(R.id.chat_emojicons);
        progress = new ProgressDialog(this);

    }

    private void initData(){
        //1、toolbar
        String name = getIntent().getStringExtra("name");
        if(!TextUtils.isEmpty(name)){
            toolbarTitle.setText(name);
        }else{
            toolbarTitle.setText("Bluetooth Chat");
        }
        //2、adapter & recycleview
        adapter = new ChatRecyclerAdapter(msgData);
        chat_recycler.setAdapter(adapter);
        //3、



    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true )
    public void EventOperate(EventMsg msg){
        switch (msg.getOperaCode()){
            case Configs.OPERATION_RECEIVE:
                RecentChat chatIn = new RecentChat("in",getCurrTime(),msg.getMsg(),R.drawable.default_head);
                msgData.add(chatIn);
                adapter.notifyDataSetChanged();
                if(progress.isShowing()){
                    progress.cancel();
                    progress.dismiss();
                }
                break;
            case Configs.OPERATION_SEND:
                RecentChat chatOut = new RecentChat("out",getCurrTime(),msg.getMsg(),R.drawable.default_head);
                msgData.add(chatOut);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

    /**
     * 按钮监听
     */
    private View.OnClickListener btn_listener = new View.OnClickListener (){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.chat_msg_face:
                    showToast("funcion developing");
                    break;
                case R.id.chat_msg_add:
                    showToast("funcion developing");
                    break;
                case R.id.chat_msg_send:
                    String inputSend = et_input.getText().toString();
                    if(!TextUtils.isEmpty(inputSend)){
                        EventBus.getDefault().post(new EventMsg().setOperaCode(Configs.OPERATION_SEND).setMsg(inputSend));
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private String getCurrTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * toast 提示
     * @param msg
     */
    private void showToast(String msg){
        Toast.makeText(this.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}
