buildscript {
    repositories {
        val artifactory_hosting_username: String by project
        val artifactory_hosting_password: String by project
        maven {
            url = uri("https://artifactory.e-spirit.hosting/artifactory/repo//")
            credentials {
                username = artifactory_hosting_username
                password = artifactory_hosting_password
            }
        }
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("de.espirit.firstspirit-module") version "0.15.9"
    id("de.espirit.firstspirit") version "1.0.7"
}

group = "de.espirit.sfcc"
version = "0.0.1-SNAPSHOT"

val artifactory_hosting_username: String by project
val artifactory_hosting_password: String by project
val kotlinVersion: String by project
val fsVersion: String by project

repositories {
    maven {
        url = uri("https://artifactory.e-spirit.hosting/artifactory/repo//")
        credentials {
            username = artifactory_hosting_username
            password = artifactory_hosting_password
        }
    }
}

configurations
        .filter { !it.name.startsWith("test") }
        .forEach {
            it.resolutionStrategy {
                failOnVersionConflict()
                force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
                force("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")
                force("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            }
        }

dependencies {
    compileOnly("de.espirit.firstspirit:fs-isolated-runtime:$fsVersion")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    fsModuleCompile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    fsModuleCompile("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    fsModuleCompile("com.github.lookfirst:sardine:5.10")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.2")
    testImplementation("de.espirit.firstspirit:fs-isolated-runtime:$fsVersion")
    testImplementation("io.mockk:mockk:1.10.0")
}

firstSpiritModule {
    displayName = "Next Generation SFCC Publication"
    resourceMode = de.espirit.firstspirit.server.module.ModuleInfo.Mode.ISOLATED
    // isolationDetectorUrl = ""
    complianceLevel = de.espirit.mavenplugins.fsmchecker.ComplianceLevel.HIGHEST
    firstSpiritVersion = fsVersion
    vendor = "e-Spirit AG"
}

firstSpirit.setVersion(fsVersion)

tasks.fsInstallModule {
    fsm = tasks.assembleFSM.get().outputs.files.first().absolutePath
    dependsOn(tasks.assembleFSM)
}

tasks.compileJava {
    options.release.set(11)
}
