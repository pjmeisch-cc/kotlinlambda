package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class KotlinLambdaEventFunctionTest {
    @Test
    fun test() {
        val kotlinLambdaEventFunction = KotlinLambdaEventFunction()

        val input =
            """
            {
              "Records": [
                {
                  "eventVersion": "2.0",
                  "eventSource": "aws:s3",
                  "awsRegion": "us-east-1",
                  "eventTime": "1970-01-01T00:00:00.000Z",
                  "eventName": "ObjectCreated:Put",
                  "userIdentity": {
                    "principalId": "EXAMPLE"
                  },
                  "requestParameters": {
                    "sourceIPAddress": "127.0.0.1"
                  },
                  "responseElements": {
                    "x-amz-request-id": "EXAMPLE123456789",
                    "x-amz-id-2": "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH"
                  },
                  "s3": {
                    "s3SchemaVersion": "1.0",
                    "configurationId": "testConfigRule",
                    "bucket": {
                      "name": "example-bucket",
                      "ownerIdentity": {
                        "principalId": "EXAMPLE"
                      },
                      "arn": "arn:aws:s3:::example-bucket"
                    },
                    "object": {
                      "key": "test%2Fkey",
                      "size": 1024,
                      "eTag": "0123456789abcdef0123456789abcdef",
                      "sequencer": "0A1B2C3D4E5F678901"
                    }
                  }
                }
              ]
            }
            """.trimIndent()

        val inputStream = input.byteInputStream(Charset.forName("UTF-8"))
        val outputStream = ByteArrayOutputStream()
        val context: Context =
            object : Context {
                override fun getAwsRequestId(): String = "awsRequestId"

                override fun getLogGroupName(): String = "logGroupName"

                override fun getLogStreamName(): String = "logStreamName"

                override fun getFunctionName(): String = "functionName"

                override fun getFunctionVersion(): String = "functionVersion"

                override fun getInvokedFunctionArn(): String = "invokedFunctionArn"

                override fun getRemainingTimeInMillis(): Int = 1000

                override fun getMemoryLimitInMB(): Int = 128

                override fun getLogger(): LambdaLogger =
                    object : LambdaLogger {
                        override fun log(message: String) {
                            println(message)
                        }

                        override fun log(message: ByteArray) {
                            println(String(message))
                        }
                    }

                override fun getIdentity(): CognitoIdentity? = throw UnsupportedOperationException("Not implemented")

                override fun getClientContext(): ClientContext = throw UnsupportedOperationException("Not implemented")
            }

        kotlinLambdaEventFunction.handleRequest(inputStream, outputStream, context)
    }
}
