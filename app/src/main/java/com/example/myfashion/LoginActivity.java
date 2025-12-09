package com.example.myfashion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginMode = true; // 记录当前是登录模式还是注册模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. 绑定控件 (注意这里使用的是新布局中的 ID)
        TextView tvLogin = findViewById(R.id.tv_tab_login);
        TextView tvRegister = findViewById(R.id.tv_tab_register);
        EditText etUser = findViewById(R.id.et_username);
        EditText etPwd = findViewById(R.id.et_password);
        EditText etPhone = findViewById(R.id.et_phone);
        // 关键点：这里用的 ID 是 btn_action，不是 btn_login
        Button btnAction = findViewById(R.id.btn_action);

        // 2. 处理“登录/注册” Tab 切换点击事件
        View.OnClickListener tabListener = v -> {
            if (v.getId() == R.id.tv_tab_login) {
                // 切换到登录模式
                isLoginMode = true;
                updateTabStyle(tvLogin, tvRegister);
                etPhone.setVisibility(View.GONE); // 隐藏手机号
                btnAction.setText("立即登录");
            } else {
                // 切换到注册模式
                isLoginMode = false;
                updateTabStyle(tvRegister, tvLogin);
                etPhone.setVisibility(View.VISIBLE); // 显示手机号
                btnAction.setText("立即注册");
            }
        };

        tvLogin.setOnClickListener(tabListener);
        tvRegister.setOnClickListener(tabListener);

        // 3. 处理按钮点击事件 (登录或注册)
        btnAction.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPwd.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLoginMode) {
                // --- 执行登录逻辑 ---
                if (DataManager.getInstance().checkLogin(u, p)) {
                    DataManager.getInstance().login(u);
                    Toast.makeText(this, "登录成功，欢迎 " + u, Toast.LENGTH_SHORT).show();

                    // 跳转主页
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                // --- 执行注册逻辑 ---
                if (phone.isEmpty()) {
                    Toast.makeText(this, "注册需要填写手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = DataManager.getInstance().register(u, p, phone);
                if (success) {
                    Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_SHORT).show();
                    // 注册成功后，自动切回登录模式
                    tvLogin.performClick();
                    etPwd.setText(""); // 清空密码以便重新输入
                } else {
                    Toast.makeText(this, "该用户名已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 辅助方法：更新 Tab 的选中样式（变色、加粗）
    private void updateTabStyle(TextView active, TextView inactive) {
        active.setTextColor(Color.parseColor("#6200EE"));
        active.getPaint().setFakeBoldText(true);
        active.invalidate(); // 刷新绘制

        inactive.setTextColor(Color.parseColor("#999999"));
        inactive.getPaint().setFakeBoldText(false);
        inactive.invalidate();
    }
}