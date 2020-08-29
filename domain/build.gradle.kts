import com.noto.buildsrc.Libraries

plugins {
    id("java-library")
    kotlin("jvm")
}

dependencies {

    api(kotlin(Libraries.Main.KOTLIN))
//    api(Libraries.Main.JODA_TIME)
    api(Libraries.Main.TIMBER)
    api(Libraries.Local.ROOM)
    api(Libraries.Main.COROUTINES)

//    testApi(Libraries.Testing.ANDROID_CORE)
//    testApi(Libraries.Testing.COROUTINES)
//    testApi(Libraries.Testing.JUNIT)
//    testApi(Libraries.Testing.KOTEST_JUNIT)
//    testApi(Libraries.Testing.KOTEST_ASSERTION)
//    testApi(Libraries.Testing.KOTEST_PROPERTY)
//    testApi(Libraries.Testing.MOCKK)
//    testApi(Libraries.Testing.KOIN_TEST)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}