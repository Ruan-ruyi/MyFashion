package com.example.myfashion;

import android.net.Uri;
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

    public CommunityAdapter(List<Post> list, OnPostClickListener listener) {
        this.mList = list;
        this.mListener = listener;
    }

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

        // 1. 设置用户名和内容
        holder.tvUsername.setText(post.getUserName());
        holder.tvContent.setText(post.getContent());
        holder.tvLikes.setText(String.valueOf(post.getLikeCount())); // 只显示数字，心形在xml里

        // 2. 【核心修复】头像显示逻辑
        String currentNick = DataManager.getInstance().getNickname();

        // 如果发帖人是当前登录用户，显示用户的自定义头像
        if (post.getUserName().equals(currentNick)) {
            String myAvatarUri = DataManager.getInstance().getCustomAvatarUri();
            if (myAvatarUri != null) {
                // 加载自定义相册头像
                Glide.with(holder.itemView.getContext())
                        .load(Uri.parse(myAvatarUri))
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.ivAvatar);
            } else {
                // 加载内置选择的头像
                Glide.with(holder.itemView.getContext())
                        .load(DataManager.getInstance().getAvatarResId())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.ivAvatar);
            }
        } else {
            // 其他人（Jessica, David等）使用默认头像
            // 为了让界面不单调，我们可以根据名字长度随机分配一个内置头像
            int[] randomAvatars = {R.drawable.o1, R.drawable.o2, R.drawable.o7, R.drawable.o8};
            int index = post.getUserName().length() % randomAvatars.length;

            Glide.with(holder.itemView.getContext())
                    .load(randomAvatars[index])
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivAvatar);
        }

        // 3. 【核心修复】帖子配图加载
        String imgUrl = post.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);

            // 判断是网络图片还是本地相册图片
            Object loadObj = imgUrl;
            if (!imgUrl.startsWith("http")) {
                loadObj = Uri.parse(imgUrl); // 本地路径转URI，修复加载失败问题
            }

            Glide.with(holder.itemView.getContext())
                    .load(loadObj)
                    .apply(new RequestOptions()
                            .placeholder(android.R.drawable.ic_menu_gallery) // 加载中
                            .error(android.R.drawable.stat_notify_error))    // 加载失败显示红叹号
                    .into(holder.ivPostImage);
        } else {
            // 如果没有图片，隐藏 ImageView，避免占位空白
            holder.ivPostImage.setVisibility(View.GONE);
        }

        // 4. 点击事件
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
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLikes = itemView.findViewById(R.id.tv_likes);
        }
    }
}