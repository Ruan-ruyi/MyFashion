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

        // 2. 初始化列表 (保持原有逻辑)
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        allOutfits = DataManager.getInstance().getOutfits();
        adapter = new OutfitAdapter(allOutfits, outfit -> {
            Intent intent = new Intent(getActivity(), OutfitDetailActivity.class);
            intent.putExtra("title", outfit.getTitle());
            intent.putExtra("imageResId", outfit.getImageResId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 3. 搜索功能 (保持原有逻辑)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { filter(s.toString()); }
        });

        // 4. 【新增】城市点击切换功能
        tvCity.setOnClickListener(v -> showCityPicker());

        // 5. 【新增】尝试自动定位
        startLocationService();
    }

    // --- 核心功能 1: 城市选择弹窗 ---
    private void showCityPicker() {
        final String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市", "武汉市", "西安市"};

        new AlertDialog.Builder(getContext())
                .setTitle("切换城市")
                .setItems(cities, (dialog, which) -> {
                    String selectedCity = cities[which];
                    updateWeatherUI(selectedCity); // 选中后更新
                    Toast.makeText(getContext(), "已切换到 " + selectedCity, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // --- 核心功能 2: 更新界面 UI ---
    private void updateWeatherUI(String city) {
        tvCity.setText(city);
        // 使用我们写的模拟器获取天气，以后这里可以换成真实 API 调用
        String weatherInfo = WeatherSimulator.getWeatherForCity(city);
        tvWeather.setText(weatherInfo);
    }

    // --- 核心功能 3: 定位服务 ---
    private void startLocationService() {
        if (getActivity() == null) return;
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 检查权限
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没权限，申请权限
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        // 获取最后一次已知位置 (快速显示)
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            updateCityFromLocation(lastKnownLocation);
        } else {
            tvCity.setText("定位中...");
        }

        // 监听位置变化 (实时更新)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateCityFromLocation(location);
                // 定位成功一次后，为了省电可以移除监听
                locationManager.removeUpdates(this);
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onProviderDisabled(@NonNull String provider) {}
        });
    }

    // 将经纬度转换为城市名 (反地理编码)
    private void updateCityFromLocation(Location location) {
        if (getContext() == null) return;
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.CHINA);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality(); // 获取城市名 (如：北京市)
                if (city == null) city = addresses.get(0).getSubAdminArea(); // 某些地区 city 为空

                if (city != null) {
                    final String finalCity = city;
                    // 确保在主线程更新 UI
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateWeatherUI(finalCity));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果定位失败（模拟器常见），默认显示北京
            if (tvCity.getText().toString().contains("定位")) {
                updateWeatherUI("北京市");
            }
        }
    }

    // 权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService(); // 授权成功，开始定位
            } else {
                Toast.makeText(getContext(), "需要定位权限才能显示当地天气", Toast.LENGTH_SHORT).show();
                updateWeatherUI("北京市"); // 拒绝后默认显示北京
            }
        }
    }

    private void filter(String text) {
        List<Outfit> filtered = new ArrayList<>();
        for (Outfit o : allOutfits) {
            if (o.getTitle().toLowerCase().contains(text.toLowerCase())) filtered.add(o);
        }
        if (adapter != null) adapter.updateData(filtered);
    }
}