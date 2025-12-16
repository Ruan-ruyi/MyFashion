package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class CommunityFragment extends Fragment {

    private CommunityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化 RecyclerView
        RecyclerView rv = view.findViewById(R.id.rv_community);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. 获取数据
        List<Post> posts = DataManager.getInstance().getCommunityPosts();

        // 3. 初始化适配器 (传入点击监听器)
        adapter = new CommunityAdapter(posts, position -> {
            // 点击事件：跳转到帖子详情页
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            // 传递帖子的索引，详情页根据索引去 DataManager 查数据
            intent.putExtra("post_index", position);
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 4. 处理悬浮按钮点击 (跳转发帖)
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            // 跳转到发帖页面
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 5. 每次回到页面刷新数据
        // (比如在详情页点赞了，或者发了新帖，这里需要更新列表显示)
        if (adapter != null) {
            adapter.updateData(DataManager.getInstance().getCommunityPosts());
        }
    }
}