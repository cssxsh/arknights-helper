plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version "2.10.0-RC2"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh"
version = "1.3.10"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "arknights-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/central")
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
    configureShadow {
        exclude("module-info.class")
    }
}

dependencies {
    compileOnly("net.mamoe:mirai-core:${mirai.coreVersion}")
    compileOnly("net.mamoe:mirai-core-utils:${mirai.coreVersion}")

    testImplementation(kotlin("test", kotlin.coreLibrariesVersion))
}

tasks {
    test {
        useJUnitPlatform()
    }
}