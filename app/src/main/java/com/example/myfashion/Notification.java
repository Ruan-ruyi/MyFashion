package com.example.myfashion;

public class Notification {
    private String title;   // 标题 (e.g., "系统通知", "收到赞")
    private String content; // 内容 (e.g., "Jessica 赞了你的帖子")
    private String time;    // 时间

    public Notification(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTime() { return time; }
}