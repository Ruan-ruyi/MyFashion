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

    // === 关键：导航组件 (Navigation) ===
    // 之前漏掉了这两个，必须加上，否则 MainActivity 会报错
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // === 图片加载 (Glide) ===
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // === 网络请求 (用于 AI 接口) ===
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // === JSON 解析 (可选，防止你以后用到) ===
    implementation("com.google.code.gson:gson:2.10.1")

    // === 单元测试 (保持默认) ===
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}