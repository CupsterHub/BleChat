package com.cupster.blechat.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cupster.blechat.R;
import com.cupster.blechat.adapter.RecyclerViewAdapter;
import com.cupster.blechat.model.RecentChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cupster on 2018/8/6.
 */

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private RecyclerView recyclerView;
    /** root item view */
    private View fragmentView;
    /** recyclerview adapter */
    private RecyclerViewAdapter adapter;
    /** 适配数据 */
    private static ArrayList<RecentChat> data = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (fragmentView==null){
            fragmentView = inflater.inflate(R.layout.fragment_home_tab01, container, false);
        }
        if(recyclerView==null){
            recyclerView = fragmentView.findViewById(R.id.home_recyclerview);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
            data.add(new RecentChat("friend x", "早上 7:xx", "how are you", R.mipmap.ic_launcher));
        }
        if(adapter == null){
            adapter = new RecyclerViewAdapter(data);
            recyclerView.setAdapter(adapter);
        }
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ArrayList<RecentChat> getData() {
        return data;
    }

    public void setData(ArrayList<RecentChat> data) {
        this.data = data;
    }

    public void setOnClickListener(RecyclerViewAdapter.OnItemClickListener listener) {
        if(adapter!=null){
            adapter.setOnClickListener(listener);
            recyclerView.setAdapter(adapter);
        }
    }

    public void updateData() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置windows登录状态提示
     *
     * @param show
     */
    public void setWindowsStatus(boolean show,String text) {
        if (!show) {
            fragmentView.findViewById(R.id.login_computer).setVisibility(View.GONE);
        } else {
            fragmentView.findViewById(R.id.login_computer).setVisibility(View.VISIBLE);
            ((TextView)fragmentView.findViewById(R.id.login_text)).setText(text);
        }
    }

}
