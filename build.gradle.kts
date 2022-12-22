/*
 * Copyright (c) 2022 by MILOSZ GILGA and FILIP HABRYN
 * Silesian University of Technology
 *
 *  File name: build.gradle.kts
 *  Last modified: 20/12/2022, 10:35
 *  Project name: ski-rent-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

plugins {
    id("java")
    id("war")
}

repositories {
    mavenCentral()
}

extra.apply {
    // dependencies versions
    set("jakkartaApiVersion", "5.0.0")
    set("junitVersion", "5.9.0")
    set("lombokVersion", "1.18.24")
    set("mySqlVersion", "8.0.31")
    set("hibernateVersion", "6.1.6.Final")
    set("liquibaseVersion", "4.18.0")
    set("liquibaseSlf4jVersion", "4.1.0")
    set("jstlVersion", "3.0.1")
    set("ejbVersion", "4.0.1")
    set("loggerVersion", "2.0.6")
    set("reflectionsVersion", "0.10.2")

    // webjars
    set("bootstrapVersion", "5.2.3")
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

dependencies {
    implementation("org.webjars:bootstrap:${rootProject.extra.get("bootstrapVersion") as String}")

    implementation("org.slf4j:slf4j-api:${rootProject.extra.get("loggerVersion") as String}")
    implementation("com.mattbertolini:liquibase-slf4j:${rootProject.extra.get("liquibaseSlf4jVersion") as String}")
    implementation("org.projectlombok:lombok:${rootProject.extra.get("lombokVersion") as String}")
    implementation("org.reflections:reflections:${rootProject.extra.get("reflectionsVersion") as String}")

    implementation("mysql:mysql-connector-java:${rootProject.extra.get("mySqlVersion") as String}")
    implementation("org.hibernate:hibernate-core:${rootProject.extra.get("hibernateVersion") as String}")
    implementation("org.liquibase:liquibase-core:${rootProject.extra.get("liquibaseVersion") as String}")

    implementation("jakarta.ejb:jakarta.ejb-api:${rootProject.extra.get("ejbVersion") as String}")
    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:${rootProject.extra.get("jstlVersion") as String}")
    compileOnly("jakarta.servlet:jakarta.servlet-api:${rootProject.extra.get("jakkartaApiVersion") as String}")

    annotationProcessor("org.projectlombok:lombok:${rootProject.extra.get("lombokVersion") as String}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra.get("junitVersion") as String}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${rootProject.extra.get("junitVersion") as String}")
}
