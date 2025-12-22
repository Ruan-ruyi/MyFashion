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
    // 【请修改】这里填入火山引擎/豆包的 API Key (Bearer Token)
    private static final String API_KEY = "sk-ByhFGiFVuRTWPTwgcOYM23LLKRnnX29cQSEFLLRbPPgimLCX";

    // 豆包图生图 API 地址
    private static final String API_URL = "https://www.dmxapi.cn/v1/images/generations";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // 生成图片很慢，建议设为 120秒
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface AICallback {
        void onSuccess(String resultUrl);
        void onError(String msg);
    }

    public static void tryOn(String userImgUrl, String garmentImgUrl, AICallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // 1. 构建 JSON 参数
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "doubao-seedream-4-5-251128"); // 模型版本
            jsonBody.put("prompt", "将图1的服装换为图2的服装"); // 提示词
            jsonBody.put("size", "4K"); // 分辨率

            // 图片数组: [用户图, 衣服图]
            JSONArray images = new JSONArray();
            images.put(userImgUrl);
            images.put(garmentImgUrl);
            jsonBody.put("image", images);

        } catch (JSONException e) {
            callback.onError("JSON 组装失败: " + e.getMessage());
            return;
        }

        // 2. 创建请求
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

        // 3. 发送请求
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
                    // 解析结果
                    JSONObject respJson = new JSONObject(responseStr);
                    JSONArray dataArray = respJson.optJSONArray("data");

                    if (dataArray != null && dataArray.length() > 0) {
                        String resultUrl = dataArray.getJSONObject(0).getString("url");
                        mainHandler.post(() -> callback.onSuccess(resultUrl));
                    } else {
                        mainHandler.post(() -> callback.onError("未返回图片数据"));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onError("解析响应失败: " + e.getMessage()));
                }
            }
        });
    }
}