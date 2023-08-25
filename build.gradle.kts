import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.compose") version "1.2.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

repositories {
    mavenCentral()
}

val javaBytecodeVersion = 11
// Configure the build options for Kotlin
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
    .configureEach {
        kotlinOptions {
            jvmTarget = "$javaBytecodeVersion"
            allWarningsAsErrors = true
            // Some configuration that is needed for compose to run
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }

// Configure the build options for Java
tasks.compileJava {
    options.release.set(javaBytecodeVersion)
}
tasks.compileTestJava {
    options.release.set(javaBytecodeVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Kotlin coroutines library
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")

    // Compose dependency
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.desktop.components.splitPane)
    implementation(compose.materialIconsExtended)

    // Decompose
    val decomposeVersion = "0.6.0"
    implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

    // MVI library to manage the application state
    val mviKotlinVersion = "3.0.0-beta01"
    implementation("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
    implementation("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlinVersion")
    implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")
    implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$mviKotlinVersion")
    implementation("com.arkivanov.mvikotlin:rx:$mviKotlinVersion")

    val reaktiveVersion = "1.2.1"
    implementation("com.badoo.reaktive:reaktive:$reaktiveVersion")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.8.5")

    // CSV
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.6.0")
}

compose.desktop {
    application {
        mainClass = "ru.uniyar.ac.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "simplex_method_tasks_solver"
            packageVersion = "1.0.0"
        }
    }
}

tasks.jar {
    manifest.attributes["Main-Class"] = "ru.uniyar.ac.MainKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")
    from(sourceSets.main.get().output)

    this.setProperty("duplicatesStrategy", DuplicatesStrategy.WARN)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    manifest.attributes["Main-Class"] = "MainKt"
}
