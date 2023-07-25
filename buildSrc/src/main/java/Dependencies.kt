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
    const val Kotlin = "1.9.0"
    const val Android = "8.0.2"
    const val Epoxy = "5.1.3"
    const val Compose = "1.5.0"
}

object App {
    const val VersionName = "2.3.0-Beta3"
    const val VersionCode = 57
    const val ID = "com.noto"
    const val Namespace = "com.noto.app"
    const val MinSDK = 21
    const val CompileSDK = 33
    const val BuildTools = "33.0.0"
    const val TargetSDK = 33
}

object Plugins {
    const val AndroidApplication = "com.android.application"
    const val KotlinAndroid = "android"
    const val NavigationSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val KotlinSerialization = "plugin.serialization"
    const val KSP = "com.google.devtools.ksp"
}