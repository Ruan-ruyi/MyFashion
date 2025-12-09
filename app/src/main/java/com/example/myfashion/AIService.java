package com.example.myfashion;

import android.os.Handler;
import android.os.Looper;

public class AIService {
    // 演示模式开关：True则不联网，直接返回成功（防止演示翻车）
    private static final boolean DEMO_MODE = true;

    public interface AICallback {
        void onSuccess(String resultUrl);
        void onError(String msg);
    }

    public static void tryOn(String base64Img, AICallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (DEMO_MODE) {
                // 模拟成功，返回一个网图作为结果
                callback.onSuccess("https://img.zcool.cn/community/01f4095e966f3fa801216518175d33.jpg");
            } else {
                // 这里可以填入真实的OkHttp请求代码
                callback.onError("API Key未配置");
            }
        }, 2000); // 模拟耗时2秒
    }
}