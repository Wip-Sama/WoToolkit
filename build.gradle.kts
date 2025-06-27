plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.2.0-RC2"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.1"
    id("com.github.johnrengelman.shadow") version "8.1.1" //gradle shadowJar per il jar con tutte le dipendenze
    application
}

group = "org.wip"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

//    implementation(kotlin("stdlib"))
//    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.10.2")
    implementation("org.openjfx:javafx-controls:23.0.1")
    implementation("org.openjfx:javafx-fxml:23.0.1")

    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    implementation(project(":nfx-core"))
    implementation(fileTree("libs") { include("*.jar") })
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "23.0.1"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "javafx.web",
        "javafx.base"
    )
}

application {
    mainModule.set("org.wip.womtoolkit")
    mainClass.set("org.wip.womtoolkit.MainKt")
}

kotlin {
    jvmToolchain(23)
}

java {
    modularity.inferModulePath.set(true)
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

tasks.jar {
    from(sourceSets.main.get().output)
    manifest {
        attributes(
            "Main-Class" to "org.wip.womtoolkit.MainKt",
            "Automatic-Module-Name" to "org.wip.womtoolkit"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/kotlin", "src/main/java"))
        }
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}