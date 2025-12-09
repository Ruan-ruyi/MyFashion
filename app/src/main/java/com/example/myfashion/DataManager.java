package com.example.myfashion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private List<Outfit> outfitList;
    private List<Post> communityPosts;
    private String currentGender = "Female";
    private String loggedInUser = null;

    // 模拟数据库：存储 用户名 -> 密码
    private Map<String, String> userDatabase;
    // 模拟数据库：存储 用户名 -> 手机号
    private Map<String, String> phoneDatabase;

    private DataManager() {
        // 1. 初始化用户数据 (预存几个账号)
        userDatabase = new HashMap<>();
        phoneDatabase = new HashMap<>();
        // 默认账号：admin / 123456
        userDatabase.put("admin", "123456");
        phoneDatabase.put("admin", "13800138000");
        // 默认账号：user / 123456
        userDatabase.put("user", "123456");
        phoneDatabase.put("user", "13900139000");

        // 2. 初始化穿搭数据 (更换为稳定的高清图)
        outfitList = new ArrayList<>();
        outfitList.add(new Outfit("春季清新碎花裙", "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600", "Female"));
        outfitList.add(new Outfit("商务休闲西装", "https://images.unsplash.com/photo-1594938298603-c8148c47e356?w=600", "Male"));
        outfitList.add(new Outfit("街头酷飒穿搭", "https://images.unsplash.com/photo-1529139574466-a302d2052574?w=600", "Male"));
        outfitList.add(new Outfit("优雅晚礼服", "https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=600", "Female"));
        outfitList.add(new Outfit("秋季风衣外套", "https://images.unsplash.com/photo-1545291730-f2f75c6d2d42?w=600", "Male"));
        outfitList.add(new Outfit("复古牛仔风", "https://images.unsplash.com/photo-1552374196-1ab2a1c593e8?w=600", "Male"));

        // 3. 初始化社区数据
        communityPosts = new ArrayList<>();
        communityPosts.add(new Post("Jessica", "今天的OOTD，心情美美哒！", "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=400", 120));
        communityPosts.add(new Post("David", "周末露营穿这套绝了", "https://images.unsplash.com/photo-1504194921103-f8b80cadd5e4?w=400", 85));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // --- 登录注册逻辑 ---

    // 验证登录
    public boolean checkLogin(String username, String password) {
        if (userDatabase.containsKey(username)) {
            String storedPwd = userDatabase.get(username);
            return storedPwd.equals(password);
        }
        return false;
    }

    // 注册新用户
    public boolean register(String username, String password, String phone) {
        if (userDatabase.containsKey(username)) {
            return false; // 用户名已存在
        }
        userDatabase.put(username, password);
        phoneDatabase.put(username, phone);
        return true;
    }

    // --- Getter / Setter (保持不变) ---
    public List<Outfit> getOutfits() {
        List<Outfit> result = new ArrayList<>();
        for (Outfit o : outfitList) {
            if (o.getGender().equalsIgnoreCase(currentGender)) {
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
}