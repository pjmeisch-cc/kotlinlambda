package de.codecentric.pjmeisch.kotlinlambda

import com.amazonaws.services.lambda.runtime.Context
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.UUID

class KafkaProducer(
    val bootstrapServers: String,
    val username: String,
    val password: String,
    private val topic: String,
) {
    private var kafkaProducer: KafkaProducer<String, String>

    init {
        val producerProps =
            mapOf<String, String>(
                "bootstrap.servers" to bootstrapServers,
                "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
                "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
                "security.protocol" to "SASL_SSL",
                "sasl.mechanism" to "PLAIN",
                "sasl.jaas.config" to
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username='$username' password='$password';",
            )
        kafkaProducer = KafkaProducer<String, String>(producerProps)
    }

    fun send(
        value: String,
        context: Context,
    ) {
        context.logger.log("sending: $value")

        val key = UUID.randomUUID().toString()
        kafkaProducer.send(ProducerRecord(topic, key, value)).get()
    }
}
