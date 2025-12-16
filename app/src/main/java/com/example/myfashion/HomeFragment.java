package com.example.myfashion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private OutfitAdapter adapter;
    private List<Outfit> allOutfits;
    private TextView tvCity, tvWeather;

    // 【新增】标签筛选相关变量
    private LinearLayout llTagsContainer;
    private final String[] tags = {"全部", "春季", "夏季", "秋季", "冬季", "通勤", "休闲", "约会", "度假", "运动"};
    private String currentTag = "全部";

    // 定位相关
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 1. 初始化控件
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        tvCity = view.findViewById(R.id.tv_city);
        tvWeather = view.findViewById(R.id.tv_weather);
        EditText etSearch = view.findViewById(R.id.et_search);
        // 【新增】绑定标签容器
        llTagsContainer = view.findViewById(R.id.ll_tags_container);

        // 2. 初始化列表
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        // 初始显示全部数据
        allOutfits = DataManager.getInstance().getOutfits();
        adapter = new OutfitAdapter(allOutfits, outfit -> {
            Intent intent = new Intent(getActivity(), OutfitDetailActivity.class);
            intent.putExtra("title", outfit.getTitle());
            intent.putExtra("imageResId", outfit.getImageResId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 3. 搜索功能 (输入文字时进行标题过滤)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { filter(s.toString()); }
        });

        // 4. 城市点击切换功能
        tvCity.setOnClickListener(v -> showCityPicker());

        // 5. 尝试自动定位
        startLocationService();

        // 6. 【新增】初始化筛选标签
        initFilterTags();
    }

    // --- 核心功能 0: 标签筛选逻辑 ---
    private void initFilterTags() {
        llTagsContainer.removeAllViews();
        for (String tag : tags) {
            TextView tv = new TextView(getContext());
            tv.setText(tag);
            tv.setTextSize(14);
            tv.setPadding(40, 16, 40, 16);

            // 设置标签的间距
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 24, 0); // 右边距
            tv.setLayoutParams(params);

            // 初始化样式 (根据是否是当前选中的标签)
            updateTagStyle(tv, tag.equals(currentTag));

            // 点击事件
            tv.setOnClickListener(v -> {
                currentTag = tag;
                // 1. 刷新所有标签的样式
                for (int i = 0; i < llTagsContainer.getChildCount(); i++) {
                    View child = llTagsContainer.getChildAt(i);
                    if (child instanceof TextView) {
                        TextView t = (TextView) child;
                        updateTagStyle(t, t.getText().toString().equals(currentTag));
                    }
                }
                // 2. 执行数据筛选
                filterByTag(currentTag);
            });

            llTagsContainer.addView(tv);
        }
    }

    // 辅助方法：更新标签样式（选中变黑，未选中变灰/白）
    private void updateTagStyle(TextView tv, boolean isSelected) {
        if (isSelected) {
            tv.setTextColor(getResources().getColor(R.color.white));
            // 使用之前上传的黑色圆角背景资源
            tv.setBackgroundResource(R.drawable.btn_black_rounded);
        } else {
            tv.setTextColor(getResources().getColor(R.color.text_primary)); // 或 Color.BLACK
            // 使用之前上传的白色描边背景资源
            tv.setBackgroundResource(R.drawable.btn_outline_white);
        }
    }

    // 根据标签筛选数据
    private void filterByTag(String tag) {
        // 调用 DataManager 中新加的方法
        List<Outfit> filteredList = DataManager.getInstance().getOutfitsByTag(tag);
        if (adapter != null) {
            adapter.updateData(filteredList);
        }
    }

    // --- 核心功能 1: 城市选择弹窗 ---
    private void showCityPicker() {
        final String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市", "武汉市", "西安市"};

        new AlertDialog.Builder(getContext())
                .setTitle("切换城市")
                .setItems(cities, (dialog, which) -> {
                    String selectedCity = cities[which];
                    updateWeatherUI(selectedCity);
                    Toast.makeText(getContext(), "已切换到 " + selectedCity, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // --- 核心功能 2: 更新界面 UI ---
    private void updateWeatherUI(String city) {
        tvCity.setText(city);
        String weatherInfo = WeatherSimulator.getWeatherForCity(city);
        tvWeather.setText(weatherInfo);
    }

    // --- 核心功能 3: 定位服务 ---
    private void startLocationService() {
        if (getActivity() == null) return;
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            updateCityFromLocation(lastKnownLocation);
        } else {
            tvCity.setText("定位中...");
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateCityFromLocation(location);
                locationManager.removeUpdates(this);
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onProviderDisabled(@NonNull String provider) {}
        });
    }

    private void updateCityFromLocation(Location location) {
        if (getContext() == null) return;
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.CHINA);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                if (city == null) city = addresses.get(0).getSubAdminArea();

                if (city != null) {
                    final String finalCity = city;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateWeatherUI(finalCity));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (tvCity.getText().toString().contains("定位")) {
                updateWeatherUI("北京市");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(getContext(), "需要定位权限才能显示当地天气", Toast.LENGTH_SHORT).show();
                updateWeatherUI("北京市");
            }
        }
    }

    // 搜索框筛选 (注意：这里是基于当前显示的全部数据进行标题搜索)
    private void filter(String text) {
        List<Outfit> filtered = new ArrayList<>();
        // 为了简单，搜索时我们还是在所有数据里搜，不局限于当前选中的标签
        // 如果想局限于标签，可以用 DataManager.getInstance().getOutfitsByTag(currentTag) 替换 allOutfits
        for (Outfit o : allOutfits) {
            if (o.getTitle().toLowerCase().contains(text.toLowerCase())) filtered.add(o);
        }
        if (adapter != null) adapter.updateData(filtered);
    }
}