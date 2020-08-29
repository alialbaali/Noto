import com.noto.buildsrc.App
import com.noto.buildsrc.Libraries
import com.noto.buildsrc.Modules

plugins {
    val plugins = com.noto.buildsrc.Plugins
    id(plugins.ANDROID_LIBRARY)
    kotlin(plugins.KOTLIN_ANDROID)
    id(plugins.KOTLIN_ANDROID_EXTENSIONS)
    kotlin(plugins.KOTLIN_KAPT)
}

android {
    compileSdkVersion(App.COMPILE_SDK)
    buildToolsVersion(App.BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(App.MIN_SDK)
        targetSdkVersion(App.TARGET_SDK)
        versionCode = App.APP_VERSION_CODE
        versionName = App.APP_VERSION_NAME

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
    packagingOptions {
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/LICENSE.md")
    }
}

dependencies {
    implementation(project(Modules.DATA))
    api(Libraries.Main.CORE)
    api(Libraries.Local.ROOM_RUNTIME)
    kapt(Libraries.Local.ROOM_COMPILER)
//    androidTestImplementation(Libraries.Testing.ARCHITECTURE_COMPONENTS)
//    androidTestImplementation(Libraries.Testing.ANDROID_JUNIT)
//    androidTestImplementation(Libraries.Testing.COROUTINES)
//    androidTestImplementation(Libraries.Testing.JUNIT)
//    androidTestImplementation(Libraries.Testing.KOTEST_JUNIT)
//    androidTestImplementation(Libraries.Testing.KOTEST_ASSERTION)
//    androidTestImplementation(Libraries.Testing.KOTEST_PROPERTY)
//    androidTestImplementation(Libraries.Testing.MOCKK)
//    androidTestImplementation(Libraries.Testing.KOIN_TEST)
//    androidTestImplementation(Libraries.Testing.ROOM)
//    androidTestImplementation(Libraries.Testing.ANDROID_RULES)
//    androidTestImplementation(Libraries.Testing.ANDROID_CORE)
//    androidTestImplementation(Libraries.Testing.ANDROID_CORE_KTX)
}
