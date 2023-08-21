import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
}

kotlin {
    jvmToolchain(App.JdkVersion)
}

android {
    compileSdk = App.CompileSDK
    buildToolsVersion = App.BuildTools
    namespace = App.Namespace

    defaultConfig {
        applicationId = App.ID
        minSdk = App.MinSDK
        targetSdk = App.TargetSDK
        versionCode = App.VersionCode
        versionName = App.VersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(project.rootProject.file("local.properties").inputStream())
            }
            storeFile = file(properties["store.file"] as String)
            storePassword = properties["store.password"] as String
            keyAlias = properties["key.alias"] as String
            keyPassword = properties["key.password"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }

        create("release-candidate") {
            versionNameSuffix = "-rc"
            applicationIdSuffix = ".rc"
            isDebuggable = true
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.viewpager2)

    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.google.material)
    implementation(libs.google.flexbox)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.compose.ui.viewbinding)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)

    implementation(libs.timber)
    implementation(libs.koin.android)
    implementation(libs.recyclerview.animators)
    implementation(libs.ticker)

    implementation(libs.epoxy)
    ksp(libs.epoxy.processor)

    coreLibraryDesugaring(libs.android.tools.desugar.jdk.libs)
}

tasks.withType<Test> {
    useJUnitPlatform()
}