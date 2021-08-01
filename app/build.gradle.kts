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
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(JakeWharton.timber)
    implementation(AndroidX.Navigation.uiKtx)
    implementation(AndroidX.Navigation.fragmentKtx)
    implementation(AndroidX.Core.ktx)
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
    implementation("androidx.room:room-runtime:2.3.0")
    implementation("androidx.room:room-ktx:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")
    implementation(Libraries.Main.DataStore)
    implementation(Libraries.Main.Koin)

    testImplementation(KotlinX.coroutines.test)
//    testImplementation(Libraries.Testing.KoinTest)
    testImplementation(Testing.Kotest.Runner.junit4)
//    testImplementation(Testing.Kotest.core)
    testImplementation(Testing.junit4)
    testImplementation(Testing.Kotest.property)
    testImplementation(Testing.Kotest.assertions.core)
//    testImplementation(Testing.Kotest.Extensions.koin)

    coreLibraryDesugaring(Libraries.Main.JavaTime)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
