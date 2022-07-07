plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.12.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh"
version = "1.4.2"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "arknights-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
        artifact(tasks.getByName("buildPluginLegacy"))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-okhttp:1.6.8")
    compileOnly("net.mamoe:mirai-core:2.12.0")
    compileOnly("net.mamoe:mirai-core-utils:2.12.0")

    testImplementation(kotlin("test", "1.6.21"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}