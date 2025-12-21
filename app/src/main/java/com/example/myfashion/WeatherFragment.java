package com.example.myfashion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment {

    private TextView tvCity;
    private TextView tvTemp;
    private Button btnSwitch;

    // 原生定位管理器
    private LocationManager locationManager;
    // 线程池，用于处理网络请求（解析城市名），防止卡顿
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCity = view.findViewById(R.id.tv_city);
        tvTemp = view.findViewById(R.id.tv_temp);
        btnSwitch = view.findViewById(R.id.btn_switch);

        // 初始化 LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // 默认状态
        tvCity.setText("等待定位...");

        // 尝试定位
        checkPermissionAndLocate();

        btnSwitch.setText("刷新定位");
        btnSwitch.setOnClickListener(v -> checkPermissionAndLocate());
    }

    // 权限请求逻辑
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fineLocationGranted != null && fineLocationGranted) ||
                        (coarseLocationGranted != null && coarseLocationGranted)) {
                    startLocation();
                } else {
                    tvCity.setText("无权限");
                    Toast.makeText(getContext(), "请授予定位权限", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkPermissionAndLocate() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            // 有权限，直接开始
            tvCity.setText("正在定位...");
            startLocation();
        }
    }

    private void startLocation() {
        try {
            // 1. 先尝试获取最后一次已知位置 (速度快)
            Location lastKnownLocation = null;
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (lastKnownLocation != null) {
                // 如果有历史位置，直接使用
                parseCity(lastKnownLocation);
            } else {
                // 2. 如果没有历史位置，请求一次单次定位更新
                // 注意：这里简单使用 Network Provider (WiFi/基站)，室内更准
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // 模拟器通常需要 GPS
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                } else {
                    tvCity.setText("定位服务未开启");
                    Toast.makeText(getContext(), "请打开手机 GPS", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            tvCity.setText("权限错误");
        }
    }

    // 监听定位结果
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            parseCity(location);
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {}
        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    // 把经纬度转成城市名 (放到子线程做，防止卡死 UI)
    private void parseCity(Location location) {
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            String resultCity = "未知城市";
            String resultWeather = "";

            try {
                // 尝试解析地址
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // 优先取市，如果是直辖市可能在 AdminArea
                    if (address.getLocality() != null) {
                        resultCity = address.getLocality();
                    } else {
                        resultCity = address.getAdminArea();
                    }
                } else {
                    // 解析失败，显示经纬度兜底
                    resultCity = String.format(Locale.getDefault(), "%.1f, %.1f", location.getLatitude(), location.getLongitude());
                }

                // 获取模拟天气
                resultWeather = WeatherSimulator.getWeatherForCity(resultCity);

            } catch (IOException e) {
                e.printStackTrace();
                // 网络错误时，显示经纬度或者默认城市
                resultCity = "网络连接失败";
                resultWeather = "暂无数据";
            }

            // 切回主线程更新 UI
            String finalCity = resultCity;
            String finalWeather = resultWeather;
            mainHandler.post(() -> {
                tvCity.setText(finalCity);
                tvTemp.setText(finalWeather);
                if ("网络连接失败".equals(finalCity)) {
                    Toast.makeText(getContext(), "无法解析城市名称，请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}