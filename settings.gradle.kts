
rootProject.name = "Noto"
include(":di", ":local", ":data", ":domain", ":app")

plugins {
    id("de.fayard.refreshVersions") version "0.10.1"
}
