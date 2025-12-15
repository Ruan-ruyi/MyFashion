package com.example.myfashion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 初始化每一行
        setupItem(R.id.row_preference, "偏好设置", R.drawable.ic_launcher_foreground); // 这里的图标后面可以换
        setupItem(R.id.row_avatar, "头像设置", R.drawable.ic_launcher_foreground);
        setupItem(R.id.row_nickname, "昵称设置", R.drawable.ic_launcher_foreground);
        setupItem(R.id.row_birthday, "生日设置", R.drawable.ic_launcher_foreground);
        setupItem(R.id.row_gender, "性别设置", R.drawable.ic_launcher_foreground);

        // --- 1. 偏好设置点击逻辑 ---
        findViewById(R.id.row_preference).setOnClickListener(v -> showPreferenceDialog());

        // --- 2. 其他设置点击 (暂做演示) ---
        View.OnClickListener demoListener = v -> Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        findViewById(R.id.row_avatar).setOnClickListener(demoListener);
        findViewById(R.id.row_nickname).setOnClickListener(demoListener);
        findViewById(R.id.row_birthday).setOnClickListener(demoListener);
        findViewById(R.id.row_gender).setOnClickListener(demoListener);

        // --- 3. 退出登录 ---
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        updatePreferenceText();
    }

    // 辅助方法：设置行的标题和图标
    private void setupItem(int viewId, String title, int iconRes) {
        View view = findViewById(viewId);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setVisibility(View.GONE); // 这里设置页面为了简洁，暂时隐藏左侧图标，如果你需要显示把这行删掉
    }

    // 显示偏好选择弹窗
    private void showPreferenceDialog() {
        String[] options = {"女装 (Female)", "男装 (Male)", "所有 (All)"};
        String[] values = {"Female", "Male", "All"};

        new AlertDialog.Builder(this)
                .setTitle("选择穿搭偏好")
                .setItems(options, (dialog, which) -> {
                    DataManager.getInstance().setGender(values[which]);
                    updatePreferenceText();
                    Toast.makeText(this, "已切换为: " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 更新右侧文字显示当前状态
    private void updatePreferenceText() {
        View view = findViewById(R.id.row_preference);
        TextView tvValue = view.findViewById(R.id.tv_value);
        String current = DataManager.getInstance().getGender();
        if ("Female".equals(current)) tvValue.setText("女装");
        else if ("Male".equals(current)) tvValue.setText("男装");
        else tvValue.setText("所有");
    }
}