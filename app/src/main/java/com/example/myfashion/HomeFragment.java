package com.example.myfashion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化天气显示 (这里使用静态数据模拟，实际可扩展为定位)
        TextView tvCity = view.findViewById(R.id.tv_city);
        TextView tvWeather = view.findViewById(R.id.tv_weather);
        tvCity.setText("上海");
        tvWeather.setText("26°C 多云");

        // 2. 初始化列表
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // 获取数据
        allOutfits = DataManager.getInstance().getOutfits();
        adapter = new OutfitAdapter(allOutfits);
        rv.setAdapter(adapter);

        // 3. 实现搜索功能
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    // 搜索过滤逻辑
    private void filter(String text) {
        List<Outfit> filteredList = new ArrayList<>();
        for (Outfit item : allOutfits) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (adapter != null) {
            adapter.updateData(filteredList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面刷新数据 (比如在设置里改了性别后)
        if (adapter != null) {
            allOutfits = DataManager.getInstance().getOutfits();
            adapter.updateData(allOutfits);
        }
    }
}