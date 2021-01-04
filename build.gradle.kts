import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.tools.ant.filters.FixCrLfFilter
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import java.io.FileReader

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.gmazzo.buildconfig") version "2.0.2"
    idea
}

group = "de.nycode"
version = "1.0.0"

repositories {
    jcenter()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/nexus/repository/public/")
    maven(url = "https://libraries.minecraft.net/")
}

val minecraft_version: String by project

dependencies {
    // PaperMC Dependency
    compileOnly("com.destroystokyo.paper", "paper-api", "$minecraft_version-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.6.0-SNAPSHOT")
    compileOnly("com.mojang", "brigadier", "1.0.17")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("me.lucko", "commodore", "1.9") {
        exclude("com.mojang", "brigadier")
    }
    implementation("io.papermc", "paperlib", "1.0.6")
    // Add your dependencies here
    // Examples
    // implementation("io.ktor", "ktor-client", "1.4.0") // Would be shaded into the final jar
    // compileOnly("io.ktor", "ktor-client", "1.4.0") // Only used on compile time
}

buildConfig {
    className("BuildConfig")
    packageName("$group.nickplugin")
    val commit = getGitHash()
    val branch = getGitBranch()
    buildConfigField("String", "GIT_COMMIT", "\"$commit\"")
    buildConfigField("String", "GIT_BRANCH", "\"$branch\"")
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

val properties = Properties()
properties.load(FileReader(File("local.properties")))
val pluginDir: String? = properties.getProperty("pluginDir", null)

tasks {
    shadowJar {
        val basePackage = "de.nycode.nickplugin.thirdparty"
        relocate("io.papermc.lib", "$basePackage.paperlib")
        relocate("me.lucko.commodore", "$basePackage.commodore")
        relocate("kotlin", "$basePackage.kotlin")
        relocate("kotlinx", "$basePackage.kotlinx")
        relocate("org.intellij", "$basePackage.intellij")
        relocate("org.jetbrains", "$basePackage.jetbrains")
    }

    if (pluginDir != null) {
        register<Copy>("copyJarToBin") {
            from("build/libs/nick-plugin-1.0.0-all.jar")
            into(pluginDir)
            dependsOn(build)
        }
    }
    processResources {
        filter(FixCrLfFilter::class)
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to project.version))
        filteringCharset = "UTF-8"
    }
    jar {
        // Disabled, because we use the shadowJar task for building our jar
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}