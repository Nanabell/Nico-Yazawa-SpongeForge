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
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.dv8tion:JDA:4.ALPHA.0_76") {
        exclude("opus-java")
    }

    shadow("com.j256.ormlite:ormlite-core:4.48") { isTransitive = false }
    shadow("com.j256.ormlite:ormlite-jdbc:4.48") { isTransitive = false }

    compileOnly("org.jetbrains:annotations:16.0.2")
    compileOnly("org.spongepowered:spongeapi:7.1.0")
    annotationProcessor("org.spongepowered:spongeapi:7.1.0")
}

configure<JavaPluginConvention>() {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

shadow {

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
}

sponge {
    plugin {
        //id = "nicos-yazawa"
        meta {
            version = version
        }
    }
}