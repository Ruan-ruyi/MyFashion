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
        // 安全检查：防止视图未创建就调用
        if (view == null) return;

        TextView tvName = view.findViewById(R.id.tv_username);
        RadioGroup rg = view.findViewById(R.id.rg_gender);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        // 1. 获取并显示用户名 (增加判空)
        String user = DataManager.getInstance().getLoggedInUser();
        if (user != null) {
            tvName.setText(user);
        } else {
            tvName.setText("游客");
        }

        // 2. 初始化性别选择状态
        String gender = DataManager.getInstance().getGender();
        if ("Male".equalsIgnoreCase(gender)) {
            rg.check(R.id.rb_male);
        } else {
            rg.check(R.id.rb_female);
        }

        // 3. 监听性别修改
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            String newGender = (checkedId == R.id.rb_male) ? "Male" : "Female";
            DataManager.getInstance().setGender(newGender);
            Toast.makeText(getContext(), "偏好已更新为: " + newGender, Toast.LENGTH_SHORT).show();
        });

        // 4. 退出登录逻辑
        btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Toast.makeText(getContext(), "已安全退出", Toast.LENGTH_SHORT).show();

            // 跳转回登录页，并清空栈（防止按返回键回来）
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}