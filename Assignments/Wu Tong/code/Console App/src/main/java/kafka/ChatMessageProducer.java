package kafka;

import kafka.serdes.ChatMessageSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Produces events to Kafka.
 */
public class ChatMessageProducer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    private static final String TOPIC = "users";
    private final Logger log = LoggerFactory.getLogger(ChatMessageProducer.class);
    private final KafkaSender<UUID, ChatMessage> sender;

    /**
     * Creates a new producer to Kafka
     * @param logLevel The desired logging level.
     */
    public ChatMessageProducer(Level logLevel) {
        // configure Kafka
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "ChatMessage-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ChatMessageSerializer.class);
        SenderOptions<UUID, ChatMessage> senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);

        // configure logger
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();
    }

    /**
     * Creates a publisher such that once subscribed, sends an event to Kafka
     * @param chatMessage
     * @return The publisher such that once subscribed, sends the event to Kafka.
     */
    public Mono<Void> send(ChatMessage chatMessage) {
        return Mono.create(sink -> {
            sender.send(Mono.just(chatMessage)
                    .map(message -> SenderRecord.create(new ProducerRecord<>(TOPIC, message.getSenderID(), message), null)))
                    .subscribe(result -> {
                        // Log message
                        log.info("Message sent to topic {}", result.recordMetadata().topic());
                        sink.success();
                    }, error -> {
                        // Log error
                        log.error("Failed: ", error);
                        sink.error(error);
                    });
        });
        //return null;
    }

    /**
     * Closes the producer.
     */
    public void close() {
        sender.close();
    }

}
