plugins {
    id(Plugins.AndroidLibrary)
    kotlin(Plugins.KotlinAndroid)
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
    implementation(project(Modules.Local))
    implementation(project(Modules.Data))
    implementation(project(Modules.Domain))
    api(Libraries.Main.Koin)
}