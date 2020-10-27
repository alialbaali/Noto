import java.net.URI
buildscript {
    repositories {
        google()
        jcenter()
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
        jcenter()
        maven { url= URI("https://jitpack.io") }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type", "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}