plugins {
    kotlin("jvm") version "2.0.0"
}

group = "de.codecentric.pjmeisch"
version = "1.2.1"

tasks {
    register<Zip>("buildLambdaZip") {
        from(compileKotlin)
        from(processResources)
        into("lib") {
            from(configurations.compileClasspath)
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.6")
    implementation("software.amazon.awssdk:apache-client:2.26.11")
    implementation("software.amazon.awssdk:s3:2.26.11")

    implementation("org.apache.kafka:kafka-clients:3.7.0")

    // we use jackson to log the event and need the jodatime converter for that
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.17.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
