plugins {
    id(Plugins.AndroidLibrary)
    kotlin(Plugins.KotlinAndroid)
    kotlin(Plugins.KotlinKapt)
}

android {
    compileSdkVersion(App.CompileSDK)
    buildToolsVersion(App.BuildTools)
    defaultConfig {
        minSdkVersion(App.MinSDK)
        targetSdkVersion(App.TargetSDK)
        versionCode = App.VersionCode
        versionName = App.VersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(Modules.Data))
    api(AndroidX.Core.ktx)
    api(AndroidX.Room.runtime)
    kapt(AndroidX.Room.compiler)
    api ("com.github.tfcporciuncula:flow-preferences:1.2.0")
}
