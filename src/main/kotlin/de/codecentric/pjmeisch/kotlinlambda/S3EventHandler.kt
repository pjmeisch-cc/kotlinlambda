package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.http4k.format.Jackson
import org.http4k.serverless.AwsLambdaEventFunction
import org.http4k.serverless.FnHandler
import org.http4k.serverless.FnLoader

val json = Jackson.apply {
    mapper.registerModule(JodaModule())
}

fun eventFnHandler() =
    FnHandler { s3Event: S3Event, context: Context ->
        context.logger.log(json.asFormatString(s3Event))
    }

fun eventFnLoader() =
    FnLoader { _: Map<String, String> ->
        eventFnHandler()
    }

class KotlinLambdaEventFunction : AwsLambdaEventFunction(eventFnLoader())
