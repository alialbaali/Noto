object Libraries {
    object Main {
        const val JavaTime = "com.android.tools:desugar_jdk_libs:${Versions.JavaTime}"
        const val Epoxy = "com.airbnb.android:epoxy:${Versions.Epoxy}"
        const val EpoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.Epoxy}"
    }

    object Gradle {
        const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
        const val Android = "com.android.tools.build:gradle:${Versions.Android}"
    }
}

object Versions {
    const val JavaTime = "1.1.5"
    const val Kotlin = "1.6.10"
    const val Android = "7.0.4"
    const val Epoxy = "4.6.3"
}

object App {
    const val VersionName = "1.7.2"
    const val VersionCode = 32
    const val ID = "com.noto"
    const val MinSDK = 21
    const val CompileSDK = 31
    const val BuildTools = "31.0.0"
    const val TargetSDK = CompileSDK
}

object Plugins {
    const val AndroidApplication = "com.android.application"
    const val KotlinAndroid = "android"
    const val KotlinKapt = "kapt"
    const val NavigationSafeArgs = "androidx.navigation.safeargs.kotlin"
}
