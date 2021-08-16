val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
}

group = "me.shounen"
version = "0.0.2"
application {
    mainClass.set("src.main.kotlin.ApplicationKt")
}

repositories {
    mavenCentral()
}

val exposedVersion: String by project
dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    implementation ("io.ktor:ktor-serialization:$ktor_version")
    implementation("com.squareup.okhttp3:okhttp:3.2.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation ("com.google.code.gson:gson:2.8.7")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.kttdevelopment:mal4j:2.3.0")

}