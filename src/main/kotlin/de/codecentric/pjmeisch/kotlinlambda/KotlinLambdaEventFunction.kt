package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
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

val httpClient = JavaHttpClient()

fun eventFnHandler() =
    FnHandler { s3Event: S3Event, context: Context ->
        context.logger.log("env:")
        context.logger.log(json.asFormatString(environment))
        context.logger.log("s3event:")
        context.logger.log(json.asFormatString(s3Event))

        // download the object from S3
        val s3 =
            S3Client
                .builder()
                .region(Region.EU_CENTRAL_1)
                .httpClient(AwsSdkClient(httpClient))
                .build()

        s3Event.records.forEach { record ->
            val objectBytes =
                s3.getObjectAsBytes { requestBuilder ->
                    requestBuilder.bucket(record.s3.bucket.name)
                    requestBuilder
                        .key(record.s3.`object`.key)
                        .build()
                }
            val contentType = objectBytes.response().contentType()
            context.logger.log("content-type: $contentType")
            if (contentType == "application/json") {
                val s = objectBytes.asUtf8String()
                context.logger.log("object content as string: $s")
            }
        }
    }

fun eventFnLoader() =
    FnLoader { env: Map<String, String> ->
        environment += env
        eventFnHandler()
    }

class KotlinLambdaEventFunction : AwsLambdaEventFunction(eventFnLoader())
