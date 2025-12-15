package com.example.myfashion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginMode = true;

    private LinearLayout layoutWelcome;
    private CardView cardForm;
    private TextView tvLogin, tvRegister;
    private EditText etUser, etPwd, etPhone;
    private Button btnAction;
    private ImageButton btnClose; // 新增关闭按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        setContentView(R.layout.activity_login);

        // 1. 绑定控件
        layoutWelcome = findViewById(R.id.layout_welcome);
        cardForm = findViewById(R.id.card_form);

        Button btnStartLogin = findViewById(R.id.btn_start_login);
        Button btnStartRegister = findViewById(R.id.btn_start_register);
        btnClose = findViewById(R.id.btn_close_card); // 绑定关闭按钮

        tvLogin = findViewById(R.id.tv_tab_login);
        tvRegister = findViewById(R.id.tv_tab_register);
        etUser = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        btnAction = findViewById(R.id.btn_action);

        // 2. 交互逻辑
        // 点击底部按钮显示表单
        btnStartLogin.setOnClickListener(v -> showForm(true));
        btnStartRegister.setOnClickListener(v -> showForm(false));

        // 点击关闭按钮隐藏表单 (新增)
        btnClose.setOnClickListener(v -> hideForm());

        // 3. Tab 切换
        View.OnClickListener tabListener = v -> {
            if (v.getId() == R.id.tv_tab_login) {
                switchToLoginMode();
            } else {
                switchToRegisterMode();
            }
        };
        tvLogin.setOnClickListener(tabListener);
        tvRegister.setOnClickListener(tabListener);

        // 4. 提交按钮
        btnAction.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPwd.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLoginMode) {
                if (DataManager.getInstance().checkLogin(u, p)) {
                    DataManager.getInstance().login(u);
                    Toast.makeText(this, "欢迎回来, " + u, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (phone.isEmpty()) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (DataManager.getInstance().register(u, p, phone)) {
                    Toast.makeText(this, "注册成功!", Toast.LENGTH_SHORT).show();
                    switchToLoginMode();
                    etPwd.setText("");
                } else {
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showForm(boolean isLogin) {
        layoutWelcome.setVisibility(View.GONE);
        cardForm.setVisibility(View.VISIBLE);
        if (isLogin) {
            switchToLoginMode();
        } else {
            switchToRegisterMode();
        }
    }

    // 隐藏表单，回到欢迎页
    private void hideForm() {
        cardForm.setVisibility(View.GONE);
        layoutWelcome.setVisibility(View.VISIBLE);
        // 收起键盘
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (cardForm.getVisibility() == View.VISIBLE) {
            hideForm();
        } else {
            super.onBackPressed();
        }
    }

    private void switchToLoginMode() {
        isLoginMode = true;
        updateTabStyle(tvLogin, true);
        updateTabStyle(tvRegister, false);
        etPhone.setVisibility(View.GONE);
        btnAction.setText("立即登录");
    }

    private void switchToRegisterMode() {
        isLoginMode = false;
        updateTabStyle(tvRegister, true);
        updateTabStyle(tvLogin, false);
        etPhone.setVisibility(View.VISIBLE);
        btnAction.setText("立即注册");
    }

    private void updateTabStyle(TextView tab, boolean isActive) {
        if (isActive) {
            tab.setTextColor(ContextCompat.getColor(this, R.color.login_text_dark));
            tab.getPaint().setFakeBoldText(true);
        } else {
            tab.setTextColor(ContextCompat.getColor(this, R.color.login_text_light));
            tab.getPaint().setFakeBoldText(false);
        }
        tab.invalidate();
    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}