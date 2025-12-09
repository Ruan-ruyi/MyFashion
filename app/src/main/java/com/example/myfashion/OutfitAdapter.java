package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.ViewHolder> {
    private List<Outfit> mList;

    public OutfitAdapter(List<Outfit> list) {
        this.mList = list;
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
        // 使用 Glide 加载图片
        Glide.with(holder.itemView.getContext())
                .load(outfit.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery) // 加载中显示的默认图
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() { return mList.size(); }

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