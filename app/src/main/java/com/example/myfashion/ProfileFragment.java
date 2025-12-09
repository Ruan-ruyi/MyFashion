package com.example.myfashion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RadioGroup rg = view.findViewById(R.id.rg_gender);

        // 读取当前状态
        if ("Male".equals(DataManager.getInstance().getGender())) {
            rg.check(R.id.rb_male);
        } else {
            rg.check(R.id.rb_female);
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            String g = (checkedId == R.id.rb_male) ? "Male" : "Female";
            DataManager.getInstance().setGender(g);
            Toast.makeText(getContext(), "设置已保存", Toast.LENGTH_SHORT).show();
        });
    }
}