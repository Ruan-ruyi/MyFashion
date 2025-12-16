package com.example.myfashion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    // 【新增】定义一个启动器，用来打开相册并接收结果
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 【新增】注册相册选择器
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            // 保存图片的路径到 DataManager
                            DataManager.getInstance().setCustomAvatarUri(selectedImage.toString());
                            refreshUI();
                            Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        setupItem(R.id.row_preference, "穿搭偏好");
        setupItem(R.id.row_avatar, "修改头像");
        setupItem(R.id.row_nickname, "修改昵称");
        setupItem(R.id.row_birthday, "设置生日");
        setupItem(R.id.row_gender, "我的性别");

        findViewById(R.id.row_preference).setOnClickListener(v -> showPreferenceDialog());
        findViewById(R.id.row_nickname).setOnClickListener(v -> showEditNicknameDialog());
        findViewById(R.id.row_gender).setOnClickListener(v -> showGenderDialog());
        findViewById(R.id.row_avatar).setOnClickListener(v -> showAvatarDialog());
        findViewById(R.id.row_birthday).setOnClickListener(v -> showBirthdayDialog());

        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

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
        TextView tvPref = findViewById(R.id.row_preference).findViewById(R.id.tv_value);
        String pref = DataManager.getInstance().getGender();
        if ("Female".equals(pref)) tvPref.setText("只看女装");
        else if ("Male".equals(pref)) tvPref.setText("只看男装");
        else tvPref.setText("全部显示");

        TextView tvNick = findViewById(R.id.row_nickname).findViewById(R.id.tv_value);
        tvNick.setText(DataManager.getInstance().getNickname());

        TextView tvGender = findViewById(R.id.row_gender).findViewById(R.id.tv_value);
        tvGender.setText(DataManager.getInstance().getUserSelfGender());

        TextView tvBirthday = findViewById(R.id.row_birthday).findViewById(R.id.tv_value);
        tvBirthday.setText(DataManager.getInstance().getBirthday());

        TextView tvAvatar = findViewById(R.id.row_avatar).findViewById(R.id.tv_value);
        // 如果有自定义头像，显示“自定义图片”，否则显示“点击修改”
        if (DataManager.getInstance().getCustomAvatarUri() != null) {
            tvAvatar.setText("自定义图片");
        } else {
            tvAvatar.setText("点击修改");
        }
    }

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

    private void showAvatarDialog() {
        // 【新增】第一个选项改为“从相册选择”
        String[] names = {"📷 从相册选择...", "默认头像", "时尚女装", "商务男装", "街头潮男", "优雅晚礼", "秋季风衣", "度假风", "简约白T"};

        // 后面的资源ID保持不变
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
                    if (which == 0) {
                        // 【关键】点击了第一项，打开相册
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickImageLauncher.launch(intent);
                    } else {
                        // 点击了后面预设的头像 (注意索引要减1，因为多了一个选项)
                        DataManager.getInstance().setAvatarResId(resIds[which - 1]);
                        refreshUI();
                        Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showBirthdayDialog() {
        Calendar c = Calendar.getInstance();
        String currentBirthday = DataManager.getInstance().getBirthday();
        try {
            String[] parts = currentBirthday.split("-");
            c.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        } catch (Exception e) {}

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            DataManager.getInstance().setBirthday(date);
            refreshUI();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}