package com.example.myfashion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
        rv.setLayoutManager(new LinearLayoutManager(getContext())); // 线性布局

        // 2. 获取数据并设置适配器
        // 注意：请确保 DataManager 中已经添加了 getCommunityPosts() 方法
        List<Post> posts = DataManager.getInstance().getCommunityPosts();
        adapter = new CommunityAdapter(posts);
        rv.setAdapter(adapter);

        // 3. 处理悬浮按钮点击 (模拟发帖)
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            String currentUser = DataManager.getInstance().getLoggedInUser();
            if (currentUser == null) currentUser = "我";

            // 创建一条新帖子 (插到列表最前面)
            Post newPost = new Post(currentUser, "刚刚分享了一个超棒的穿搭想法！✨", "", 0);

            // 调用 DataManager 添加数据
            DataManager.getInstance().addPost(newPost);

            // 刷新列表
            adapter.updateData(DataManager.getInstance().getCommunityPosts());

            // 滚动到顶部
            rv.smoothScrollToPosition(0);

            Toast.makeText(getContext(), "发布成功！", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面刷新数据
        if (adapter != null) {
            adapter.updateData(DataManager.getInstance().getCommunityPosts());
        }
    }
}