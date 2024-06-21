package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.http4k.aws.AwsSdkClient
import org.http4k.client.JavaHttpClient
import org.http4k.format.Jackson
import org.http4k.serverless.AwsLambdaEventFunction
import org.http4k.serverless.FnHandler
import org.http4k.serverless.FnLoader
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

val json =
    Jackson.apply {
        mapper.registerModule(JodaModule())
    }

var environment = mutableMapOf<String, String>()

fun <K, V> MutableMap<K, out V>.getOrThrow(key: K): V = get(key) ?: throw IllegalArgumentException("$key not set")

val kafkaProducer: KafkaProducer by lazy {
    KafkaProducer(
        bootstrapServers = environment.getOrThrow("KAFKA_BOOTSTRAPSERVERS"),
        username = environment.getOrThrow("KAFKA_USERNAME"),
        password = environment.getOrThrow("KAFKA_PASSWORD"),
        topic = environment.getOrThrow("KAFKA_TOPIC"),
    )
}

val s3 =
    S3Client
        .builder()
        .region(Region.EU_CENTRAL_1)
        .httpClient(AwsSdkClient(JavaHttpClient()))
        .build()

fun eventFnHandler() =
    FnHandler { s3Event: S3Event, context: Context ->
        log(context, s3Event)
        s3Event.records.forEach { processS3Record(it, context) }
    }

private fun processS3Record(
    record: S3EventNotification.S3EventNotificationRecord,
    context: Context,
) {
    val objectBytes =
        s3.getObjectAsBytes { requestBuilder ->
            requestBuilder.bucket(record.s3.bucket.name).key(record.s3.`object`.key)
        }

    if (objectBytes.response().contentType() == "application/json") {
        kafkaProducer.send(objectBytes.asUtf8String(), context)
    }
}

private fun log(
    context: Context,
    s3Event: S3Event,
) {
    context.logger.log("environment:")
    context.logger.log(
        json.asFormatString(
            environment.mapValues { (_, value) ->
                if (value.startsWith("KAFKA_")) "*****" else value
            },
        ),
    )
    context.logger.log("s3event:")
    context.logger.log(json.asFormatString(s3Event))
}

fun eventFnLoader() =
    FnLoader { env: Map<String, String> ->
        environment += env
        eventFnHandler()
    }

class KotlinLambdaEventFunction : AwsLambdaEventFunction(eventFnLoader())
