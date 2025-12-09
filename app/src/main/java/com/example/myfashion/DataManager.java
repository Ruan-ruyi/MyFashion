package com.example.myfashion;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Outfit> outfitList;
    private String currentGender = "Female"; // 默认女性

    private DataManager() {
        outfitList = new ArrayList<>();
        // 模拟数据：你可以把这些URL换成你找到的真实图片链接
        outfitList.add(new Outfit("春季碎花裙", "https://img.zcool.cn/community/01f4095e966f3fa801216518175d33.jpg", "Female"));
        outfitList.add(new Outfit("商务西装", "https://img.zcool.cn/community/01a4e95d5b6e6ba8012187f4c07923.jpg", "Male"));
        outfitList.add(new Outfit("休闲T恤", "https://img.zcool.cn/community/018c645d94121da8012060be7ea45f.jpg", "Female"));
        outfitList.add(new Outfit("运动卫衣", "https://img.zcool.cn/community/010c795d94122fa801211d5308b49e.jpg", "Male"));
        // 复制更多行以丰富列表...
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

    public String getGender() { return currentGender; }
    public void setGender(String gender) { this.currentGender = gender; }
}