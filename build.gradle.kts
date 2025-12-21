plugins {
    java
    id("org.springframework.boot") version "3.5.9"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.8.0"
}

group = "maks.molch.dmitr"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

springBoot {
    mainClass.set("maks.molch.dmitr.badminton_service.BadmintonServiceApplication")
}

dependencies {
    val mapstructVersion = "1.6.3"

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.projectlombok:lombok:1.18.34")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.postgresql:postgresql")
    implementation("org.apache.commons:commons-lang3:3.19.0")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val openApiOutputDir = "$projectDir/generated/openapi"

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/api.yaml")
    outputDir.set(openApiOutputDir)
    apiPackage.set("maks.molch.dmitr.badminton_service.generated.api")
    modelPackage.set("maks.molch.dmitr.badminton_service.generated.model")
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to "ApiUtil.java"
        )
    )
    configOptions.set(
        mapOf(
            "library" to "spring-boot",
            "useSpringBoot3" to "true",
            "delegatePattern" to "true",
            "useTags" to "true",
            "dateLibrary" to "java8"
        )
    )
}

sourceSets {
    named("main") {
        java.srcDir("$openApiOutputDir/src/main/java")
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}
