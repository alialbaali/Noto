buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(com.noto.buildsrc.Libraries.GRADLE)
        classpath(com.noto.buildsrc.Libraries.KOTLIN_GRADLE)
        classpath(com.noto.buildsrc.Libraries.NAVIGATION_SAFE_ARGS)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
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