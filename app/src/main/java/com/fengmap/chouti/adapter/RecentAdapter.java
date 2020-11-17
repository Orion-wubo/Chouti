package com.fengmap.chouti.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fengmap.chouti.entity.FileEntity;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder> {
    Context context;
    List list;
    Integer layout;
    int[] to;

    public RecentAdapter(Context context, List<Object> list, Integer layout, int[] to) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.to = to;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, FileEntity fileEntity);
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final FileEntity fileEntity = (FileEntity) list.get(position);
        String path = fileEntity.getPath();
        File file = new File(path);
        if (!file.exists()) {
            holder.tv_name.setTextColor(Color.RED);
            holder.tv_path.setTextColor(Color.RED);

            holder.tv_name.setText("~"+fileEntity.getName());
            holder.tv_path.setText("~"+fileEntity.getPath());
        } else {
            holder.tv_name.setText(fileEntity.getName());
            holder.tv_path.setText(fileEntity.getPath());
        }


        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = fileEntity.getId();
                int delete = LitePal.delete(FileEntity.class, id);
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        if (onItemClickListener != null) {
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
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
        private TextView tv_name,tv_path;
        private LinearLayout ll_item;
        private ImageView iv_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(to[0]);
            ll_item = itemView.findViewById(to[1]);
            tv_path = itemView.findViewById(to[2]);
            iv_delete = itemView.findViewById(to[3]);
        }
    }
}
