import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
}

group = "com.github.blanexie.vxph"
version = "0.0.1-SNAPSHOT"

val saTokenVersion = "1.37.0"


java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("xerces:xercesImpl:2.12.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("org.xerial:sqlite-jdbc:3.43.0.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.3.1.Final")
    implementation("com.vladmihalcea:hibernate-types-52:2.4.3")
   implementation("cn.dev33:sa-token-spring-boot3-starter:1.37.0")

    implementation("cn.hutool:hutool-all:5.8.22")
    implementation("com.aliyun:alidns20150109:3.0.8") {
        exclude("pull-parser", "pull-parser")
    }
    implementation("com.dampcake:bencode:1.4.1")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
