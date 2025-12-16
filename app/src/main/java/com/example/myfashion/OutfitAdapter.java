package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
// 【新增】引入这两个必要的包
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.ViewHolder> {
    private List<Outfit> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Outfit outfit);
    }

    public OutfitAdapter(List<Outfit> list, OnItemClickListener listener) {
        this.mList = list;
        this.mListener = listener;
    }

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

    // --- 👇 这里是你要修改的核心部分 👇 ---
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Outfit outfit = mList.get(position);
        holder.tvTitle.setText(outfit.getTitle());

        // 【关键修改】配置 Glide 选项：禁用缓存
        // 这样可以强制 Glide 每次都去读最新的资源 ID，防止 o1 显示成 o2 的情况
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background) // 加载中显示
                .error(R.drawable.ic_launcher_foreground)       // 加载失败显示
                .diskCacheStrategy(DiskCacheStrategy.NONE)      // ❌ 禁用磁盘缓存
                .skipMemoryCache(true);                         // ❌ 跳过内存缓存

        Glide.with(holder.itemView.getContext())
                .load(outfit.getImageResId()) // 加载本地资源 ID
                .apply(options)               // 应用上面的防缓存配置
                .into(holder.ivImage);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(outfit);
            }
        });
    }
    // --- 👆 修改结束 👆 ---

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