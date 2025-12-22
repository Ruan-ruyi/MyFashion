package com.example.myfashion;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;

public class TryOnFragment extends Fragment {
    private ImageView ivOrigin, ivGarment, ivResult;
    private ProgressBar loading;
    private Outfit selectedOutfit = null; // 选中的衣服
    private Uri userImageUri = null;      // 用户上传的照片Uri

    // 相册选择器
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    userImageUri = uri;
                    ivOrigin.setImageURI(uri);
                    ivResult.setImageDrawable(null); // 清空旧结果
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_try_on, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ivOrigin = view.findViewById(R.id.iv_origin);
        ivGarment = view.findViewById(R.id.iv_garment);
        ivResult = view.findViewById(R.id.iv_result);
        loading = view.findViewById(R.id.loading);

        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button btnSelectGarment = view.findViewById(R.id.btn_select_garment);
        Button btnStart = view.findViewById(R.id.btn_start);

        btnUpload.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSelectGarment.setOnClickListener(v -> showWardrobeDialog());

        // --- 核心逻辑：点击开始换装 ---
        btnStart.setOnClickListener(v -> {
            if (userImageUri == null) {
                Toast.makeText(getContext(), "请先上传本人照片", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedOutfit == null) {
                Toast.makeText(getContext(), "请先选择目标服装", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. 界面显示加载中
            loading.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "正在处理图片...", Toast.LENGTH_SHORT).show();

            // 2. 将图片转为 File
            File userFile = FileUtil.uriToFile(getContext(), userImageUri);
            File clothFile = FileUtil.drawableToFile(getContext(), selectedOutfit.getImageResId());

            if (userFile == null || clothFile == null) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "图片转换失败，请重试", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 开始链式上传 (人像 -> 衣服 -> AI)

            // Step A: 上传人像
            ImageUploader.upload(userFile, new ImageUploader.UploadCallback() {
                @Override
                public void onSuccess(String userOnlineUrl) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "人像上传成功，正在上传服装...", Toast.LENGTH_SHORT).show()
                    );

                    // Step B: 上传服装
                    ImageUploader.upload(clothFile, new ImageUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String clothOnlineUrl) {
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "正在请求 AI 生成，请耐心等待...", Toast.LENGTH_LONG).show()
                            );

                            // Step C: 调用 AI 换装
                            AIService.tryOn(userOnlineUrl, clothOnlineUrl, new AIService.AICallback() {
                                @Override
                                public void onSuccess(String resultUrl) {
                                    if (getActivity() == null) return;
                                    getActivity().runOnUiThread(() -> {
                                        loading.setVisibility(View.GONE);
                                        // 显示结果
                                        Glide.with(TryOnFragment.this).load(resultUrl).into(ivResult);
                                        Toast.makeText(getContext(), "换装成功！", Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onError(String msg) {
                                    if (getActivity() == null) return;
                                    getActivity().runOnUiThread(() -> {
                                        loading.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "AI 生成失败: " + msg, Toast.LENGTH_LONG).show();
                                    });
                                }
                            });
                        }

                        @Override
                        public void onError(String msg) {
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "服装上传失败: " + msg, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }

                @Override
                public void onError(String msg) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "人像上传失败: " + msg, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    // 显示衣柜弹窗
    private void showWardrobeDialog() {
        Dialog dialog = new Dialog(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_my_post_list, null);
        dialog.setContentView(dialogView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        TextView tvEmpty = dialogView.findViewById(R.id.tv_empty);
        RecyclerView rv = dialogView.findViewById(R.id.rv_list);
        View btnBack = dialogView.findViewById(R.id.btn_back);

        tvTitle.setText("请选择一件服装");
        btnBack.setOnClickListener(v -> dialog.dismiss());

        List<Outfit> favoriteList = DataManager.getInstance().getMyFavoriteOutfits();

        if (favoriteList == null || favoriteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("衣柜空空如也，快去收藏吧！");
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

            OutfitAdapter adapter = new OutfitAdapter(favoriteList, outfit -> {
                selectedOutfit = outfit;
                Glide.with(this).load(outfit.getImageResId()).into(ivGarment);
                ivGarment.setPadding(0,0,0,0);
                dialog.dismiss();
            });
            rv.setAdapter(adapter);
        }
        dialog.show();
    }
}