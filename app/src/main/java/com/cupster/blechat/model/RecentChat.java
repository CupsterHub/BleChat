package com.cupster.blechat.model;

import android.widget.ImageView;

import com.cupster.blechat.R;

/**
 * home 最近聊天界面
 * Created by Cupster on 2018/8/7.
 */

public class RecentChat {

    private String name =null;
    private String time =null;
    private String lastwords = null;
    private int icon = R.mipmap.ic_launcher;

    public RecentChat(String name , String time ,String lastwords ,int icon){
        this.name= name;
        this.time = time;
        this.lastwords = lastwords ;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastwords() {
        return lastwords;
    }

    public void setLastwords(String lastwords) {
        this.lastwords = lastwords;
    }

    public int getIcon_url() {
        return icon;
    }

    public void setIcon_url(int icon_url) {
        this.icon = icon_url;
    }
}
