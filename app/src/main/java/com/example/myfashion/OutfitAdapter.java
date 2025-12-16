package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Outfit outfit = mList.get(position);
        holder.tvTitle.setText(outfit.getTitle());

        // 【同步修复】列表页也禁用缓存，保持一致
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // ❌ 禁用磁盘缓存
                .skipMemoryCache(true);                    // ❌ 跳过内存缓存

        Glide.with(holder.itemView.getContext())
                .load(outfit.getImageResId())
                .apply(options)
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