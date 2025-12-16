package com.example.myfashion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private ImageView ivAvatar;

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
        ivAvatar = view.findViewById(R.id.iv_avatar);

        // 2. 初始化菜单项显示 (文字+图标)
        setupMenuItem(view, R.id.menu_favorites, "我的收藏", android.R.drawable.btn_star_big_off);
        setupMenuItem(view, R.id.menu_likes, "我的点赞", android.R.drawable.btn_star_big_on);
        setupMenuItem(view, R.id.menu_notifications, "消息通知", android.R.drawable.ic_dialog_email);
        setupMenuItem(view, R.id.menu_settings, "设置", android.R.drawable.ic_menu_preferences);

        // 3. 设置点击事件 (这里是核心修改：从 Toast 变成了真正的 Intent 跳转)

        // 跳转 -> 设置
        view.findViewById(R.id.menu_settings).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        // 跳转 -> 我的收藏 (传递参数 TYPE = FAVORITES)
        view.findViewById(R.id.menu_favorites).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPostListActivity.class);
            intent.putExtra("TYPE", "FAVORITES");
            startActivity(intent);
        });

        // 跳转 -> 我的点赞 (传递参数 TYPE = LIKES)
        view.findViewById(R.id.menu_likes).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPostListActivity.class);
            intent.putExtra("TYPE", "LIKES");
            startActivity(intent);
        });

        // 跳转 -> 消息通知
        view.findViewById(R.id.menu_notifications).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });

        // 4. 首次加载用户信息
        updateUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面时刷新数据 (防止在设置页改了头像回来没变)
        updateUserInfo();
    }

    private void updateUserInfo() {
        // 更新昵称
        if (tvName != null) {
            tvName.setText(DataManager.getInstance().getNickname());
        }

        // 更新头像
        if (ivAvatar != null) {
            // 优先检查是否有自定义头像 (上传的)
            String customAvatarUri = DataManager.getInstance().getCustomAvatarUri();

            if (customAvatarUri != null) {
                // 加载相册图片
                try {
                    Glide.with(this)
                            .load(Uri.parse(customAvatarUri))
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivAvatar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 加载内置头像 (默认或选择的卡通图)
                int avatarResId = DataManager.getInstance().getAvatarResId();
                Glide.with(this)
                        .load(avatarResId)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivAvatar);
            }
        }
    }

    // 辅助方法：设置菜单项的标题和图标
    private void setupMenuItem(View rootView, int itemId, String title, int iconRes) {
        View itemView = rootView.findViewById(itemId);
        TextView tv = itemView.findViewById(R.id.tv_title);
        ImageView iv = itemView.findViewById(R.id.iv_icon);
        tv.setText(title);
        iv.setImageResource(iconRes);
    }
}