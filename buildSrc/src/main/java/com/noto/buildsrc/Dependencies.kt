package com.noto.buildsrc

object Libraries {

    object Main {

        const val LIFE_CYCLE = "androidx.lifecycle:lifecycle-extensions:${Versions.LIFE_CYCLE}"

        const val WORK_MANAGER = "androidx.work:work-runtime-ktx:${Versions.WORK_MANAGER}"

        const val DATA_BINDING_COMPILER = "com.android.databinding:compiler:${Versions.DATA_BINDING_COMPILER}"

        const val LIVE_DATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIVE_DATA}"

        const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEW_MODEL}"

        const val VIEW_MODEL_STATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.VIEW_MODEL_STATE}"

        const val NAVIGATION_FRAGMENT = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_FRAGMENT}"

        const val NAVIGATION = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"

        const val KOTLIN = "stdlib:${Versions.KOTLIN}"

        const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"

        const val CORE = "androidx.core:core-ktx:${Versions.CORE}"

        const val SUPPORT = "androidx.legacy:legacy-support-v4:${Versions.SUPPORT}"

        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"

        const val MATERIAL_DESIGN = "com.google.android.material:material:${Versions.MATERIAL_DESIGN}"

        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"

        const val COORDINATOR_LAYOUT = "androidx.coordinatorlayout:coordinatorlayout:${Versions.COORDINATOR_LAYOUT}"

        const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"

        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"

        const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES_ANDROID}"

        const val KOIN = "org.koin:koin-android-viewmodel:${Versions.KOIN}"

        const val JAVA_TIME = "com.android.tools:desugar_jdk_libs:${Versions.JAVA_TIME}"

    }

    object Gradle {

        const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"

        const val ANDROID = "com.android.tools.build:gradle:${Versions.GRADLE_ANDROID}"

        const val NAVIGATION = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.GRADLE_NAVIGATION}"

    }

    object Local {

        const val ROOM = "androidx.room:room-ktx:${Versions.ROOM}"

        const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"

        const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"

    }

    object Testing {

        // Mocking Framework
        const val MOCKK = "io.mockk:mockk:${Versions.Testing.MOCKK}"

        // Assertion Framework
        const val KOTEST_JUNIT = "io.kotest:kotest-runner-junit5-jvm:${Versions.Testing.KOTEST}"

        const val KOTEST_ASSERTION = "io.kotest:kotest-assertions-core-jvm:${Versions.Testing.KOTEST}"

        const val KOTEST_PROPERTY = "io.kotest:kotest-property-jvm:${Versions.Testing.KOTEST}"

        const val KOTEST_RUNNER = "io.kotest:kotest-runner-console-jvm:${Versions.Testing.KOTEST}"

        const val KOTEST_KOIN = "io.kotest:kotest-extensions-koin:${Versions.Testing.KOTEST}"

        const val JUNIT = "junit:junit:${Versions.Testing.JUNIT}"

        const val ANDROID_CORE = "androidx.test:core:${Versions.Testing.ANDROID_CORE}"

        const val ANDROID_CORE_KTX = "androidx.test:core-ktx:${Versions.Testing.ANDROID_CORE}"

        const val ANDROID_JUNIT = "androidx.test.ext:junit-ktx:${Versions.Testing.TEST_JUNIT}"

        const val ANDROID_RULES = "androidx.test:rules:${Versions.Testing.ANDROID_RULES}"

        const val ANDROID_ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.Testing.ESPRESSO}"

        const val ROOM = "androidx.room:room-testing:${Versions.ROOM}"

        const val KOIN_TEST = "org.koin:koin-test:${Versions.KOIN}"

        const val ARCHITECTURE_COMPONENTS = "androidx.arch.core:core-testing:${Versions.Testing.ARCHITECTURE_COMPONENTS}"

        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"
    }

}

private object Versions {

    // Main
    const val KOTLIN = "1.3.72"

    const val JAVA_TIME = "1.0.4"

    const val NAVIGATION_FRAGMENT = "2.2.1"

    const val COORDINATOR_LAYOUT = "1.1.0"

    const val CONSTRAINT_LAYOUT = "2.0.0-beta8"

    const val VIEW_MODEL_STATE = "2.2.0"

    const val MATERIAL_DESIGN = "1.2.0-alpha06"

    const val RECYCLER_VIEW = "1.1.0"

    const val NAVIGATION = "2.2.1"

    const val LIFE_CYCLE = "2.2.0"

    const val VIEW_MODEL = "2.2.0"

    const val LIVE_DATA = "2.2.0"

    const val TIMBER = "4.7.1"

    const val CORE = "1.2.0"

    const val DATA_BINDING_COMPILER = "3.1.4"

    const val WORK_MANAGER = "2.3.4"

    const val JODA_TIME = "2.10.6"

    const val APP_COMPAT = "1.1.0"

    const val SUPPORT = "1.0.0"

    const val COIL = "0.11.0"

    // Local
    const val ROOM = "2.2.5"

    // Coroutines
    const val COROUTINES = "1.3.8"
    const val COROUTINES_ANDROID = "1.3.8"

    // Gradle
    const val GRADLE_ANDROID = "4.0.1"
    const val GRADLE_NAVIGATION = "2.2.2"

    // DI
    const val KOIN = "2.1.5"

    const val DEPENDENCIES_VERSIONS = "0.28.0"

    object Testing {

        const val MOCKK = "1.10.0"

        const val KOTEST = "4.0.0"

        const val TEST_JUNIT = "1.1.1"

        const val ESPRESSO = "3.2.0"

        const val JUNIT = "4.12"

        const val ARCHITECTURE_COMPONENTS = "2.1.0"

        const val ANDROID_RULES = "1.2.0"

        const val ANDROID_CORE = "1.2.0"
    }
}

object Modules {
    const val BUILD_SRC = ":buildsrc"
    const val DATA = ":data"
    const val DI = ":di"
    const val RESOURCES = ":resources"
    const val LOCAL = ":local"
    const val REMOTE = ":remote"
    const val DOMAIN = ":domain"
    const val APP = ":app"
}

object App {
    const val APP_ID = "com.noto"
    const val APP_VERSION_NAME = "0.4.0"
    const val APP_NAME = "Noto"
    const val APP_VERSION_CODE = 8
    const val MIN_SDK = 21
    const val COMPILE_SDK = 30
    const val BUILD_TOOLS = "30.0.0"
    const val TARGET_SDK = COMPILE_SDK
}

object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val KOTLIN_ANDROID = "android"
    const val KOTLIN_ANDROID_EXTENSIONS = "kotlin-android-extensions"
    const val KOTLIN_KAPT = "kapt"
    const val NAVIGATION_SAFE_ARGS = "androidx.navigation.safeargs.kotlin"
}
