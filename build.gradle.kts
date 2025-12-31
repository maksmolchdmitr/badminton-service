plugins {
    java
    id("org.springframework.boot") version "3.5.9"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.8.0"
    id("nu.studer.jooq") version "9.0"
    id("org.liquibase.gradle") version "2.2.1"
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

val jooqVersion = "3.19.29"
val postgresqlVersion = "42.7.3"
val jwtVersion = "0.11.5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-kotlin:$jooqVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.projectlombok:lombok:1.18.34")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("org.mapstruct:mapstruct:1.5.2.Final")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.2.Final")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.dbunit:dbunit:2.7.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    jooqGenerator("org.postgresql:postgresql:42.7.3")

    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.postgresql:postgresql:$postgresqlVersion")
    liquibaseRuntime("info.picocli:picocli:4.7.6")
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/jooq"))
}

springBoot {
    mainClass.set("maks.molch.dmitr.badminton_service.BadmintonServiceApplication")
}

tasks.named("clean") {
    delete(layout.buildDirectory.dir("generated"))
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

fun getEnv(name: String, defaultValue: String): String =
    System.getenv(name) ?: defaultValue

val dbUrl = getEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/badminton_db")
val dbUser = getEnv("SPRING_DATASOURCE_USERNAME", "badminton")
val dbPassword = getEnv("SPRING_DATASOURCE_PASSWORD", "badminton_pass")

jooq {
    version.set(jooqVersion)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }

                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"

                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "badminton"
                        excludes = "databasechangelog|databasechangeloglock"
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = true
                        isPojosAsKotlinDataClasses = false
                    }

                    target.apply {
                        packageName = "maks.molch.dmitr.badminton_service.generated.jooq"
                        directory = layout.buildDirectory.dir("generated/jooq").get().asFile.path
                    }
                }
            }
        }
    }
}

sourceSets {
    named("main") {
        java.srcDir("$openApiOutputDir/src/main/java")
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.register<JavaExec>("liquibaseUpdate") {
    group = "Liquibase"
    description = "Run Liquibase update using JavaExec"

    mainClass.set("liquibase.integration.commandline.Main")
    classpath = configurations.liquibaseRuntime.get()

    args(
        "--changeLogFile=src/main/resources/db/changelog/db.changelog-master.yaml",
        "--url=$dbUrl",
        "--username=$dbUser",
        "--password=$dbPassword",
        "update"
    )
}

tasks.register("generateDb") {
    dependsOn("generateJooq")
}

tasks.named("generateJooq") {
    dependsOn("liquibaseUpdate")
}

tasks.named("compileJava") {
    dependsOn("generateDb")
}