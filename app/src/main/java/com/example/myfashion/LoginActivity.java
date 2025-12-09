package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // 引入这个库来获取 colors.xml 里的颜色

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginMode = true; // 记录当前是登录模式还是注册模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. 绑定控件
        TextView tvLogin = findViewById(R.id.tv_tab_login);
        TextView tvRegister = findViewById(R.id.tv_tab_register);
        EditText etUser = findViewById(R.id.et_username);
        EditText etPwd = findViewById(R.id.et_password);
        EditText etPhone = findViewById(R.id.et_phone);
        Button btnAction = findViewById(R.id.btn_action);

        // 2. 定义 Tab 切换逻辑
        View.OnClickListener tabListener = v -> {
            if (v.getId() == R.id.tv_tab_login) {
                // === 切换到登录模式 ===
                isLoginMode = true;

                // 样式调整：登录变黑(选中)，注册变灰(未选中)
                updateTabStyle(tvLogin, true);
                updateTabStyle(tvRegister, false);

                etPhone.setVisibility(View.GONE); // 隐藏手机号
                btnAction.setText("立即登录");
            } else {
                // === 切换到注册模式 ===
                isLoginMode = false;

                // 样式调整：注册变黑(选中)，登录变灰(未选中)
                updateTabStyle(tvRegister, true);
                updateTabStyle(tvLogin, false);

                etPhone.setVisibility(View.VISIBLE); // 显示手机号
                btnAction.setText("立即注册");
            }
        };

        // 绑定点击事件
        tvLogin.setOnClickListener(tabListener);
        tvRegister.setOnClickListener(tabListener);

        // 初始化默认状态（登录模式）
        updateTabStyle(tvLogin, true);
        updateTabStyle(tvRegister, false);


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

    /**
     * 辅助方法：更新 Tab 的选中样式
     * 使用 colors.xml 中定义的颜色
     * @param tab TextView 控件
     * @param isActive 是否为选中状态
     */
    private void updateTabStyle(TextView tab, boolean isActive) {
        if (isActive) {
            // 选中状态：使用 text_primary (深色)，加粗
            tab.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tab.getPaint().setFakeBoldText(true);
        } else {
            // 未选中状态：使用 text_secondary (浅色)，不加粗
            tab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tab.getPaint().setFakeBoldText(false);
        }
        tab.invalidate(); // 刷新绘制
    }
}