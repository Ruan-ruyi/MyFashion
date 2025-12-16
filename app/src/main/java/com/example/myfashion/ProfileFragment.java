package com.example.myfashion;

import android.content.Intent;
import android.net.Uri;
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
    private ImageView ivAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (view == null) return;

        tvName = view.findViewById(R.id.tv_username);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        setupMenuItem(view, R.id.menu_favorites, "我的收藏", android.R.drawable.btn_star_big_off);
        setupMenuItem(view, R.id.menu_likes, "我的点赞", android.R.drawable.btn_star_big_on);
        setupMenuItem(view, R.id.menu_notifications, "消息通知", android.R.drawable.ic_dialog_email);
        setupMenuItem(view, R.id.menu_settings, "设置", android.R.drawable.ic_menu_preferences);

        view.findViewById(R.id.menu_settings).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        View.OnClickListener toastListener = v -> Toast.makeText(getContext(), "功能开发中...", Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.menu_favorites).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_likes).setOnClickListener(toastListener);
        view.findViewById(R.id.menu_notifications).setOnClickListener(toastListener);

        updateUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (tvName != null) {
            tvName.setText(DataManager.getInstance().getNickname());
        }

        if (ivAvatar != null) {
            // 【关键修改】优先检查是否有自定义头像 URI
            String customAvatarUri = DataManager.getInstance().getCustomAvatarUri();

            if (customAvatarUri != null) {
                // 加载相册图片
                Glide.with(this)
                        .load(Uri.parse(customAvatarUri))
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivAvatar);
            } else {
                // 加载内置头像
                int avatarResId = DataManager.getInstance().getAvatarResId();
                Glide.with(this)
                        .load(avatarResId)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivAvatar);
            }
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