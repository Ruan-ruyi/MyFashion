package com.example.myfashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private TextView tvId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (view == null) return;

        // 1. 绑定控件
        tvName = view.findViewById(R.id.tv_username);
        // 如果你的布局里有显示ID的TextView，可以绑定，没有的话就忽略
        // tvId = view.findViewById(R.id.tv_user_id);

        // 2. 初始化菜单项
        setupMenuItem(view, R.id.menu_favorites, "我的收藏", android.R.drawable.btn_star_big_off);
        setupMenuItem(view, R.id.menu_likes, "我的点赞", android.R.drawable.btn_star_big_on);
        setupMenuItem(view, R.id.menu_notifications, "消息通知", android.R.drawable.ic_dialog_email);
        setupMenuItem(view, R.id.menu_settings, "设置", android.R.drawable.ic_menu_preferences);

        // 3. 设置点击事件
        view.findViewById(R.id.menu_settings).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        View.OnClickListener toastListener = v -> Toast.makeText(getContext(), "功能开发中...", Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.menu_favorites).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_likes).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_notifications).setOnClickListener(toastListener);

        // 首次加载数据显示
        updateUserInfo();
    }

    // 【关键】当页面重新可见时（比如从设置页返回），刷新数据
    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (tvName != null) {
            // 使用 DataManager 里的昵称，而不是仅仅用登录名
            tvName.setText(DataManager.getInstance().getNickname());
        }
    }

    private void setupMenuItem(View rootView, int itemId, String title, int iconRes) {
        View itemView = rootView.findViewById(itemId);
        TextView tv = itemView.findViewById(R.id.tv_title);
        ImageView iv = itemView.findViewById(R.id.iv_icon);

        tv.setText(title);
        iv.setImageResource(iconRes);
    }
}