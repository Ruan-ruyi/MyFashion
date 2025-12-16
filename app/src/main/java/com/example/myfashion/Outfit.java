package com.example.myfashion;

public class Outfit {
    private String title;
    private int imageResId;
    private String gender;
    // 【新增】标签数组，例如 {"休闲", "夏季"}
    private String[] tags;
    // 【新增】是否被收藏
    private boolean isFavorite;

    // 修改构造函数，增加 tags
    public Outfit(String title, int imageResId, String gender, String[] tags) {
        this.title = title;
        this.imageResId = imageResId;
        this.gender = gender;
        this.tags = tags;
        this.isFavorite = false; // 默认未收藏
    }

    public String getTitle() { return title; }
    public int getImageResId() { return imageResId; }
    public String getGender() { return gender; }

    // 【新增 Getter/Setter】
    public String[] getTags() { return tags; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}