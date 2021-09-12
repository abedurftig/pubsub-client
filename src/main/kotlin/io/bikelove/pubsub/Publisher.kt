package io.bikelove.pubsub

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.Instant

fun main(args: Array<String>) {

    val topicName = "projects/bikelove-platform/topics/strava-events"

    val helper = PubSubHelper("bikelove-platform")
    val mapper = ObjectMapper()
    mapper.registerKotlinModule()

    val event = StravaEvent(
        objectType = "activity",
        objectId = 5894795633,
        aspectType = "create",
        updates = emptyMap(),
        ownerId = 5546232,
        subscriptionId = 123,
        eventTime = Instant.now().toEpochMilli()
    )

    helper.publishMessage(topicName, mapper.writeValueAsString(event))
}