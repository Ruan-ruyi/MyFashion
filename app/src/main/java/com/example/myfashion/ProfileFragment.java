package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 1. 设置用户名显示
        TextView tvUsername = view.findViewById(R.id.tv_username);
        String user = DataManager.getInstance().getLoggedInUser();
        tvUsername.setText(user != null ? user : "未登录用户");

        // 2. 性别设置逻辑 (保持原有)
        RadioGroup rg = view.findViewById(R.id.rg_gender);
        if ("Male".equals(DataManager.getInstance().getGender())) {
            rg.check(R.id.rb_male);
        } else {
            rg.check(R.id.rb_female);
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            String g = (checkedId == R.id.rb_male) ? "Male" : "Female";
            DataManager.getInstance().setGender(g);
            Toast.makeText(getContext(), "设置已保存", Toast.LENGTH_SHORT).show();
        });

        // 3. 退出登录逻辑
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            // 清除登录状态
            DataManager.getInstance().logout();
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();

            // 跳转回登录页
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // 清空任务栈，防止用户点返回键又回到 App 内部
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}