import com.noto.buildsrc.App
import com.noto.buildsrc.Libraries
import com.noto.buildsrc.Modules

plugins {
    val plugins = com.noto.buildsrc.Plugins
    id(plugins.ANDROID_APPLICATION)
    kotlin(plugins.KOTLIN_ANDROID)
    id(plugins.KOTLIN_ANDROID_EXTENSIONS)
    kotlin(plugins.KOTLIN_KAPT)
    id(plugins.NAVIGATION_SAFE_ARGS)
}

android {
    compileSdkVersion(App.COMPILE_SDK)
    buildToolsVersion(App.BUILD_TOOLS)
    defaultConfig {
        applicationId = App.APP_ID
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

    buildFeatures {
        dataBinding = true
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Modules.DOMAIN))

    // DI
    implementation(project(Modules.DI))

    // Main
    implementation(Libraries.Main.NAVIGATION)
    implementation(Libraries.Main.NAVIGATION_FRAGMENT)
    implementation(Libraries.Main.CONSTRAINT_LAYOUT)
    implementation(Libraries.Main.APP_COMPAT)
    implementation(Libraries.Main.COORDINATOR_LAYOUT)
    implementation(Libraries.Main.LIFE_CYCLE)
    implementation(Libraries.Main.VIEW_MODEL)
    implementation(Libraries.Main.VIEW_MODEL_STATE)
    implementation(Libraries.Main.RECYCLER_VIEW)
    implementation(Libraries.Main.LIVE_DATA)
    implementation(Libraries.Main.SUPPORT)
    implementation(Libraries.Main.MATERIAL_DESIGN)
    implementation(Libraries.Main.WORK_MANAGER)
    implementation(Libraries.Main.CORE)
    implementation(Libraries.Main.COIL)
    kapt(Libraries.Main.DATA_BINDING_COMPILER)


    // Coroutines
    implementation(Libraries.Main.COROUTINES_ANDROID)

}
