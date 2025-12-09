package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        // 第一道防线：如果布局文件有问题，这里会捕获，不让 APP 闪退
        try {
            return inflater.inflate(R.layout.fragment_profile, container, false);
        } catch (Exception e) {
            e.printStackTrace();
            // 在屏幕上显示错误原因
            Toast.makeText(getContext(), "布局加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 第二道防线：防止代码空指针崩溃
        try {
            super.onViewCreated(view, savedInstanceState);

            // 1. 尝试寻找控件
            TextView tvName = view.findViewById(R.id.tv_username);
            RadioGroup rg = view.findViewById(R.id.rg_gender);
            Button btnLogout = view.findViewById(R.id.btn_logout);

            // 2. 检查是否真的找到了 (关键步骤！)
            if (tvName == null) {
                throw new RuntimeException("找不到 tv_username，请检查 xml");
            }
            if (rg == null) {
                throw new RuntimeException("找不到 rg_gender，请检查 xml");
            }
            if (btnLogout == null) {
                throw new RuntimeException("找不到 btn_logout，请检查 xml");
            }

            // 3. 安全地获取数据
            String user = DataManager.getInstance().getLoggedInUser();
            tvName.setText(user != null ? user : "游客");

            String gender = DataManager.getInstance().getGender();
            if ("Male".equalsIgnoreCase(gender)) {
                rg.check(R.id.rb_male);
            } else {
                rg.check(R.id.rb_female);
            }

            // 4. 设置监听器
            rg.setOnCheckedChangeListener((group, checkedId) -> {
                String newGender = (checkedId == R.id.rb_male) ? "Male" : "Female";
                DataManager.getInstance().setGender(newGender);
                Toast.makeText(getContext(), "偏好已更新", Toast.LENGTH_SHORT).show();
            });

            btnLogout.setOnClickListener(v -> {
                DataManager.getInstance().logout();
                Toast.makeText(getContext(), "已安全退出", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            // 捕获所有错误，用弹窗显示出来，而不是直接闪退
            e.printStackTrace();
            String err = "运行错误: " + e.getMessage();
            Log.e("ProfileError", err);
            Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
        }
    }
}