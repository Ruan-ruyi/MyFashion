package com.example.myfashion;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Outfit> outfitList;
    private List<Post> communityPosts; // 新增：社区帖子数据
    private String currentGender = "Female";

    // 新增：当前登录用户
    private String loggedInUser = null;

    private DataManager() {
        // 1. 初始化穿搭数据 (保持原样)
        outfitList = new ArrayList<>();
        outfitList.add(new Outfit("春季碎花裙", "https://img.zcool.cn/community/01f4095e966f3fa801216518175d33.jpg", "Female"));
        outfitList.add(new Outfit("商务西装", "https://img.zcool.cn/community/01a4e95d5b6e6ba8012187f4c07923.jpg", "Male"));
        // ... (你的其他数据)

        // 2. 初始化社区帖子数据 (新增)
        communityPosts = new ArrayList<>();
        communityPosts.add(new Post("小美", "今天这套穿搭太绝了！", "https://img.zcool.cn/community/01f4095e966f3fa801216518175d33.jpg", 102));
        communityPosts.add(new Post("Jason", "男生职场穿搭指南", "https://img.zcool.cn/community/01a4e95d5b6e6ba8012187f4c07923.jpg", 58));
        communityPosts.add(new Post("Lisa", "周末去露营怎么穿？", "https://img.zcool.cn/community/018c645d94121da8012060be7ea45f.jpg", 230));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public List<Outfit> getOutfits() {
        List<Outfit> result = new ArrayList<>();
        for (Outfit o : outfitList) {
            if (o.getGender().equalsIgnoreCase(currentGender)) {
                result.add(o);
            }
        }
        return result;
    }

    // 新增方法
    public List<Post> getCommunityPosts() { return communityPosts; }
    public void addPost(Post post) { communityPosts.add(0, post); } // 发帖插到最前面

    public String getGender() { return currentGender; }
    public void setGender(String gender) { this.currentGender = gender; }

    public void login(String username) { this.loggedInUser = username; }
    public void logout() { this.loggedInUser = null; }
    public String getLoggedInUser() { return loggedInUser; }
}