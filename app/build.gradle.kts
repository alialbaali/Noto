plugins {
    id(Plugins.AndroidApplication)
    kotlin(Plugins.KotlinAndroid)
    kotlin(Plugins.KotlinKapt)
    id(Plugins.NavigationSafeArgs)
}

android {
    compileSdk = App.CompileSDK
    buildToolsVersion = App.BuildTools
    defaultConfig {
        applicationId = App.ID
        minSdk = App.MinSDK
        targetSdk = App.TargetSDK
        versionCode = App.VersionCode
        versionName = App.VersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {

        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    packagingOptions {
    }
    configurations {
        implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations"))
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Modules.Domain))
    implementation(project(Modules.DI))

    implementation(AndroidX.Navigation.uiKtx)
    implementation(AndroidX.Navigation.fragmentKtx)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.coordinatorLayout)
    implementation(AndroidX.Lifecycle.viewModelKtx)
    implementation(AndroidX.Lifecycle.runtimeKtx)
    implementation(AndroidX.Lifecycle.liveDataKtx)
    implementation(AndroidX.Lifecycle.liveDataCoreKtx)
    implementation(AndroidX.Lifecycle.viewModelSavedState)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.Legacy.supportV4)
    implementation(Google.Android.material)
    implementation(AndroidX.Work.runtimeKtx)
    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)
    coreLibraryDesugaring(Libraries.Main.JavaTime)
}

