package com.example.myfashion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView tvCity = view.findViewById(R.id.tv_city);
        TextView tvTemp = view.findViewById(R.id.tv_temp);
        Button btnSwitch = view.findViewById(R.id.btn_switch);

        // 默认显示
        tvCity.setText("北京市");
        tvTemp.setText("22°C 晴朗");

        btnSwitch.setOnClickListener(v -> {
            tvCity.setText("上海市");
            tvTemp.setText("26°C 多云");
        });
    }
}