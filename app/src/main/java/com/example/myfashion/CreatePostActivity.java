package com.example.myfashion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private String selectedImageUri; // 保存选中的图片路径

    private final ActivityResultLauncher<Intent> pickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedImageUri = uri.toString();
                        ivPreview.setImageURI(uri);
                        ivPreview.setPadding(0,0,0,0); // 清除默认图标的padding
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        ivPreview = findViewById(R.id.iv_preview);
        EditText etContent = findViewById(R.id.et_content);
        Button btnPublish = findViewById(R.id.btn_publish);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 点击选图
        ivPreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickerLauncher.launch(intent);
        });

        // 点击发布
        btnPublish.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "写点什么吧...", Toast.LENGTH_SHORT).show();
                return;
            }
            // 调用 DataManager 保存帖子
            DataManager.getInstance().addNewPost(content, selectedImageUri);
            Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
            finish(); // 关闭页面，返回列表
        });
    }
}