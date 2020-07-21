import java.net.URI
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(com.noto.buildsrc.Libraries.Gradle.KOTLIN)
        classpath(com.noto.buildsrc.Libraries.Gradle.ANDROID)
        classpath(com.noto.buildsrc.Libraries.Gradle.NAVIGATION)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url= URI("https://jitpack.io") }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type")
        }
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}