package com.example.myfashion;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AIService {
    // 【重要】请将此处替换为你申请的真实 API Key
    private static final String API_KEY = "sk-ByhFGiFVuRTWPTwgcOYM23LLKRnnX29cQSEFLLRbPPgimLCX";
    private static final String API_URL = "https://www.dmxapi.cn/v1/images/generations";

    // 各种超时设置，AI 生成图片比较慢，建议设长一点
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface AICallback {
        void onSuccess(String resultUrl);
        void onError(String msg);
    }

    /**
     * 发起换装请求
     * @param userImgUrl  图1：用户照片的公网 URL (必须是 http/https 开头)
     * @param garmentImgUrl 图2：服装照片的公网 URL (必须是 http/https 开头)
     * @param callback    回调
     */
    public static void tryOn(String userImgUrl, String garmentImgUrl, AICallback callback) {
        // 主线程 Handler，用于将结果切回主线程更新 UI
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // 1. 构建 JSON 请求体
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "doubao-seedream-4-5-251128"); // 使用 Python 代码中的最新模型
            jsonBody.put("prompt", "将图1的服装换为图2的服装"); // 核心提示词
            jsonBody.put("size", "4K"); // 分辨率

            // 组装 image 数组
            JSONArray images = new JSONArray();
            images.put(userImgUrl);    // 图1
            images.put(garmentImgUrl); // 图2
            jsonBody.put("image", images);

        } catch (JSONException e) {
            callback.onError("JSON 组装失败: " + e.getMessage());
            return;
        }

        // 2. 创建 HTTP 请求
        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 3. 发送异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d("AIService", "Response: " + responseStr);

                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onError("API 报错: " + response.code() + " - " + responseStr));
                    return;
                }

                try {
                    // 4. 解析返回的 JSON
                    // 结构：{ "data": [ { "url": "..." } ], ... }
                    JSONObject respJson = new JSONObject(responseStr);
                    JSONArray dataArray = respJson.optJSONArray("data");

                    if (dataArray != null && dataArray.length() > 0) {
                        String resultUrl = dataArray.getJSONObject(0).getString("url");
                        mainHandler.post(() -> callback.onSuccess(resultUrl));
                    } else {
                        mainHandler.post(() -> callback.onError("未返回图片 URL"));
                    }

                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onError("解析响应失败: " + e.getMessage()));
                }
            }
        });
    }
}