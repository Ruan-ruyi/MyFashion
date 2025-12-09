package com.example.myfashion;

public class Post {
    private String username;
    private String content;
    private String imageUrl;
    private int likes;

    public Post(String username, String content, String imageUrl, int likes) {
        this.username = username;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likes = likes;
    }

    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public int getLikes() { return likes; }
}