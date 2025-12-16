package com.example.myfashion;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String userName;
    private String content;
    private String imageUrl; // 支持网络 URL 或本地 Uri 字符串
    private int likeCount;

    // --- 新增字段 ---
    private boolean isLiked = false;      // 是否已点赞
    private boolean isFavorited = false;  // 是否已收藏
    private List<String> comments;        // 评论列表 (暂时只存文字)

    public Post(String userName, String content, String imageUrl, int likeCount) {
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.comments = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public boolean isFavorited() { return isFavorited; }
    public void setFavorited(boolean favorited) { isFavorited = favorited; }

    public List<String> getComments() { return comments; }
    public void addComment(String comment) { this.comments.add(comment); }
}