package com.example.myfashion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class HomeFragment extends Fragment {
    private OutfitAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new OutfitAdapter(DataManager.getInstance().getOutfits());
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面，重新读取数据（以防性别改变）
        if (adapter != null) {
            adapter.updateData(DataManager.getInstance().getOutfits());
        }
    }
}