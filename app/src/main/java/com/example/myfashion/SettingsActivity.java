package com.example.myfashion;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 初始化行
        setupItem(R.id.row_preference, "穿搭偏好");
        setupItem(R.id.row_avatar, "修改头像");
        setupItem(R.id.row_nickname, "修改昵称");
        setupItem(R.id.row_birthday, "设置生日");
        setupItem(R.id.row_gender, "我的性别");

        // 1. 穿搭偏好
        findViewById(R.id.row_preference).setOnClickListener(v -> showPreferenceDialog());

        // 2. 修改昵称
        findViewById(R.id.row_nickname).setOnClickListener(v -> showEditNicknameDialog());

        // 3. 修改性别
        findViewById(R.id.row_gender).setOnClickListener(v -> showGenderDialog());

        // 4. 【新增】修改头像
        findViewById(R.id.row_avatar).setOnClickListener(v -> showAvatarDialog());

        // 5. 【新增】修改生日
        findViewById(R.id.row_birthday).setOnClickListener(v -> showBirthdayDialog());

        // 退出登录
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 刷新显示
        refreshUI();
    }

    private void setupItem(int viewId, String title) {
        View view = findViewById(viewId);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setVisibility(View.GONE);
    }

    private void refreshUI() {
        // 偏好
        TextView tvPref = findViewById(R.id.row_preference).findViewById(R.id.tv_value);
        String pref = DataManager.getInstance().getGender();
        if ("Female".equals(pref)) tvPref.setText("只看女装");
        else if ("Male".equals(pref)) tvPref.setText("只看男装");
        else tvPref.setText("全部显示");

        // 昵称
        TextView tvNick = findViewById(R.id.row_nickname).findViewById(R.id.tv_value);
        tvNick.setText(DataManager.getInstance().getNickname());

        // 性别
        TextView tvGender = findViewById(R.id.row_gender).findViewById(R.id.tv_value);
        tvGender.setText(DataManager.getInstance().getUserSelfGender());

        // 【新增】生日回显
        TextView tvBirthday = findViewById(R.id.row_birthday).findViewById(R.id.tv_value);
        tvBirthday.setText(DataManager.getInstance().getBirthday());

        // 【新增】头像文字提示 (由于头像在列表里只显示文字，我们在 Profile 页显示图片)
        TextView tvAvatar = findViewById(R.id.row_avatar).findViewById(R.id.tv_value);
        tvAvatar.setText("点击修改");
    }

    // --- 弹窗逻辑 ---

    // 1. 偏好
    private void showPreferenceDialog() {
        String[] options = {"只看女装", "只看男装", "全部显示"};
        String[] values = {"Female", "Male", "All"};
        new AlertDialog.Builder(this)
                .setTitle("首页显示内容")
                .setItems(options, (dialog, which) -> {
                    DataManager.getInstance().setGender(values[which]);
                    refreshUI();
                })
                .show();
    }

    // 2. 昵称
    private void showEditNicknameDialog() {
        EditText input = new EditText(this);
        input.setHint("请输入新昵称");
        input.setText(DataManager.getInstance().getNickname());

        new AlertDialog.Builder(this)
                .setTitle("修改昵称")
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        DataManager.getInstance().setNickname(newName);
                        refreshUI();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 3. 性别
    private void showGenderDialog() {
        String[] options = {"男", "女", "保密"};
        new AlertDialog.Builder(this)
                .setTitle("选择您的性别")
                .setItems(options, (dialog, which) -> {
                    DataManager.getInstance().setUserSelfGender(options[which]);
                    refreshUI();
                })
                .show();
    }

    // 4. 【新增】头像选择器 (内置 8 个预设头像)
    private void showAvatarDialog() {
        // 定义预设的头像名字和对应的图片资源ID
        String[] names = {"默认头像", "时尚女装", "商务男装", "街头潮男", "优雅晚礼", "秋季风衣", "度假风", "简约白T"};
        final int[] resIds = {
                R.mipmap.ic_launcher_round,
                R.drawable.o1,
                R.drawable.o2,
                R.drawable.o3,
                R.drawable.o4,
                R.drawable.o5,
                R.drawable.o7,
                R.drawable.o8
        };

        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setItems(names, (dialog, which) -> {
                    // 保存选中的头像资源ID
                    DataManager.getInstance().setAvatarResId(resIds[which]);
                    refreshUI();
                    Toast.makeText(this, "头像已更新，去【我的】页面查看效果吧！", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 5. 【新增】生日选择器 (DatePicker)
    private void showBirthdayDialog() {
        Calendar c = Calendar.getInstance();
        // 尝试解析当前保存的生日，如果格式不对就用当前日期
        String currentBirthday = DataManager.getInstance().getBirthday();
        try {
            String[] parts = currentBirthday.split("-");
            c.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        } catch (Exception e) {
            // 解析失败，忽略
        }

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            DataManager.getInstance().setBirthday(date);
            refreshUI();
            Toast.makeText(this, "生日已更新", Toast.LENGTH_SHORT).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}