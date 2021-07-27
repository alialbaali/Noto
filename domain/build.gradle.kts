plugins {
    id("java-library")
    kotlin("jvm")
}

dependencies {
    api(JakeWharton.timber)
    api(AndroidX.Room.common)
    api(KotlinX.Coroutines.core)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}