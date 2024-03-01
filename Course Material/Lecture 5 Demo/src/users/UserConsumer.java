package users;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import users.model.User;
import users.serdes.UserDeserializer;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class UserConsumer {
    public static void main(String[] args) {
        KafkaReceiver<Integer, User> receiver = createReceiver(List.of("users"));
        receiver.receive().doOnNext(event -> {
            System.out.printf("%d: %s\n", event.key(), event.value());
            event.receiverOffset().commit().subscribe();
        }).blockLast();
    }
    public static KafkaReceiver<Integer, User> createReceiver(Collection<String> topics) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "my-simple-group");
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-users");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDeserializer.class);
        ReceiverOptions<Integer, User> receiverOptions = ReceiverOptions.create(p);
        return KafkaReceiver.create(receiverOptions.subscription(topics));
    }
}
