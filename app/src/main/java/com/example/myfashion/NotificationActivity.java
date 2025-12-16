package com.example.myfashion;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_list); // 复用之前的列表布局

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("消息通知");
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 使用 NotificationAdapter
        NotificationAdapter adapter = new NotificationAdapter(DataManager.getInstance().getNotifications());
        rv.setAdapter(adapter);
    }
}