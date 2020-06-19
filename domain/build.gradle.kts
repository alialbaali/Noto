import com.noto.buildsrc.Libraries

plugins {
    id("java-library")
    kotlin("jvm")
}

dependencies {
    api(kotlin(Libraries.Main.KOTLIN))
    api(Libraries.Main.JODA_TIME)
    api(Libraries.Main.TIMBER)
    api(Libraries.Local.ROOM)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}