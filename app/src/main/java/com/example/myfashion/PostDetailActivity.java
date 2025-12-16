package com.example.myfashion;

import android.os.Bundle;
import android.view.LayoutInflater;
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

public class PostDetailActivity extends AppCompatActivity {

    private Post currentPost;
    private LinearLayout layoutComments;
    private CheckBox cbLike, cbFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 1. 获取传递过来的 Post 索引
        int postIndex = getIntent().getIntExtra("post_index", -1);
        currentPost = DataManager.getInstance().getPostByIndex(postIndex);

        if (currentPost == null) {
            finish(); // 数据错误，关闭页面
            return;
        }

        // 2. 绑定控件
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvUser = findViewById(R.id.tv_detail_user);
        TextView tvContent = findViewById(R.id.tv_detail_content);
        ImageView ivImage = findViewById(R.id.iv_detail_image);
        cbLike = findViewById(R.id.cb_like);
        cbFavorite = findViewById(R.id.cb_favorite);
        layoutComments = findViewById(R.id.layout_comments);
        EditText etComment = findViewById(R.id.et_comment);
        Button btnSend = findViewById(R.id.btn_send_comment);

        // 3. 填充数据
        tvUser.setText(currentPost.getUserName());
        tvContent.setText(currentPost.getContent());
        Glide.with(this).load(currentPost.getImageUrl()).into(ivImage);

        // 4. 初始化状态 (点赞/收藏)
        updateStatusUI();

        // 5. 点击事件：点赞
        cbLike.setOnClickListener(v -> {
            boolean isChecked = cbLike.isChecked();
            currentPost.setLiked(isChecked);
            // 更新点赞数
            int count = currentPost.getLikeCount();
            currentPost.setLikeCount(isChecked ? count + 1 : count - 1);
            updateStatusUI();
        });

        // 6. 点击事件：收藏
        cbFavorite.setOnClickListener(v -> {
            currentPost.setFavorited(cbFavorite.isChecked());
            String msg = cbFavorite.isChecked() ? "已收藏" : "取消收藏";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // 7. 发送评论
        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                String myName = DataManager.getInstance().getNickname();
                currentPost.addComment(myName + ": " + text); // 格式：昵称: 内容
                etComment.setText("");
                refreshComments(); // 刷新列表
                Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
            }
        });

        // 8. 首次加载评论
        refreshComments();
    }

    private void updateStatusUI() {
        cbLike.setChecked(currentPost.isLiked());
        cbLike.setText(" " + currentPost.getLikeCount());
        cbFavorite.setChecked(currentPost.isFavorited());
    }

    // 动态生成评论视图 (简化版)
    private void refreshComments() {
        layoutComments.removeAllViews(); // 清空旧的
        for (String comment : currentPost.getComments()) {
            TextView tv = new TextView(this);
            tv.setText(comment);
            tv.setPadding(0, 10, 0, 10);
            tv.setTextSize(14);
            layoutComments.addView(tv);
        }
    }
}