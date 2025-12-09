package com.example.myfashion;

public class Outfit {
    private String title;
    private String imageUrl;
    private String gender; // "Male" or "Female"

    public Outfit(String title, String imageUrl, String gender) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.gender = gender;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getGender() { return gender; }
}
