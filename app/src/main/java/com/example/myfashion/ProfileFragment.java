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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (view == null) return;

        // 1. 设置用户信息
        TextView tvName = view.findViewById(R.id.tv_username);
        String user = DataManager.getInstance().getLoggedInUser();
        tvName.setText(user != null ? user : "游客");

        // 2. 初始化菜单项
        // 参数：ID, 标题, 图标资源(这里暂时用系统的星号代替，你可以换成自己的 R.drawable.xxx)
        setupMenuItem(view, R.id.menu_favorites, "我的收藏", android.R.drawable.btn_star_big_off);
        setupMenuItem(view, R.id.menu_likes, "我的点赞", android.R.drawable.btn_star_big_on); // 暂时用星号代替心形
        setupMenuItem(view, R.id.menu_notifications, "消息通知", android.R.drawable.ic_dialog_email);
        setupMenuItem(view, R.id.menu_settings, "设置", android.R.drawable.ic_menu_preferences);

        // 3. 设置点击事件
        view.findViewById(R.id.menu_settings).setOnClickListener(v -> {
            // 跳转到设置页
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        View.OnClickListener toastListener = v -> Toast.makeText(getContext(), "功能开发中...", Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.menu_favorites).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_likes).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_notifications).setOnClickListener(toastListener);
    }

    private void setupMenuItem(View rootView, int itemId, String title, int iconRes) {
        View itemView = rootView.findViewById(itemId);
        TextView tv = itemView.findViewById(R.id.tv_title);
        ImageView iv = itemView.findViewById(R.id.iv_icon);

        tv.setText(title);
        iv.setImageResource(iconRes);
        // 如果想让图标变灰色，可以加: iv.setImageTintList(ColorStateList.valueOf(Color.GRAY));
    }
}