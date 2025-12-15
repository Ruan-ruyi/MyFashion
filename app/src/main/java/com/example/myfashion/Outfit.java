package com.example.myfashion;

public class Outfit {
    private String title;
    private int imageResId; // 修改点：类型改为 int，变量名改为 imageResId
    private String gender;

    // 修改构造函数，接收 int 类型的图片ID
    public Outfit(String title, int imageResId, String gender) {
        this.title = title;
        this.imageResId = imageResId;
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    // 修改 Getter 方法
    public int getImageResId() {
        return imageResId;
    }

    public String getGender() {
        return gender;
    }
}