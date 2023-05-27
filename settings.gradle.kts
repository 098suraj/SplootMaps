pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        mavenCentral()
        jcenter()
    }
}
rootProject.name = "SplootMaps"
include (":app")
