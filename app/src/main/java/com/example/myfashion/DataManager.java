package com.example.myfashion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataManager {
    private static DataManager instance;
    private List<Outfit> outfitList;
    private List<Post> communityPosts;

    // --- 现有字段 ---
    private String currentGender = "Female";
    private String loggedInUser = null;

    // --- 个人信息字段 ---
    private String nickname = "点击设置昵称";
    private String userSelfGender = "保密";

    // 【新增】头像和生日
    private int avatarResId = R.mipmap.ic_launcher_round; // 默认头像
    private String birthday; // 生日字符串 (YYYY-MM-DD)

    private Map<String, String> userDatabase;
    private Map<String, String> phoneDatabase;

    private DataManager() {
        initUserData();
        initOutfitData();
        initCommunityData();

        // 【新增】初始化时生成一个随机生日
        this.birthday = generateRandomBirthday();
    }

    // 生成 1990年~2005年 之间的随机日期
    private String generateRandomBirthday() {
        Random rnd = new Random();
        int year = 1990 + rnd.nextInt(16); // 1990-2005
        int month = 1 + rnd.nextInt(12);
        int day = 1 + rnd.nextInt(28); // 偷懒只生成到28号，防止2月报错
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    private void initUserData() {
        userDatabase = new HashMap<>();
        phoneDatabase = new HashMap<>();
        userDatabase.put("admin", "123456");
        phoneDatabase.put("admin", "13800138000");
        userDatabase.put("user", "123456");
        phoneDatabase.put("user", "13900139000");
    }

    private void initOutfitData() {
        outfitList = new ArrayList<>();
        outfitList.add(new Outfit("春季清新碎花裙", R.drawable.o1, "Female"));
        outfitList.add(new Outfit("商务休闲西装", R.drawable.o2, "Male"));
        outfitList.add(new Outfit("街头酷飒穿搭", R.drawable.o3, "Male"));
        outfitList.add(new Outfit("优雅晚礼服", R.drawable.o4, "Female"));
        outfitList.add(new Outfit("秋季风衣外套", R.drawable.o5, "Male"));
        outfitList.add(new Outfit("复古牛仔风", R.drawable.o6, "Male"));
        outfitList.add(new Outfit("夏日海边度假风", R.drawable.o7, "Female"));
        outfitList.add(new Outfit("极简主义白T恤", R.drawable.o8, "Male"));
        outfitList.add(new Outfit("冬季保暖羽绒服", R.drawable.o9, "Female"));
        outfitList.add(new Outfit("运动健身套装", R.drawable.o10, "Female"));
        outfitList.add(new Outfit("日系工装风格", R.drawable.o11, "Male"));
        outfitList.add(new Outfit("约会甜美穿搭", R.drawable.o12, "Female"));
        outfitList.add(new Outfit("职场精英范", R.drawable.o13, "Male"));
        outfitList.add(new Outfit("海岛风情长裙", R.drawable.o14, "Female"));
    }

    private void initCommunityData() {
        communityPosts = new ArrayList<>();
        communityPosts.add(new Post("Jessica", "今天的OOTD，心情美美哒！✨", "https://picsum.photos/id/1011/800/600", 120));
        communityPosts.add(new Post("David", "周末露营穿这套绝了🏕️", "https://picsum.photos/id/1015/800/600", 85));
        communityPosts.add(new Post("Lisa", "探店新发现，这家店的衣服超有设计感！", "https://picsum.photos/id/1025/800/600", 230));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public boolean checkLogin(String username, String password) {
        if (userDatabase.containsKey(username)) return userDatabase.get(username).equals(password);
        return false;
    }

    public boolean register(String username, String password, String phone) {
        if (userDatabase.containsKey(username)) return false;
        userDatabase.put(username, password);
        phoneDatabase.put(username, phone);
        return true;
    }

    // --- Getter / Setter ---
    public List<Outfit> getOutfits() {
        List<Outfit> result = new ArrayList<>();
        for (Outfit o : outfitList) {
            if ("All".equalsIgnoreCase(currentGender) || o.getGender().equalsIgnoreCase(currentGender)) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Post> getCommunityPosts() { return communityPosts; }
    public void addPost(Post post) { communityPosts.add(0, post); }
    public String getGender() { return currentGender; }
    public void setGender(String gender) { this.currentGender = gender; }
    public void login(String username) { this.loggedInUser = username; }
    public void logout() { this.loggedInUser = null; }
    public String getLoggedInUser() { return loggedInUser; }

    public String getNickname() {
        if (nickname == null || nickname.equals("点击设置昵称")) {
            return loggedInUser != null ? loggedInUser : "游客";
        }
        return nickname;
    }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getUserSelfGender() { return userSelfGender; }
    public void setUserSelfGender(String gender) { this.userSelfGender = gender; }

    // 【新增】头像和生日的 Getter/Setter
    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
}