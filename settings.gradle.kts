pluginManagement {
    val artifactory_hosting_username: String by settings
    val artifactory_hosting_password: String by settings

    repositories {
        maven {
            url = uri("https://artifactory.e-spirit.hosting/artifactory/repo/")
            credentials {
                username = artifactory_hosting_username
                password = artifactory_hosting_password
            }
        }
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    plugins {
        // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
        id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
    }
}

rootProject.name = "next-generation-sfcc-publication"
