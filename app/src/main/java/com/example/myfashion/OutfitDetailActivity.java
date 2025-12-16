package com.example.myfashion;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class OutfitDetailActivity extends AppCompatActivity {

    private Outfit currentOutfit;
    private ImageView ivFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_detail);

        // 1. 获取传递的数据
        String title = getIntent().getStringExtra("title");
        int imgResId = getIntent().getIntExtra("imageResId", 0);

        // 【核心新增】通过标题查找真实的 Outfit 对象，以便操作收藏状态
        currentOutfit = DataManager.getInstance().getOutfitByTitle(title);

        // 2. 绑定控件
        ImageView iv = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        ivFavorite = findViewById(R.id.iv_favorite); // 绑定收藏按钮
        Toolbar toolbar = findViewById(R.id.toolbar);

        tvTitle.setText(title);

        // 3. 加载图片 (禁用缓存以修复潜在的加载问题)
        if (imgResId != 0) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(android.R.drawable.stat_notify_error)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(this)
                    .load(imgResId)
                    .apply(options)
                    .into(iv);
        } else {
            Toast.makeText(this, "图片加载失败 (ID=0)", Toast.LENGTH_SHORT).show();
        }

        // 4. 【核心新增】处理收藏逻辑
        if (currentOutfit != null) {
            updateFavoriteIcon(); // 初始化图标状态

            ivFavorite.setOnClickListener(v -> {
                // 切换状态
                boolean newState = !currentOutfit.isFavorite();
                currentOutfit.setFavorite(newState);

                // 更新 UI
                updateFavoriteIcon();

                // 提示用户
                String msg = newState ? "已放入衣柜" : "已移出衣柜";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // 根据当前状态更新爱心图标
    private void updateFavoriteIcon() {
        if (currentOutfit.isFavorite()) {
            ivFavorite.setImageResource(R.drawable.ic_heart_filled); // 需要实心红心资源
            ivFavorite.setColorFilter(getResources().getColor(android.R.color.holo_red_light));
        } else {
            ivFavorite.setImageResource(R.drawable.ic_heart_outline); // 需要空心心形资源
            ivFavorite.clearColorFilter(); // 清除颜色滤镜，恢复默认色
        }
    }
}