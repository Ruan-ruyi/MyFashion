package com.example.myfashion;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;

public class OutfitDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_detail);

        // 修改点：接收 int 类型的数据，默认值设为 0
        String title = getIntent().getStringExtra("title");
        int imgResId = getIntent().getIntExtra("imageResId", 0);

        ImageView iv = findViewById(R.id.iv_detail_image);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        Toolbar toolbar = findViewById(R.id.toolbar);

        tvTitle.setText(title);

        // 修改点：加载 int 资源
        if (imgResId != 0) {
            Glide.with(this).load(imgResId).into(iv);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }
}