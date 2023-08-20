buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.tools.build.gradle)
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
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