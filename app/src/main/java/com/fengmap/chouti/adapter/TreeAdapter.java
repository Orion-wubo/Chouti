package com.fengmap.chouti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fengmap.chouti.R;
import com.fengmap.chouti.entity.FileEntity;

import java.util.List;

/**
 * Created by 剑雨丶游魂 on 2016/7/18.
 */
public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.MyViewHolder> {
    Context context;
    List list;
    Integer layout;
    int[] to;

    public TreeAdapter(Context context, List<Object> list, Integer layout, int[] to) {
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.to = to;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position,String path);
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
        final FileEntity fileEntity = (FileEntity) list.get(position);
        if (fileEntity.isFile()) {
            holder.iv_type.setImageResource(R.mipmap.file);
        } else {
            holder.iv_type.setImageResource(R.mipmap.dir);
        }
        holder.tv_name.setText(fileEntity.getName());

        if (onItemClickListener != null) {
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.ll_item, pos, fileEntity.getPath());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_type;
        private TextView tv_name;
        private LinearLayout ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_type = itemView.findViewById(to[0]);
            tv_name = itemView.findViewById(to[1]);
            ll_item = itemView.findViewById(to[2]);
        }
    }

    /**
     * 添加所有child
     *
     * @param lists
     * @param position
     */
    public void addAllChild(List<?> lists, int position) {
        list.addAll(position, lists);
        notifyItemRangeInserted(position, lists.size());
    }

    /**
     * 删除所有child
     *
     * @param position
     * @param itemnum
     */
    public void deleteAllChild(int position, int itemnum) {
        for (int i = 0; i < itemnum; i++) {
            list.remove(position);
        }
        notifyItemRangeRemoved(position, itemnum);
    }
}
