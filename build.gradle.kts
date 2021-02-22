
plugins {
    id("maven-publish")
    kotlin("multiplatform") version "1.4.20"
    id("org.jetbrains.dokka") version "1.4.20"
}

repositories {
    mavenLocal()
    google()
    jcenter()
    maven(url = "http://mvnrepository.com/artifact/")
    maven(url = "https://dl.bintray.com/spekframework/spek-dev")
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

kotlin {
    jvm()

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
            }
        }

        named("commonTest") {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.4.10")
                implementation("org.spekframework.spek2:spek-dsl-metadata:2.0.9")
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("io.vertx:vertx-core:4.0.0")
                implementation("joda-time:joda-time:2.9.7")
                implementation("io.vertx:vertx-jdbc-client:4.0.0")
                implementation("com.github.jasync-sql:jasync-postgresql:1.1.5")
                implementation("com.github.jasync-sql:jasync-mysql:1.1.5")
                implementation("io.zeko:zeko-data-mapper:1.6.2")
                implementation("com.fasterxml.jackson.core:jackson-databind:2.9.10.3")
                implementation("com.zaxxer:HikariCP:3.4.5")
                implementation("io.vertx:vertx-lang-kotlin:4.0.0")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
            }
        }

        named("jvmTest") {
            dependencies {
                implementation("org.spekframework.spek2:spek-dsl-jvm:2.0.9")
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.9")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.4"
        }
    }
}

group = "io.zeko"
version = "1.2.3-SNAPSHOT"
description = "io.zeko:zeko-sql-builder"
