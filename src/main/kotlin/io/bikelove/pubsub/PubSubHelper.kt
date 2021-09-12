package io.bikelove.pubsub

import com.google.api.gax.core.CredentialsProvider
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.api.gax.rpc.TransportChannelProvider
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.cloud.pubsub.v1.stub.GrpcPublisherStub
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub
import com.google.cloud.pubsub.v1.stub.PublisherStubSettings
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.PublishRequest
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.PullRequest
import com.google.pubsub.v1.PullResponse
import com.google.pubsub.v1.PushConfig
import com.google.pubsub.v1.TopicName
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder


class PubSubHelper(
    private val projectId: String
) {

    companion object {

        private val channel = managedChannel()
        var channelProvider: TransportChannelProvider =
            FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
        var credentialsProvider: CredentialsProvider = NoCredentialsProvider.create()

        private fun managedChannel(): ManagedChannel {
            val hostPort = System.getenv("PUBSUB_EMULATOR_HOST")
            return ManagedChannelBuilder.forTarget(hostPort).usePlaintext().build()
        }
    }

    fun createTopic(
        topicId: String
    ) {
        val topicAdminSettings = TopicAdminSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build()
        TopicAdminClient.create(topicAdminSettings).use { topicAdminClient ->
            val topicName = TopicName.of(projectId, topicId)
            topicAdminClient.createTopic(topicName)
        }
    }

    private fun createSubscriberStubSettings() =
        SubscriberStubSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build()

    fun createSubscription(
        subscriptionId: String,
        topicId: String,
    ) {
        val subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build()

        val subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)
        val subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId)
        subscriptionAdminClient.createSubscription(
            subscriptionName,
            TopicName.of(projectId, topicId),
            PushConfig.getDefaultInstance(),
            10
        )
    }

    fun publishMessage(topicName: String, message: String) {

        val message = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message))
        val publishRequest = publishRequest(message, topicName)

        val publisher = GrpcPublisherStub.create(publisherStubSettings())
        publisher.publishCallable().call(publishRequest)
    }

    private fun publisherStubSettings(): PublisherStubSettings {
        return PublisherStubSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build()
    }

    private fun publishRequest(
        message: PubsubMessage.Builder,
        topicName: String
    ): PublishRequest {
        return PublishRequest.newBuilder()
            .addMessages(message)
            .setTopic(topicName)
            .build()
    }

    fun pullMessages(
        subscriptionId: String
    ): PullResponse {

        val subscriberStubSettings = createSubscriberStubSettings()
        val subscriber = GrpcSubscriberStub.create(subscriberStubSettings)

        val pullRequest = PullRequest.newBuilder()
            .setMaxMessages(1)
            .setSubscription(
                ProjectSubscriptionName.format(
                    projectId,
                    subscriptionId
                )
            )
            .build()

        return subscriber.pullCallable().call(pullRequest)
    }
}