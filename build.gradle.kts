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
    implementation(platform("org.http4k:http4k-bom:5.24.0.0"))
    implementation("org.http4k:http4k-aws")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-serverless-core")
    implementation("org.http4k:http4k-serverless-lambda-runtime")
    implementation("org.http4k:http4k-serverless-lambda")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")

    implementation("software.amazon.awssdk:s3:2.26.11")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.6")

    implementation("org.apache.kafka:kafka-clients:3.7.0")

    // we use jackson to log the event and need the jodatime converter for that
    implementation("org.http4k:http4k-format-jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.18.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
