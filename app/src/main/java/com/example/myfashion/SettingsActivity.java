package com.example.myfashion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        // 初始化菜单项
        setupItem(R.id.row_preference, "穿搭偏好 (看男装/女装)");
        setupItem(R.id.row_avatar, "修改头像");
        setupItem(R.id.row_nickname, "修改昵称");
        setupItem(R.id.row_birthday, "设置生日");
        setupItem(R.id.row_gender, "我的性别");

        // 1. 穿搭偏好
        findViewById(R.id.row_preference).setOnClickListener(v -> showPreferenceDialog());

        // 2. 修改昵称 (核心逻辑)
        findViewById(R.id.row_nickname).setOnClickListener(v -> showEditNicknameDialog());

        // 3. 修改性别 (核心逻辑)
        findViewById(R.id.row_gender).setOnClickListener(v -> showGenderDialog());

        // 4. 其他暂未实现的功能
        View.OnClickListener demoListener = v -> Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        findViewById(R.id.row_avatar).setOnClickListener(demoListener);
        findViewById(R.id.row_birthday).setOnClickListener(demoListener);

        // 退出登录
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 关键点：进入页面时，刷新显示，从 DataManager 读取最新数据
        refreshUI();
    }

    private void setupItem(int viewId, String title) {
        View view = findViewById(viewId);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setVisibility(View.GONE);
    }

    // 刷新界面文字 (从 DataManager 获取数据)
    private void refreshUI() {
        // 1. 刷新偏好
        TextView tvPref = findViewById(R.id.row_preference).findViewById(R.id.tv_value);
        String pref = DataManager.getInstance().getGender();
        if ("Female".equals(pref)) tvPref.setText("只看女装");
        else if ("Male".equals(pref)) tvPref.setText("只看男装");
        else tvPref.setText("全部显示");

        // 2. 【关键】刷新昵称
        TextView tvNick = findViewById(R.id.row_nickname).findViewById(R.id.tv_value);
        tvNick.setText(DataManager.getInstance().getNickname());

        // 3. 【关键】刷新我的性别
        TextView tvGender = findViewById(R.id.row_gender).findViewById(R.id.tv_value);
        tvGender.setText(DataManager.getInstance().getUserSelfGender());
    }

    // --- 弹窗逻辑 ---

    // 1. 偏好设置弹窗
    private void showPreferenceDialog() {
        String[] options = {"只看女装", "只看男装", "全部显示"};
        String[] values = {"Female", "Male", "All"};
        new AlertDialog.Builder(this)
                .setTitle("首页显示内容")
                .setItems(options, (dialog, which) -> {
                    DataManager.getInstance().setGender(values[which]); // 保存到 DataManager
                    refreshUI();
                    Toast.makeText(this, "首页内容已更新", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 2. 修改昵称弹窗
    private void showEditNicknameDialog() {
        EditText input = new EditText(this);
        input.setHint("请输入新昵称");
        input.setText(DataManager.getInstance().getNickname()); // 显示当前昵称
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("修改昵称")
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        DataManager.getInstance().setNickname(newName); // 【关键】保存到 DataManager
                        refreshUI(); // 刷新界面
                        Toast.makeText(this, "昵称已保存", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 3. 修改性别弹窗
    private void showGenderDialog() {
        String[] options = {"男", "女", "保密"};
        new AlertDialog.Builder(this)
                .setTitle("选择您的性别")
                .setItems(options, (dialog, which) -> {
                    DataManager.getInstance().setUserSelfGender(options[which]); // 【关键】保存到 DataManager
                    refreshUI(); // 刷新界面
                })
                .show();
    }
}