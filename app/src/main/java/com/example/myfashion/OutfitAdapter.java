package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.ViewHolder> {
    private List<Outfit> mList;
    // 1. 新增监听器接口
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Outfit outfit);
    }

    public OutfitAdapter(List<Outfit> list, OnItemClickListener listener) { // 修改构造函数
        this.mList = list;
        this.mListener = listener;
    }

    // 更新数据的方法
    public void updateData(List<Outfit> newList) {
        this.mList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Outfit outfit = mList.get(position);
        holder.tvTitle.setText(outfit.getTitle());

        // 修改点：load() 方法现在直接接收 int 类型的资源ID，Glide 能自动识别
        Glide.with(holder.itemView.getContext())
                .load(outfit.getImageResId()) // 这里改成了 getImageResId()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(outfit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_outfit);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}