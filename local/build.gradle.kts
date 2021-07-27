plugins {
    id(Plugins.AndroidLibrary)
    kotlin(Plugins.KotlinAndroid)
    kotlin(Plugins.KotlinKapt)
}

android {
    compileSdk = App.CompileSDK
    buildToolsVersion = App.BuildTools
    defaultConfig {
        minSdk = App.MinSDK
        targetSdk = App.TargetSDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
    implementation(project(Modules.Domain))
    api(AndroidX.Core.ktx)
    api(Libraries.Main.ROOM_RUNTIME)
    kapt(Libraries.Main.ROOM_COMPILER)
    api(Libraries.Main.ROOM_COMMON)
    api(Libraries.Main.ROOM)
    api(Libraries.Main.DataStore)
}
