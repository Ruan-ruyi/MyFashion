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

    // 模拟数据库
    private Map<String, String> userDatabase;
    private Map<String, String> phoneDatabase;

    private DataManager() {
        initUserData();
        initOutfitData();
        initCommunityData();
    }

    // 初始化用户数据
    private void initUserData() {
        userDatabase = new HashMap<>();
        phoneDatabase = new HashMap<>();
        userDatabase.put("admin", "123456");
        phoneDatabase.put("admin", "13800138000");
        userDatabase.put("user", "123456");
        phoneDatabase.put("user", "13900139000");
    }

    // 初始化穿搭数据 (使用更稳定的 Lorem Picsum 图片源)
    private void initOutfitData() {
        outfitList = new ArrayList<>();
        // 格式: https://picsum.photos/id/{图片ID}/{宽}/{高}
        outfitList.add(new Outfit("春季清新碎花裙", "https://picsum.photos/id/64/600/800", "Female"));
        outfitList.add(new Outfit("商务休闲西装", "https://picsum.photos/id/447/600/800", "Male"));
        outfitList.add(new Outfit("街头酷飒穿搭", "https://picsum.photos/id/342/600/800", "Male"));
        outfitList.add(new Outfit("优雅晚礼服", "https://picsum.photos/id/439/600/800", "Female"));
        outfitList.add(new Outfit("秋季风衣外套", "https://picsum.photos/id/1005/600/800", "Male"));
        outfitList.add(new Outfit("复古牛仔风", "https://picsum.photos/id/338/600/800", "Male"));
        outfitList.add(new Outfit("夏日海边度假风", "https://picsum.photos/id/838/600/800", "Female"));
        outfitList.add(new Outfit("极简主义搭配", "https://picsum.photos/id/91/600/800", "Male"));
    }

    // 初始化社区数据 (使用 Lorem Picsum 头像和配图)
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

    // --- 登录注册逻辑 (保持不变) ---
    public boolean checkLogin(String username, String password) {
        if (userDatabase.containsKey(username)) {
            return userDatabase.get(username).equals(password);
        }
        return false;
    }

    public boolean register(String username, String password, String phone) {
        if (userDatabase.containsKey(username)) return false;
        userDatabase.put(username, password);
        phoneDatabase.put(username, phone);
        return true;
    }

    // --- Getter / Setter (保持不变) ---
    public List<Outfit> getOutfits() {
        List<Outfit> result = new ArrayList<>();
        for (Outfit o : outfitList) {
            if (o.getGender().equalsIgnoreCase(currentGender)) result.add(o);
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