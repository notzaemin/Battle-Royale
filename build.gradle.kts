import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:${project.properties["paperVersion"]}-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:kommand-core:${project.properties["kommandVersion"]}")
    compileOnly("io.github.monun:invfx-api:${project.properties["invfxVersion"]}")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    register<Jar>("outputJar") {
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")
        from(sourceSets["main"].output)
        doLast {
            copy {
                from(archiveFile)
                into("./out")
            }
        }
    }
    register<Jar>("paperJar") {
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")
        from(sourceSets["main"].output)
        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".server/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}