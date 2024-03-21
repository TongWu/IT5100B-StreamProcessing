package kafka;

import kafka.serdes.ChatMessageDeserializer;
import kafka.serdes.ChatMessageSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import java.util.*;

public class ChatUser2 {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    private static final String TOPIC = "users";
    private static final String CONSUMER_TOPIC = "message-adjusted";
    private final Logger log = LoggerFactory.getLogger(ChatUser.class);
    private final UUID userUUID;
    private final KafkaSender<UUID, ChatMessage> sender;
    private final KafkaReceiver<UUID, ChatMessage> receiver;

    // Additional feature
    private static final long RATE_LIMIT_WINDOW_MS = 60000; // 1000 = 1s
    private static final int MAX_MESSAGES_PER_SECOND = 2; // Maximum message sent allowed per second
    private final ConcurrentHashMap<UUID, AtomicLong> lastMessageTimestamp = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, AtomicInteger> messageCount = new ConcurrentHashMap<>();


    public ChatUser2() {
        this.userUUID = UUID.randomUUID();
        // Producer config
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChatMessageSerializer.class);
        SenderOptions<UUID, ChatMessage> senderOptions = SenderOptions.create(producerProps);
        this.sender = KafkaSender.create(senderOptions);

        // Consumer config
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, userUUID.toString());
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, userUUID.toString());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ChatMessageDeserializer.class);
        ReceiverOptions<UUID, ChatMessage> receiverOptions = ReceiverOptions.create(p);
        this.receiver = KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(CONSUMER_TOPIC)));
    }

    public void sendMessage(UUID recipientUUID, String message) {
        // Check the message rate limiter
        AtomicLong lastTimestamp = lastMessageTimestamp.computeIfAbsent(recipientUUID, k -> new AtomicLong());
        AtomicInteger count = messageCount.computeIfAbsent(recipientUUID, k -> new AtomicInteger());

        long currentTimestamp = System.currentTimeMillis();
        long elapsedTime = currentTimestamp - lastTimestamp.get();

        if (elapsedTime < RATE_LIMIT_WINDOW_MS) {
            if (count.get() >= MAX_MESSAGES_PER_SECOND) {
                // If above the rate limiter, block sending
                System.out.println("Rate limit exceeded. Please wait before sending more messages.");
                System.out.println("The current rate limit is: " + MAX_MESSAGES_PER_SECOND + " per " + RATE_LIMIT_WINDOW_MS/1000 + " seconds.");
                return;
            }
            count.incrementAndGet();
        } else {
            lastTimestamp.set(currentTimestamp);
            count.set(1);
        }

        // Send message
        ChatMessage chatMessage = new ChatMessage(userUUID, recipientUUID, message);
        sender.send(Mono.just(chatMessage)
                .map(msg -> SenderRecord.create(new ProducerRecord<>(TOPIC, msg.getSenderID(), msg), null)))
                .subscribe(result -> log.info("Message sent to topic {}", result.recordMetadata().topic()),
                           error -> log.error("Failed to send message: ", error));
    }

    public void listenForMessages() {
        // Subscribe the message receiver
        receiver.receive()
                 .filter(record -> record.value().getRecipientID().equals(userUUID))
                 .map(ReceiverRecord::value)
                 .doOnNext(event -> System.out.println("Message from " + event.getSenderID() + ": " + event.getMessage()))
                 .subscribe(msg -> log.info("Received message: {}", msg.getMessage()));
    }

    public static void main(String[] args) {
        ChatUser2 user = new ChatUser2();
        System.out.println("Your User UUID: " + user.userUUID);
        user.listenForMessages();

        Scanner scanner = new Scanner(System.in);
        UUID recipientUUID = null;

        // Input valid UUID
        while (recipientUUID == null) {
            System.out.println("Enter recipient UUID: ");
            String input = scanner.nextLine();
            try {
                recipientUUID = UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID format. Please try again.");
            }
        }

        while (true) {
            System.out.println("Enter message: [CHANGE_RECIPIENT/EXIT]");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("EXIT")) {
                System.out.println("Exiting program.");
                System.exit(0);
            }

            if (message.equalsIgnoreCase("CHANGE_RECIPIENT")) {
                System.out.println("Enter new recipient UUID: ");
                recipientUUID = UUID.fromString(scanner.nextLine());
                System.out.println("Recipient changed to: " + recipientUUID);
                continue;
            } else {
                user.sendMessage(recipientUUID, message);
            }
        }
    }
}

