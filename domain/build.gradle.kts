import com.noto.buildsrc.Libraries

plugins {
    id("java-library")
    kotlin("jvm")
}

dependencies {
    api(kotlin(Libraries.KOTLIN))
    testImplementation(Libraries.JUNIT)
    api(Libraries.ROOM)
    api(Libraries.JODA_TIME)
    api(Libraries.MOSHI)
    api(Libraries.TIMBER)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}