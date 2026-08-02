import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val generateBuildInfo = tasks.register("generateBuildInfo") {
    val outDir = layout.buildDirectory.dir("generated/buildInfo/kotlin")
    outputs.dir(outDir)
    outputs.upToDateWhen { false }

    val gitSha = providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        isIgnoreExitValue = true
    }.standardOutput.asText

    doLast {
        val dir = outDir.get().asFile
        dir.mkdirs()
        val agora = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"))
        val sha = gitSha.get().trim().ifEmpty { "?" }
        dir.resolve("BuildInfo.kt").writeText(
            """
            package com.luisamsampaio.jiggie

            object BuildInfo {
                const val TIME = "$agora"
                const val SHA = "$sha"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    
    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuildInfo)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}