package com.fengmap.chouti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.MyViewHolder> {
    Context context;
    List list;
    Integer layout;
    int[] to;

    public PathAdapter(Context context, List<Object> list, Integer layout, int[] to) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.to = to;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String path);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickLitener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final String fileEntity = (String) list.get(position);
        holder.tv_name.setText(fileEntity+" /");

        if (onItemClickListener != null) {
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = holder.getLayoutPosition();
                    if ((position == 0 && position == list.size() - 1) || position == list.size() - 1) {
                        return;
                    }
                    onItemClickListener.onItemClick(holder.ll_item, position, fileEntity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private LinearLayout ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(to[0]);
            ll_item = itemView.findViewById(to[1]);
        }
    }
}
