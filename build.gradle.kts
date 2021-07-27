import java.net.URI
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libraries.Gradle.Kotlin)
        classpath(Libraries.Gradle.Android)
        classpath(Libraries.Gradle.Navigation)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("delete", Delete::class.java) {
    delete(rootProject.buildDir)
}