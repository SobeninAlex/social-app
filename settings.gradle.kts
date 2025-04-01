plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "social-app"
include("data")
include("app")
include("domain")
include("utils")
