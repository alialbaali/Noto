import java.util.*

plugins {
    id(Plugins.AndroidApplication)
    kotlin(Plugins.KotlinAndroid)
    kotlin(Plugins.KotlinKapt)
    id(Plugins.NavigationSafeArgs)
    kotlin("plugin.serialization") version "1.5.31"
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
    bundle {
        language {
            enableSplit = false
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
    kapt {
        correctErrorTypes = true
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
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.Work.runtimeKtx)
    implementation(Google.Android.material)
    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)
    implementation(KotlinX.Serialization.json)
    implementation(AndroidX.Biometric.ktx)
    implementation(AndroidX.DataStore.preferences)
    implementation(Koin.android)
    implementation(Libraries.Main.Epoxy)
    kapt(Libraries.Main.EpoxyProcessor)
    implementation("androidx.room:room-runtime:2.4.0")
    implementation("androidx.room:room-ktx:2.4.0")
    kapt("androidx.room:room-compiler:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("com.google.android:flexbox:2.0.1")
    implementation("com.eightbitlab:blurview:1.6.6")
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")

    testImplementation(Testing.Kotest.Runner.junit5)
    testImplementation(Testing.Kotest.Assertions.core)

    testImplementation(Koin.test)
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

    androidTestImplementation(Koin.test)
    androidTestImplementation(Testing.junit4)

    androidTestImplementation(AndroidX.Test.coreKtx)
    androidTestImplementation(AndroidX.Test.runner)
    androidTestImplementation(AndroidX.Test.rules)
    androidTestImplementation(AndroidX.fragment.testing)
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
