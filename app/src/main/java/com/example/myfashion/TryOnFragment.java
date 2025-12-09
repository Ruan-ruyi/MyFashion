package com.example.myfashion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class TryOnFragment extends Fragment {
    private ImageView ivOrigin, ivResult;
    private ProgressBar loading;

    // 新版图片选择器
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) ivOrigin.setImageURI(uri);
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_try_on, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ivOrigin = view.findViewById(R.id.iv_origin);
        ivResult = view.findViewById(R.id.iv_result);
        loading = view.findViewById(R.id.loading);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button btnStart = view.findViewById(R.id.btn_start);

        btnUpload.setOnClickListener(v -> pickImage.launch("image/*"));

        btnStart.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            AIService.tryOn("base64_placeholder", new AIService.AICallback() {
                @Override
                public void onSuccess(String resultUrl) {
                    loading.setVisibility(View.GONE);
                    Glide.with(TryOnFragment.this).load(resultUrl).into(ivResult);
                    Toast.makeText(getContext(), "换装成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String msg) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}