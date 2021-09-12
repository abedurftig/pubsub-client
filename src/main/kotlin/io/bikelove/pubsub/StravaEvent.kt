package io.bikelove.pubsub

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class StravaEvent(
    @JsonProperty("object_type")
    val objectType: String,
    @JsonProperty("object_id")
    val objectId: Long,
    @JsonProperty("aspect_type")
    val aspectType: String,
    @JsonInclude
    val updates: Map<String, String>,
    @JsonProperty("owner_id")
    val ownerId: Long,
    @JsonProperty("subscription_id")
    val subscriptionId: Int,
    @JsonProperty("event_time")
    val eventTime: Long
)
