plugins {
//    id("com.github.ben-manes.versions") version "0.28.0"
}
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

//    apply(plugin = "com.github.ben-manes.versions")

    repositories {
        google()
        jcenter()
    }

//    tasks {
//
//        dependencyUpdates {
//
//            gradleReleaseChannel = "current"
//            checkForGradleUpdate = true
//
//            fun isNonStable(version: String): Boolean {
//                val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
//                val regex = "^[0-9,.v-]+(-r)?$".toRegex()
//                val isStable = stableKeyword || regex.matches(version)
//                return isStable.not()
//            }
//
//            rejectVersionIf {
//                isNonStable(candidate.version)
//            }
//
//        }
//    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {

        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()

        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf("-Xallow-result-return-type")
        }

    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}