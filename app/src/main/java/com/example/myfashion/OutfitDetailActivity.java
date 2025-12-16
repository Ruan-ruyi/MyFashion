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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_detail);

        // 1. 获取传递的数据
        String title = getIntent().getStringExtra("title");
        int imgResId = getIntent().getIntExtra("imageResId", 0);

        // 2. 绑定控件
        ImageView iv = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        Toolbar toolbar = findViewById(R.id.toolbar);

        tvTitle.setText(title);

        // 3. 【核心修复】强制加载图片并禁用缓存
        if (imgResId != 0) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background) // 加载中显示
                    .error(android.R.drawable.stat_notify_error)    // 加载失败显示红叹号 (方便排查)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      // ❌ 禁用磁盘缓存
                    .skipMemoryCache(true);                         // ❌ 跳过内存缓存

            Glide.with(this)
                    .load(imgResId)
                    .apply(options)
                    .into(iv);
        } else {
            // 如果 ID 为 0，说明数据传递有问题
            Toast.makeText(this, "图片加载失败 (ID=0)", Toast.LENGTH_SHORT).show();
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }
}