package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

val json =
    ObjectMapper().apply {
        registerModule(JodaModule())
    }

var environment = mutableMapOf<String, String>()

fun <K, V> MutableMap<K, out V>.getOrThrow(key: K): V = get(key) ?: throw IllegalArgumentException("$key not set")

val kafkaProducer: KafkaProducer by lazy {
    KafkaProducer(
        bootstrapServers = System.getenv().getOrThrow("KAFKA_BOOTSTRAPSERVERS"),
        username = System.getenv().getOrThrow("KAFKA_USERNAME"),
        password = System.getenv().getOrThrow("KAFKA_PASSWORD"),
        topic = System.getenv().getOrThrow("KAFKA_TOPIC"),
    )
}

val s3 =
    S3Client
        .builder()
        .region(Region.EU_CENTRAL_1)
        .httpClient(ApacheHttpClient.builder().build())
        .build()

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
        json.writeValueAsString(
            environment.mapValues { (key, value) ->
                if (key.startsWith("KAFKA_")) "*****" else value
            },
        ),
    )
    context.logger.log("s3event:")
    context.logger.log(json.writeValueAsString(s3Event))
}

class KotlinLambdaEventFunction : RequestHandler<S3Event?, String?> {
    override fun handleRequest(
        s3Event: S3Event?,
        context: Context?,
    ): String {
        log(context!!, s3Event!!)
        s3Event.records.forEach { processS3Record(it, context) }
        return "ok"
    }
}
