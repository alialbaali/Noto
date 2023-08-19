buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libraries.Gradle.Kotlin)
        classpath(Libraries.Gradle.Android)
        classpath(AndroidX.Navigation.safeArgsGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("delete", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}