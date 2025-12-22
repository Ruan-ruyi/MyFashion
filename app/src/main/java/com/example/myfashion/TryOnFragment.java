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
import java.util.List;

public class TryOnFragment extends Fragment {
    private ImageView ivOrigin, ivGarment, ivResult;
    private ProgressBar loading;
    private Outfit selectedOutfit = null; // 当前选中的服装
    private Uri userImageUri = null;      // 用户上传的照片URI

    // 图片选择器
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    userImageUri = uri;
                    ivOrigin.setImageURI(uri);
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

        // 1. 上传照片
        btnUpload.setOnClickListener(v -> pickImage.launch("image/*"));

        // 2. 选择衣柜中的服装
        btnSelectGarment.setOnClickListener(v -> showWardrobeDialog());

        // 3. 开始换装
        btnStart.setOnClickListener(v -> {
            if (userImageUri == null) {
                Toast.makeText(getContext(), "请先上传本人照片", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedOutfit == null) {
                Toast.makeText(getContext(), "请先选择目标服装", Toast.LENGTH_SHORT).show();
                return;
            }

            loading.setVisibility(View.VISIBLE);

            // 传递用户图片URI字符串和选中的服装资源ID(转字符串)给AI服务
            // 实际项目中可能需要将资源图片转为Base64或上传后的URL
            String garmentIdStr = String.valueOf(selectedOutfit.getImageResId());

            AIService.tryOn(userImageUri.toString(), garmentIdStr, new AIService.AICallback() {
                @Override
                public void onSuccess(String resultUrl) {
                    loading.setVisibility(View.GONE);
                    Glide.with(TryOnFragment.this).load(resultUrl).into(ivResult);
                    Toast.makeText(getContext(), "换装成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String msg) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 显示衣柜选择弹窗
    private void showWardrobeDialog() {
        Dialog dialog = new Dialog(getContext());
        // 复用 activity_my_post_list 的布局，因为它包含标题栏和RecyclerView
        // 如果想更美观，可以新建一个 dialog_select_outfit.xml
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_my_post_list, null);
        dialog.setContentView(dialogView);

        // 设置弹窗宽高等属性
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        TextView tvEmpty = dialogView.findViewById(R.id.tv_empty);
        RecyclerView rv = dialogView.findViewById(R.id.rv_list);
        View btnBack = dialogView.findViewById(R.id.btn_back);

        tvTitle.setText("选择穿搭");
        btnBack.setOnClickListener(v -> dialog.dismiss());

        // 获取收藏数据
        List<Outfit> favoriteList = DataManager.getInstance().getMyFavoriteOutfits();

        if (favoriteList == null || favoriteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("衣柜空空如也，快去收藏吧！");
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);

            // 使用网格布局
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

            OutfitAdapter adapter = new OutfitAdapter(favoriteList, outfit -> {
                // 选中回调
                selectedOutfit = outfit;
                // 更新UI显示选中的衣服
                ivGarment.setPadding(0,0,0,0); // 清除padding以完整显示
                Glide.with(this).load(outfit.getImageResId()).into(ivGarment);
                dialog.dismiss();
            });
            rv.setAdapter(adapter);
        }

        dialog.show();
    }
}