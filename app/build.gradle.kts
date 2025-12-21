plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myfashion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myfashion"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // === 基础 UI 组件 ===
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // === 【关键修复】防止闪退：CardView 卡片库 ===
    // 你的 Profile 页面用了 CardView，必须加这句，否则会崩！
    implementation("androidx.cardview:cardview:1.0.0")

    // === 导航组件 (底部菜单跳转必用) ===
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // === 图片加载 (Glide) ===
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // === 网络请求 (AI 接口必用) ===
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // === 测试依赖 (保持默认) ===
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // === 【新增】Google 定位服务 ===
    implementation("com.google.android.gms:play-services-location:21.0.1")
}