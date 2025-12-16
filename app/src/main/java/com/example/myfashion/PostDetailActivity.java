package com.example.myfashion;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PostDetailActivity extends AppCompatActivity {

    private Post currentPost;
    private LinearLayout layoutComments;
    private CheckBox cbLike, cbFavorite;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        int postIndex = getIntent().getIntExtra("post_index", -1);
        currentPost = DataManager.getInstance().getPostByIndex(postIndex);

        if (currentPost == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvUser = findViewById(R.id.tv_detail_user);
        TextView tvContent = findViewById(R.id.tv_detail_content);
        ImageView ivImage = findViewById(R.id.iv_detail_image);
        ivAvatar = findViewById(R.id.iv_detail_avatar); // 绑定头像

        cbLike = findViewById(R.id.cb_like);
        cbFavorite = findViewById(R.id.cb_favorite);
        layoutComments = findViewById(R.id.layout_comments);
        EditText etComment = findViewById(R.id.et_comment);
        Button btnSend = findViewById(R.id.btn_send_comment);

        // 1. 设置文本数据
        tvUser.setText(currentPost.getUserName());
        tvContent.setText(currentPost.getContent());

        // 2. 【核心修复】头像加载逻辑 (复用适配器的逻辑)
        loadAvatar(currentPost.getUserName());

        // 3. 【核心修复】图片加载逻辑 (解决 "自己传的图不显示" 问题)
        String imgUrl = currentPost.getImageUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);

            // 判断是否需要转 Uri
            Object loadObj = imgUrl;
            if (!imgUrl.startsWith("http")) {
                loadObj = Uri.parse(imgUrl);
            }

            Glide.with(this)
                    .load(loadObj)
                    .apply(new RequestOptions()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.stat_notify_error)) // 加载失败显示红叹号
                    .into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }

        // 4. 更新点赞/收藏状态
        updateStatusUI();

        // 5. 点击事件
        cbLike.setOnClickListener(v -> {
            boolean isChecked = cbLike.isChecked();
            currentPost.setLiked(isChecked);
            int count = currentPost.getLikeCount();
            currentPost.setLikeCount(isChecked ? count + 1 : count - 1);
            updateStatusUI();
        });

        cbFavorite.setOnClickListener(v -> {
            currentPost.setFavorited(cbFavorite.isChecked());
            String msg = cbFavorite.isChecked() ? "已收藏" : "取消收藏";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                String myName = DataManager.getInstance().getNickname();
                currentPost.addComment(myName + ": " + text);
                etComment.setText("");
                refreshComments();
                Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
            }
        });

        refreshComments();
    }

    // 单独提取头像加载逻辑
    private void loadAvatar(String postUserName) {
        String currentNick = DataManager.getInstance().getNickname();

        if (postUserName.equals(currentNick)) {
            String myAvatarUri = DataManager.getInstance().getCustomAvatarUri();
            if (myAvatarUri != null) {
                Glide.with(this).load(Uri.parse(myAvatarUri)).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
            } else {
                Glide.with(this).load(DataManager.getInstance().getAvatarResId()).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
            }
        } else {
            int[] randomAvatars = {R.drawable.o1, R.drawable.o2, R.drawable.o7, R.drawable.o8};
            int index = postUserName.length() % randomAvatars.length;
            Glide.with(this).load(randomAvatars[index]).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
        }
    }

    private void updateStatusUI() {
        cbLike.setChecked(currentPost.isLiked());
        cbLike.setText(" " + currentPost.getLikeCount());
        cbFavorite.setChecked(currentPost.isFavorited());

        // 选中变红，未选中变灰
        if (currentPost.isLiked()) {
            cbLike.setTextColor(Color.parseColor("#FF2442")); // 红色
        } else {
            cbLike.setTextColor(Color.parseColor("#666666")); // 灰色
        }
    }

    private void refreshComments() {
        layoutComments.removeAllViews();
        for (String comment : currentPost.getComments()) {
            TextView tv = new TextView(this);
            tv.setText(comment);
            tv.setPadding(0, 12, 0, 12);
            tv.setTextSize(15);
            tv.setTextColor(Color.parseColor("#333333"));
            layoutComments.addView(tv);

            // 加个分割线
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
            layoutComments.addView(divider);
        }
    }
}