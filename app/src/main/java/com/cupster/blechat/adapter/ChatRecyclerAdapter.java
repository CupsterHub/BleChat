package com.cupster.blechat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cupster.blechat.R;
import com.cupster.blechat.model.RecentChat;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 聊天界面 信息适配器
 *
 * Created by Cupster on 2018/8/9.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    private List<RecentChat> mData;

    public ChatRecyclerAdapter(){}

    public ChatRecyclerAdapter(List<RecentChat> data){
        mData = data;
    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.ViewHolder holder, int position) {
        if(mData.get(position).getName().equals("in")){
            holder.right.setVisibility(View.GONE);
            holder.left.setVisibility(View.VISIBLE);
            holder.time_left.setText(mData.get(position).getTime());
            holder.icon_left.setImageResource(R.drawable.default_head);
            holder.msg_left.setText(mData.get(position).getLastwords());
        }
        if (mData.get(position).getName().equals("out")){
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            holder.time_right.setText(mData.get(position).getTime());
            holder.icon_right.setImageResource(R.drawable.default_head);
            holder.msg_right.setText(mData.get(position).getLastwords());
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout left ;
        public LinearLayout right;

        public TextView time_left;
        public TextView msg_left;
        public ImageView icon_left;

        public TextView time_right;
        public TextView msg_right;
        public ImageView icon_right;

        public ViewHolder(View itemView) {
            super(itemView);
            left = itemView.findViewById(R.id.chat_msg_left);
            right = itemView.findViewById(R.id.chat_msg_right);

            time_left = itemView.findViewById(R.id.item_chat_left_time);
            msg_left = itemView.findViewById(R.id.item_chat_left_msg);
            icon_left = itemView.findViewById(R.id.item_chat_left_icon);

            time_right = itemView.findViewById(R.id.item_chat_right_time);
            msg_right = itemView.findViewById(R.id.item_chat_right_msg);
            icon_right = itemView.findViewById(R.id.item_chat_right_icon);
        }
    }

}
