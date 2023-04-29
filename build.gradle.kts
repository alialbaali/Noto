import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
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
        jcenter()
    }

    // Temporary workaround for https://github.com/google/ksp/issues/1288
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}

tasks.register("delete", Delete::class.java) {
    delete(rootProject.buildDir)
}