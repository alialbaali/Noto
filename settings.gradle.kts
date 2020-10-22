import de.fayard.refreshVersions.bootstrapRefreshVersions

rootProject.name = "Noto"
include(":di", ":local", ":data", ":domain", ":app")


buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()