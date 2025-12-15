package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private OutfitAdapter adapter;
    private List<Outfit> allOutfits; // 保存所有数据，用于搜索恢复

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        // 设置瀑布流布局
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // 获取数据
        allOutfits = DataManager.getInstance().getOutfits();

        // 初始化适配器，并处理点击事件
        adapter = new OutfitAdapter(allOutfits, outfit -> {
            // 点击跳转到详情页
            Intent intent = new Intent(getActivity(), OutfitDetailActivity.class);
            intent.putExtra("title", outfit.getTitle());
            // 修改点：传递的数据变成 int 类型
            intent.putExtra("imageResId", outfit.getImageResId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 处理搜索框输入
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 实时搜索：每次输入文字变化，都调用过滤方法
                filter(s.toString());
            }
        });
    }

    // 搜索过滤逻辑
    private void filter(String text) {
        List<Outfit> filtered = new ArrayList<>();
        for (Outfit o : allOutfits) {
            // 如果标题包含输入的文字 (忽略大小写)
            if (o.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(o);
            }
        }
        // 更新列表显示
        if (adapter != null) {
            adapter.updateData(filtered);
        }
    }
}