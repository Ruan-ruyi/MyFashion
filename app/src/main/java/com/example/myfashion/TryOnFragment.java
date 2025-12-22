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
    private Outfit selectedOutfit = null;
    private Uri userImageUri = null;

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

        btnUpload.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSelectGarment.setOnClickListener(v -> showWardrobeDialog());

        btnStart.setOnClickListener(v -> {
            // 校验
            if (userImageUri == null) {
                Toast.makeText(getContext(), "请先上传本人照片", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedOutfit == null) {
                Toast.makeText(getContext(), "请先选择目标服装", Toast.LENGTH_SHORT).show();
                return;
            }

            loading.setVisibility(View.VISIBLE);

            // 【注意】这里是关键点！
            // 真实的 App 需要先将 userImageUri 上传到服务器拿到 URL。
            // 同样的，selectedOutfit 里的资源 ID (R.drawable.xx) 也需要对应一个公网 URL。

            // 为了演示 API 能跑通，这里我们使用 Python 脚本里的示例公网 URL
            // 实际上你应该传:
            // String userUrl = uploadImageAndGetUrl(userImageUri);
            // String clothUrl = selectedOutfit.getNetworkUrl();

            String demoUserUrl = "https://ark-project.tos-cn-beijing.volces.com/doc_image/seedream4_imagesToimage_1.png";
            String demoClothUrl = "https://ark-project.tos-cn-beijing.volces.com/doc_image/seedream4_imagesToimage_2.png";

            Toast.makeText(getContext(), "正在请求云端 AI...", Toast.LENGTH_LONG).show();

            // 调用我们写好的 AIService
            AIService.tryOn(demoUserUrl, demoClothUrl, new AIService.AICallback() {
                @Override
                public void onSuccess(String resultUrl) {
                    loading.setVisibility(View.GONE);
                    // 使用 Glide 加载返回的 URL
                    Glide.with(TryOnFragment.this)
                            .load(resultUrl)
                            .into(ivResult);
                    Toast.makeText(getContext(), "换装成功！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String msg) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "失败: " + msg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // 显示衣柜选择弹窗 (保持不变)
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
        tvTitle.setText("选择穿搭");
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
                ivGarment.setPadding(0,0,0,0);
                Glide.with(this).load(outfit.getImageResId()).into(ivGarment);
                dialog.dismiss();
            });
            rv.setAdapter(adapter);
        }
        dialog.show();
    }
}