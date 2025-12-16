package com.example.myfashion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataManager {
    private static DataManager instance;
    private List<Outfit> outfitList;
    private List<Post> communityPosts;

    private String currentGender = "Female";
    private String loggedInUser = null;

    private String nickname = "点击设置昵称";
    private String userSelfGender = "保密";

    // --- 头像相关 ---
    private int avatarResId = R.mipmap.ic_launcher_round; // 默认内置头像ID
    private String customAvatarUri = null; // 【新增】自定义头像的路径 (URI字符串)

    private String birthday;

    private Map<String, String> userDatabase;
    private Map<String, String> phoneDatabase;

    private DataManager() {
        initUserData();
        initOutfitData();
        initCommunityData();
        this.birthday = generateRandomBirthday();
    }

    private String generateRandomBirthday() {
        Random rnd = new Random();
        int year = 1990 + rnd.nextInt(16);
        int month = 1 + rnd.nextInt(12);
        int day = 1 + rnd.nextInt(28);
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

    // --- Getter/Setter 修改 ---
    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
        this.customAvatarUri = null; // 如果设置了内置头像，就清空自定义头像
    }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    // 【新增】自定义头像的 Getter/Setter
    public String getCustomAvatarUri() { return customAvatarUri; }
    public void setCustomAvatarUri(String customAvatarUri) { this.customAvatarUri = customAvatarUri; }

    // 添加新帖子 (发帖用)
    public void addNewPost(String content, String imageUri) {
        // 获取当前登录用户，如果没有则默认为 "我"
        String user = getNickname();
        Post newPost = new Post(user, content, imageUri, 0);
        //在这个列表的开头添加，这样新帖子就在最上面
        communityPosts.add(0, newPost);
    }

    // 根据索引获取帖子 (用于详情页)
    public Post getPostByIndex(int index) {
        if (index >= 0 && index < communityPosts.size()) {
            return communityPosts.get(index);
        }
        return null;
    }

    // --- 【新增】通知相关 ---
    private List<Notification> notificationList;

    // 初始化一些模拟通知
    private void initNotifications() {
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("系统通知", "欢迎来到 MyFashion！开启你的时尚之旅吧。", "刚刚"));
        notificationList.add(new Notification("收到赞", "Jessica 赞了你的帖子", "10分钟前"));
        notificationList.add(new Notification("新评论", "David: 这套搭配太帅了，求链接！", "1小时前"));
        notificationList.add(new Notification("活动提醒", "周末穿搭挑战赛即将开始，快来参加！", "昨天"));
    }

    // 获取通知列表 (如果为空就初始化)
    public List<Notification> getNotifications() {
        if (notificationList == null) {
            initNotifications();
        }
        return notificationList;
    }

    // 添加新通知 (供其他地方调用)
    public void addNotification(String title, String content) {
        if (notificationList == null) initNotifications();
        notificationList.add(0, new Notification(title, content, "刚刚"));
    }

    // --- 【新增】获取“我的点赞”列表 ---
    public List<Post> getMyLikedPosts() {
        List<Post> result = new ArrayList<>();
        for (Post p : communityPosts) {
            if (p.isLiked()) { // 只要 isLiked 为 true，说明是我点的赞
                result.add(p);
            }
        }
        return result;
    }

    // --- 【新增】获取“我的收藏”列表 ---
    public List<Post> getMyFavoritePosts() {
        List<Post> result = new ArrayList<>();
        for (Post p : communityPosts) {
            if (p.isFavorited()) { // 只要 isFavorited 为 true
                result.add(p);
            }
        }
        return result;
    }
}