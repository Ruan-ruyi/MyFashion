package com.example.myfashion;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class ImageUploader {
    // 【请修改】填入你在 ImgBB 申请的 API Key
    private static final String API_KEY = "47ab91e30c5be72b02ccccaa4bff07aa";

    // ImgBB 上传接口 (Key 直接拼在 URL 后面)
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload?key=" + API_KEY;

    // 设置较长的超时时间，防止上传大图超时
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface UploadCallback {
        void onSuccess(String onlineUrl);
        void onError(String msg);
    }

    public static void upload(File file, UploadCallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // 构建请求体
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                // ImgBB 要求参数名必须叫 "image"
                .addFormDataPart("image", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("网络错误: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                Log.d("ImageUploader", "Response: " + respStr);

                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onError("服务器错误: " + response.code()));
                    return;
                }

                try {
                    // 解析 ImgBB 返回的 JSON
                    JSONObject json = new JSONObject(respStr);
                    boolean success = json.optBoolean("success");

                    if (success) {
                        // 提取图片 URL
                        String url = json.getJSONObject("data").getString("url");
                        mainHandler.post(() -> callback.onSuccess(url));
                    } else {
                        // 提取错误信息
                        String errorMsg = "上传失败";
                        if (json.has("error")) {
                            errorMsg = json.getJSONObject("error").getString("message");
                        }
                        final String finalMsg = errorMsg;
                        mainHandler.post(() -> callback.onError(finalMsg));
                    }

                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("解析失败: " + e.getMessage()));
                }
            }
        });
    }
}