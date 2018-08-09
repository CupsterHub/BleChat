package com.cupster.blechat.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cupster.blechat.R;
import com.cupster.blechat.model.RecentChat;

import org.w3c.dom.Text;

import java.util.List;


/**
 * recycleview adapter
 * Created by Cupster on 2018/8/7.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<RecentChat> mData;

    public OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(List<RecentChat> data){
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        //TODO 更改头像来源
        holder.imageView.setImageResource(mData.get(position).getIcon_url());

        holder.name.setText(mData.get(position).getName());
        holder.time.setText(mData.get(position).getTime());
        holder.lastWords.setText(mData.get(position).getLastwords());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view ,int position);
    }

    public void setOnClickListener (OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView name;
        public TextView time;
        public TextView lastWords;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_re_icon);
            name = itemView.findViewById(R.id.item_re_name);
            time = itemView.findViewById(R.id.item_re_last_time);
            lastWords = itemView.findViewById(R.id.item_re_last_msg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view,getPosition());
        }

    }
}
