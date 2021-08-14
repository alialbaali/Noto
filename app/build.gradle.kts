import java.util.*

plugins {
    id(Plugins.AndroidApplication)
    kotlin(Plugins.KotlinAndroid)
    kotlin(Plugins.KotlinKapt)
    id(Plugins.NavigationSafeArgs)
}

android {
    compileSdk = App.CompileSDK
    buildToolsVersion = App.BuildTools
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
            signingConfig = signingConfigs.getByName("release")
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

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
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
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")

    testImplementation(Testing.Kotest.Runner.junit5)
    testImplementation(Testing.Kotest.Assertions.core)

    testImplementation(Libraries.Testing.KoinTest)
    testImplementation(Testing.junit4)
    testImplementation(Testing.robolectric)

    testImplementation(AndroidX.Test.coreKtx)
    testImplementation(AndroidX.Test.runner)
    testImplementation(AndroidX.Test.rules)
    testImplementation(AndroidX.Test.Ext.junitKtx)
    testImplementation(AndroidX.ArchCore.testing)
    testImplementation(AndroidX.Test.Espresso.core)
    testImplementation(KotlinX.coroutines.test)

    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(Kotlin.Test.junit)
    androidTestImplementation(Kotlin.Test.common)

    androidTestImplementation(Libraries.Testing.KoinTest)
    androidTestImplementation(Testing.junit4)
//    androidTestImplementation(Testing.robolectric)

    androidTestImplementation(AndroidX.Test.coreKtx)
    androidTestImplementation(AndroidX.Test.runner)
    androidTestImplementation(AndroidX.Test.rules)
    androidTestImplementation(AndroidX.fragmentTesting)
    androidTestImplementation(AndroidX.Test.Ext.junitKtx)
    androidTestImplementation(AndroidX.ArchCore.testing)
    androidTestImplementation(AndroidX.Test.Espresso.core)
    androidTestImplementation(AndroidX.Test.Espresso.contrib)
    androidTestImplementation("androidx.navigation:navigation-testing:2.3.5")

    coreLibraryDesugaring(Libraries.Main.JavaTime)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
