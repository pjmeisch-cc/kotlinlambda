plugins {
    kotlin("jvm") version "1.9.22"
}

group = "de.codecentric.pjmeisch"
version = "1.0-SNAPSHOT"

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
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")
    implementation(platform("org.http4k:http4k-bom:5.13.6.1"))
    implementation("org.http4k:http4k-aws")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-serverless-core")
    implementation("org.http4k:http4k-serverless-lambda-runtime")
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    // we use jackson to log the event and need the jodatime converter for that
    implementation("org.http4k:http4k-format-jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.16.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
