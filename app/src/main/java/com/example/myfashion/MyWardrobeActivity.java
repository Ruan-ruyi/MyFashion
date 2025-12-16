package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyWardrobeActivity extends AppCompatActivity {
    private OutfitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 复用通用的列表布局，如果没有可以直接用 activity_my_post_list 的布局或者新建
        // 这里假设我们复用 activity_my_post_list.xml (它包含 title, back button, recyclerview)
        setContentView(R.layout.activity_my_post_list);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        RecyclerView rv = findViewById(R.id.rv_list);

        tvTitle.setText("我的衣柜");
        tvEmpty.setText("还没有收藏任何穿搭哦~");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 获取收藏的穿搭数据
        List<Outfit> favoriteList = DataManager.getInstance().getMyFavoriteOutfits();

        if (favoriteList == null || favoriteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);

            // 使用瀑布流布局，和首页一致
            rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            adapter = new OutfitAdapter(favoriteList, outfit -> {
                // 点击跳转详情
                Intent intent = new Intent(this, OutfitDetailActivity.class);
                intent.putExtra("title", outfit.getTitle());
                intent.putExtra("imageResId", outfit.getImageResId());
                startActivity(intent);
            });
            rv.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到页面刷新数据（万一在详情页取消收藏了，回来得消失）
        List<Outfit> list = DataManager.getInstance().getMyFavoriteOutfits();
        if (adapter != null) {
            adapter.updateData(list);
            // 处理空状态显隐...
        }
    }
}