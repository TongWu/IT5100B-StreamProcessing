package kafka;

import kafka.serdes.ChatMessageDeserializer;
import kafka.serdes.ChatMessageSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes state changes from Kafka.
 */
public class ChatMessageConsumer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    private static final String TOPIC = "users";
    private final UUID currentUserUUID;
    private final ReceiverOptions<UUID, ChatMessage> receiverOptions;

    /**
     * Creates a new Kafka consumer.
     */
    public ChatMessageConsumer(UUID currentUserUUID) {
        this.currentUserUUID = currentUserUUID;
        // Create properties.
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "ChatMessage-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ChatMessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
    }

    /**
     * Returns a publisher that emits new events from Kafka.
     * @return A publisher that emits new events from Kafka.
     */
    public Flux<ChatMessage> consume() {
        // Subscribe to users topic
        ReceiverOptions<UUID, ChatMessage> options = receiverOptions.subscription(Collections.singleton(TOPIC));
        // Create the publisher
        Flux<ReceiverRecord<UUID, ChatMessage>> kafkaFlux = KafkaReceiver.create(options).receive();
        // Map each ConsumerRecord to just the UserStateChange
        return kafkaFlux
                .map(ReceiverRecord::value)
                .filter(message -> message.getRecipientID().equals(currentUserUUID));
    }

    /**
     * Consumes events from Kafka.
     * to the console.
     * @param args Not used.
     */
    public static void main(String[] args) {
        UUID currentUserUUID = UUID.randomUUID();
        System.out.println("Receive message to: " + currentUserUUID);
        ChatMessageConsumer consumer = new ChatMessageConsumer(currentUserUUID);
        consumer.consume().subscribe(chatMessage -> {
            System.out.println("Message from " + chatMessage.getSenderID() + ": " + chatMessage.getMessage());
        });
    }
}