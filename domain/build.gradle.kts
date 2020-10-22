plugins {
    id("java-library")
    kotlin("jvm")
}

dependencies {
    api(Kotlin.stdlib.jdk8)
    api(JakeWharton.timber)
    api(AndroidX.Room.ktx)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}