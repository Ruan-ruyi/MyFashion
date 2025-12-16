package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    // 1. 定义点击回调接口
    public interface OnPostClickListener {
        void onPostClick(int position);
    }

    private List<Post> mList;
    private OnPostClickListener mListener;

    // 2. 构造函数接收数据和监听器
    public CommunityAdapter(List<Post> list, OnPostClickListener listener) {
        this.mList = list;
        this.mListener = listener;
    }

    // 更新数据的方法
    public void updateData(List<Post> newList) {
        this.mList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mList.get(position);

        // 绑定数据 (注意：这里使用 Post.java 中定义的正确方法名)
        holder.tvUsername.setText(post.getUserName());
        holder.tvContent.setText(post.getContent());

        // 显示点赞数 (如果有 isLiked 状态也可以在这里改变心形颜色)
        holder.tvLikes.setText("❤ " + post.getLikeCount());

        // 加载右侧配图
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)) // 加载中显示默认图
                .into(holder.ivPostImage);

        // 头像暂时用静态图
        holder.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);

        // 3. 绑定整项点击事件
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onPostClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage;
        TextView tvUsername, tvContent, tvLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定 item_post.xml 中的 ID
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLikes = itemView.findViewById(R.id.tv_likes);
        }
    }
}