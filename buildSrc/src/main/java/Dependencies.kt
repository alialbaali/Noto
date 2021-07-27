object Libraries {
    object Main {
        const val Koin = "org.koin:koin-android-viewmodel:${Versions.Koin}"
        const val JavaTime = "com.android.tools:desugar_jdk_libs:${Versions.JavaTime}"
        const val DataStore = "androidx.datastore:datastore-preferences:1.0.0-alpha02"
        const val ROOM = "androidx.room:room-ktx:${Versions.ROOM}"
        const val ROOM_COMMON = "androidx.room:room-common:${Versions.ROOM}"
        const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
        const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    }
    object Gradle {
        const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
        const val Android = "com.android.tools.build:gradle:${Versions.GradleAndroid}"
        const val Navigation = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Navigation}"
    }
    object Testing {
        const val KoinTest = "org.koin:koin-test:${Versions.Koin}"
    }
}

object Versions {
    const val Kotlin = "1.5.20"
    const val JavaTime = "1.0.9"
    const val Navigation = "2.3.1"
    const val GradleAndroid = "7.0.0-rc01"
    const val Koin = "2.1.6"
    const val ROOM = "2.3.0"
}

object Modules {
    const val Data = ":data"
    const val DI = ":di"
    const val Local = ":local"
    const val Domain = ":domain"
}

object App {
    const val ID = "com.noto"
    const val VersionName = "1.0.0"
    const val Name = "Noto"
    const val VersionCode = 9
    const val MinSDK = 21
    const val CompileSDK = 30
    const val BuildTools = "30.0.2"
    const val TargetSDK = CompileSDK
}

object Plugins {
    const val AndroidApplication = "com.android.application"
    const val AndroidLibrary = "com.android.library"
    const val KotlinAndroid = "android"
    const val KotlinKapt = "kapt"
    const val NavigationSafeArgs = "androidx.navigation.safeargs.kotlin"
}
