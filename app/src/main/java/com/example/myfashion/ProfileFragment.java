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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private ImageView ivAvatar; // 新增头像控件变量

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
        ivAvatar = view.findViewById(R.id.iv_avatar); // 绑定头像 ImageView

        // 2. 初始化菜单项
        setupMenuItem(view, R.id.menu_favorites, "我的收藏", android.R.drawable.btn_star_big_off);
        setupMenuItem(view, R.id.menu_likes, "我的点赞", android.R.drawable.btn_star_big_on);
        setupMenuItem(view, R.id.menu_notifications, "消息通知", android.R.drawable.ic_dialog_email);
        setupMenuItem(view, R.id.menu_settings, "设置", android.R.drawable.ic_menu_preferences);

        // 3. 点击事件
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

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到这个页面，都刷新数据
        updateUserInfo();
    }

    private void updateUserInfo() {
        // 更新昵称
        if (tvName != null) {
            tvName.setText(DataManager.getInstance().getNickname());
        }

        // 【新增】更新头像
        if (ivAvatar != null) {
            int avatarResId = DataManager.getInstance().getAvatarResId();
            // 使用 Glide 加载圆形头像
            Glide.with(this)
                    .load(avatarResId)
                    .apply(RequestOptions.circleCropTransform()) // 自动裁剪成圆形
                    .into(ivAvatar);
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