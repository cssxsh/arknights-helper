plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version  Versions.mirai
    id("net.mamoe.maven-central-publish") version "0.6.1"
}

group = "xyz.cssxsh"
version = "1.3.0"

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
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
//            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
//            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
//            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.util.ConsoleExperimentalApi")
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.utils.MiraiExperimentalApi")
        }
        test {
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    // implementation(ktor("client-serialization", Versions.ktor))
    compileOnly(mirai("core", Versions.mirai))

    testImplementation(junit("api", Versions.junit))
    testRuntimeOnly(junit("engine", Versions.junit))
}

tasks {
    test {
        useJUnitPlatform()
    }
}