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
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/metadata.jvm.kotlin_module")
        exclude("META-INF/metadata.kotlin_module")
        exclude("com.google.guava")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(Modules.DOMAIN))
    implementation(project(Modules.DI))
    implementation(Libraries.NAVIGATION)
    implementation(Libraries.NAVIGATION_FRAGMENT)
    implementation(Libraries.CONSTRAINT_LAYOUT)
    implementation(Libraries.APP_COMPAT)
    implementation(Libraries.COORDINATOR_LAYOUT)
    implementation(Libraries.COROUTINES)
    implementation(Libraries.COROUTINES_ANDROID)
    implementation(Libraries.LIFE_CYCLE)
    implementation(Libraries.VIEW_MODEL)
    implementation(Libraries.VIEW_MODEL_STATE)
    implementation(Libraries.RECYCLER_VIEW)
    implementation(Libraries.LIVE_DATA)
    implementation(Libraries.SUPPORT)
    implementation(Libraries.MATERIAL_DESIGN)
    implementation(Libraries.PROGRESS_BUTTON)
    implementation(Libraries.GLIDE)
    implementation(Libraries.WORK_MANAGER)
    kapt(Libraries.GLIDE_COMPILER)
    kapt(Libraries.DATA_BINDING_COMPILER)
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}
