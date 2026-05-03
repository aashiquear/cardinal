pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "cardinal"

include(":app")
include(":car")
include(":core:core-common")
include(":core:core-domain")
include(":core:core-data")
include(":feature:feature-map")
include(":feature:feature-routing")
include(":feature:feature-navigation")
include(":feature:feature-poi")
include(":feature:feature-weather")
include(":feature:feature-traffic")
include(":data:data-local")
include(":data:data-remote")
