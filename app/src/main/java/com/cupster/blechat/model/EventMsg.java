package com.cupster.blechat.model;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 消息事件类
 * Created by Cupster on 2018/8/7.
 */

public class EventMsg {

    private RecentChat recentChat= null;
    private int operaCode =0;
    private int requestCode= 0;
    private int responseCode = 0;

    public String getMsg() {
        return msg;
    }

    public EventMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    private String msg="";
//    private String [] arrStr = new String[3];//长度暂时设置3
//    private HashMap<String ,String> devices = new HashMap<>();

//    public HashMap<String, String> getDevices() {
//        return devices;
//    }
//
//    public EventMsg setDevices(HashMap<String, String> devices) {
//        this.devices = devices;
//        return this;
//    }

    public EventMsg(){

    }

    public RecentChat getRecentChat() {
        return recentChat;
    }

    public EventMsg setRecentChat(RecentChat recentChat) {
        this.recentChat = recentChat;
        return this;
    }




    public int getOperaCode() {
        return operaCode;
    }

    public EventMsg setOperaCode(int operaCode) {
        this.operaCode = operaCode;
        return this;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public EventMsg setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public EventMsg setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }
}
