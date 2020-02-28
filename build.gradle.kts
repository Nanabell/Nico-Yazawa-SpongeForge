plugins {
    java
    kotlin("jvm") version "1.3.61"
    id("org.spongepowered.plugin") version "0.9.0"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "com.nanabell.nico"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://maven.pkg.github.com/nanabell/nico-yazawa-spongeforge") {
        credentials {
            username = project.findProperty("github.username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github.package.personal.access.token") as String? ?: System.getenv("GITHUB_PACKAGE_TOKEN")
        }
    }
}

dependencies {
    shadow(kotlin("stdlib-jdk8"))
    shadow(kotlin("reflect"))

    shadow(files("$projectDir/libs/quickstart-moduleloader-0.11.0.jar"))
    shadow("dev.morphia.morphia:core:1.5.8")
    shadow("net.dv8tion:JDA:4.ALPHA.0_76") {
        exclude("opus-java")
    }

    compileOnly("org.jetbrains:annotations:16.0.2")
    compileOnly("org.spongepowered:spongeapi:7.1.0")
    annotationProcessor("org.spongepowered:spongeapi:7.1.0")
}

configurations {
    compile {
        extendsFrom(shadow.get())
    }
}

tasks {
    shadowJar {
        configurations.add(project.configurations.shadow.get())

        relocate("org.apache", "nanabell.org.apache") {
            exclude("org/apache/logging/**")
        }

        exclude("META-INF/**")
        classifier = ""
    }


    if (hasProperty("server")) {
        val serverDir = property("server") as String

        val copy = register<Copy>("copy-result") {
            dependsOn(shadowJar)

            from("$buildDir/libs")
            into("$serverDir/mods")
        }

        build {
            dependsOn(copy)
        }
    }

    build {
        dependsOn(shadowJar)
        if (hasProperty("server"))
            dependsOn("copy-result")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

sponge {
    plugin {
        //id = "nicos-yazawa"
        meta {
            version = version
        }
    }
}
