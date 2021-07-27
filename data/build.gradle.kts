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
    api(project(Modules.Domain))
    testImplementation(KotlinX.coroutines.test)
    testImplementation(Libraries.Testing.KoinTest)
    testImplementation(Testing.Kotest.Runner.junit4)
    testImplementation(Testing.Kotest.core)
    testImplementation(Testing.junit4)
    testImplementation(Testing.Kotest.property)
    testImplementation(Testing.Kotest.assertions.core)
    testImplementation(Testing.Kotest.Extensions.koin)
}

tasks.withType<Test> {
    useJUnitPlatform()
}