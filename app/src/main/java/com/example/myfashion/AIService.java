package com.example.myfashion;

import android.os.Handler;
import android.os.Looper;

public class AIService {
    // 演示模式开关：True则不联网，直接返回成功
    private static final boolean DEMO_MODE = true;

    public interface AICallback {
        void onSuccess(String resultUrl);
        void onError(String msg);
    }

    // 更新：增加 garmentImg 参数
    public static void tryOn(String userImg, String garmentImg, AICallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (DEMO_MODE) {
                // 模拟成功，返回一个网图作为结果
                // 实际开发中，这里会把 userImg 和 garmentImg 发送到服务器
                callback.onSuccess("https://img.zcool.cn/community/01f4095e966f3fa801216518175d33.jpg");
            } else {
                callback.onError("API Key未配置");
            }
        }, 2000); // 模拟耗时2秒
    }
}