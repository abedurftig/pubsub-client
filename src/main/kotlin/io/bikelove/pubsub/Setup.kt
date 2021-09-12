package io.bikelove.pubsub

fun main(args: Array<String>) {

    val topicName = "strava-events"
    val subscriptionName = "strava-events-sub"

    val helper = PubSubHelper("bikelove-platform")
    println("created helper")

    helper.createTopic(topicName)
    println("created topic")
    helper.createSubscription(subscriptionName, topicName)
    println("created subscription")
}