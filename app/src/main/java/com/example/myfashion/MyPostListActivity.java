package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyPostListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_list);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        RecyclerView rv = findViewById(R.id.rv_list);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 获取页面类型 (是看收藏还是看点赞？)
        String type = getIntent().getStringExtra("TYPE");
        List<Post> dataList;

        if ("LIKES".equals(type)) {
            tvTitle.setText("我的点赞");
            dataList = DataManager.getInstance().getMyLikedPosts();
        } else {
            tvTitle.setText("我的收藏");
            dataList = DataManager.getInstance().getMyFavoritePosts();
        }

        // 设置列表
        if (dataList == null || dataList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));

            // 复用 CommunityAdapter
            CommunityAdapter adapter = new CommunityAdapter(dataList, position -> {
                // 点击跳转详情 (注意：这里需要传 post 在原列表中的真实 index，
                // 但为了简单，我们暂时无法反向查找 index，所以这里仅作展示，或者你可以传递 Post 对象到详情页)
                // 简化处理：点击仅提示，或者由于是 Post 对象引用，点进去修改后返回刷新可能需要更复杂逻辑
                // 这里我们暂且不支持从"收藏页"点进详情再修改状态的实时同步，仅作展示
            });
            rv.setAdapter(adapter);
        }
    }
}