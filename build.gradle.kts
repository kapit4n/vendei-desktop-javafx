plugins {
    application
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // DB
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.flywaydb:flyway-core:10.17.0")
    implementation("org.jooq:jooq:3.19.10")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")

    // Logging (simple + good defaults)
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.vendei.desktop.Main")
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls")
}

