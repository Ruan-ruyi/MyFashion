package com.example.myfashion;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeatherSimulator {

    private static final Map<String, String[]> CITY_WEATHER_MAP = new HashMap<>();

    static {
        // 预设一些城市的天气数据 (格式：{温度, 天气状况})
        CITY_WEATHER_MAP.put("北京市", new String[]{"22°C", "晴"});
        CITY_WEATHER_MAP.put("上海市", new String[]{"25°C", "多云"});
        CITY_WEATHER_MAP.put("广州市", new String[]{"30°C", "雷阵雨"});
        CITY_WEATHER_MAP.put("深圳市", new String[]{"29°C", "多云"});
        CITY_WEATHER_MAP.put("杭州市", new String[]{"20°C", "小雨"});
        CITY_WEATHER_MAP.put("成都市", new String[]{"18°C", "阴"});
        CITY_WEATHER_MAP.put("武汉市", new String[]{"24°C", "晴"});
        CITY_WEATHER_MAP.put("西安市", new String[]{"21°C", "多云"});
    }

    /**
     * 根据城市名获取天气
     * 如果是未知城市，这就随机生成一个，模拟真实感
     */
    public static String getWeatherForCity(String city) {
        if (CITY_WEATHER_MAP.containsKey(city)) {
            String[] data = CITY_WEATHER_MAP.get(city);
            return data[0] + " " + data[1];
        } else {
            // 随机生成一个天气，防止报错
            Random random = new Random();
            int temp = 15 + random.nextInt(15); // 15~30度
            String[] conditions = {"晴", "多云", "阴", "小雨"};
            String condition = conditions[random.nextInt(conditions.length)];
            return temp + "°C " + condition;
        }
    }
}