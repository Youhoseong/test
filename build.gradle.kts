import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("idea")
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.21"
    kotlin("kapt") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    kotlin("plugin.noarg") version "1.8.21"
    kotlin("plugin.jpa") version "1.8.21"
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "idea")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "kotlin-noarg")
    apply(plugin = "kotlin-jpa")
    apply(plugin = "kotlin-allopen")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("com.google.code.gson:gson:2.8.9")
        implementation(kotlin("reflect"))

        // test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
        testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
        testImplementation("io.kotest:kotest-property-jvm:5.5.5")
        testImplementation("io.kotest:kotest-framework-datatest:5.5.5")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    }
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }
