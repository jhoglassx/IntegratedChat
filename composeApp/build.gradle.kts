import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Locale
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id("jacoco")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.ExperimentalMultiplatform")
            }
        }

        val commonMain by getting {
            kotlin.srcDir("${project.rootDir}/buildConfig")
            dependencies {
                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(compose.uiTooling)
                implementation(compose.preview)

                // Lifecycle
                implementation(libs.lifecycle.viewmodel)

                // Serialization
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                // Ktor Client
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)

                // Ktor Server
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.host.common)
                implementation(libs.ktor.server.auth)
                implementation(libs.ktor.server.auth.jwt)
                implementation(libs.ktor.server.call.logging)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Koin
                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Navigation
                implementation(libs.navigation.compose)

                //Coil
                implementation(libs.coil.compose.core)
                implementation(libs.coil.compose)
                implementation(libs.coil.mp)
                implementation(libs.coil.network.ktor)

                //Logger
                implementation(libs.kermit)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.kotlin.test.junit)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.kotest.assertions.core)
            }
        }

        val androidMain by getting {
            dependencies {
                // Compose
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.uiTooling)
                implementation(compose.preview)
                implementation(libs.androidx.material3)

                // AndroidX
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.credentials)
                implementation(libs.androidx.credentials.play.services.auth)

                // Networking
                implementation(libs.okhttp)
                implementation(libs.ktor.client.okhttp)

                // Google Services
                implementation(libs.play.services.auth)
                implementation(libs.googleid)

                // Koin
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.kotlin.test.junit)
                implementation(libs.androidx.core.ktx)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.espresso.core)
            }
        }

        val desktopMain by getting {
            dependencies {
                // Compose
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)

                // Coroutines
                implementation(libs.kotlinx.coroutines.swing)

                // Google OAuth
                implementation(libs.google.oauth.client)
                implementation(libs.google.api.services.oauth2)

                // Ktor
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.server.html.builder)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.kotlin.test.junit)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk)
                implementation(libs.kotest.assertions.core)
            }
        }
    }
}

android {
    namespace = "com.js.integratedchat"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.js.integratedchat"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = false
        }
    }

    packaging{
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/*.SF")
        resources.excludes.add("META-INF/*.DSA")
        resources.excludes.add("META-INF/*.RSA")
        resources.excludes.add("META-INF/io.netty.versions.properties")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

compose.desktop {
    application {
        mainClass = "com.js.integratedchat.ui.main.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.js.integratedchat"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xexpect-actual-classes")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "reporting"
    description = "Generate Jacoco code coverage report for unit tests"

    val reportXmlPath = "${project.rootDir}/composeApp/build/reports/jacoco/test/jacocoTestReport.xml"
    val reportHtmlPath = "${project.rootDir}/composeApp/build/reports/jacoco/jacocoHtml"

    reports {
        xml.required = true
        xml.outputLocation = file(reportXmlPath)
        html.required = true
        html.outputLocation = file(reportHtmlPath)
        csv.required = false
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/*MyApplication*.*"
    )
    val excludedDirs = listOf(
        "**/provider/**",
        "**/service/**",
        "**/di/**",
        "**/generated/**"
    )

    val javaDebugTree = fileTree("${project.rootDir}/composeApp/build/intermediates/javac/debug") {
        exclude(fileFilter)
        exclude(excludedDirs)
    }
    val kotlinDebugTree = fileTree("${project.rootDir}/composeApp/build/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
        exclude(excludedDirs)
    }

    val mainSrc = "${project.rootDir}/composeApp/src/main/"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(javaDebugTree, kotlinDebugTree))

    executionData.setFrom(fileTree(mapOf(
        "dir" to "${project.rootDir}/composeApp/build/jacoco",
        "includes" to listOf("**/*.exec", "**/*.ec")
    )))

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(fileFilter)
            exclude(excludedDirs)
        }
    }))
}

tasks.register("generateBuildConfig") {
    doLast {
        val flavor = project.findProperty("flavor")?.toString() ?: "debug"
        val isLocalOrDebug = flavor == "debug"
        val propertiesFile = File("${project.rootDir}/keys-$flavor.properties")

        val outputDir = file("${project.rootDir}/buildConfig")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val buildConfigFile = File(outputDir, "BuildConfig.kt")

        val envKeys = listOf(
            "TWITCH_CLIENT_ID",
            "TWITCH_CLIENT_SECRET",
            "GOOGLE_DESKTOP_CLIENT_ID",
            "GOOGLE_DESKTOP_CLIENT_SECRET",
            "GOOGLE_WEB_CLIENT_ID",
            "GOOGLE_WEB_CLIENT_SECRET"
        )

        if (isLocalOrDebug && propertiesFile.exists()) {
            val properties = Properties().apply {
                load(propertiesFile.inputStream())
            }

            buildConfigFile.writeText(
                buildConfigFileContentFromLocal(properties)
            )
        } else {
            buildConfigFile.writeText(
                buildConfigFileContentFromEnv(envKeys)
            )
        }
    }
}

tasks.named("assemble") {
    dependsOn(tasks.named("generateBuildConfig"))
}

tasks.named("build") {
    dependsOn(tasks.named("generateBuildConfig"))
}

fun buildConfigFileContentFromLocal(properties: Properties): String {
    val sb = StringBuilder()
    sb.append("package com.js.integratedchat\n\n")
    sb.append("object BuildConfig {\n")
    properties.forEach { (key, value) ->
        sb.append("    const val ${key.toString().uppercase(Locale.getDefault())} = $value\n")
    }
    sb.append("}\n")
    return sb.toString()
}


fun buildConfigFileContentFromEnv(keys: List<String>): String {
    val sb = StringBuilder()
    sb.append("package com.js.integratedchat\n\n")
    sb.append("object BuildConfig {\n")

    keys.forEach { key ->
        val value = System.getenv(key) ?: "default_value"
        sb.append("    const val ${key.uppercase(Locale.getDefault())} = \"$value\"\n")
    }

    sb.append("}\n")
    return sb.toString()
}