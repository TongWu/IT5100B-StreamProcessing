import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class UserConsumer {
    public static void main(String[] args) {
        KafkaReceiver<Integer, Double> receiver = createReceiver(List.of("user-balances"), "balance-consumer");
        receiver.receive().subscribe(event -> {
            System.out.printf("%d: %s\n", event.key(), event.value());
            event.receiverOffset().commit().subscribe();
        });
        KafkaReceiver<Integer, Double> txnAlerts = createReceiver(List.of("user-alerts"), "alert-consumer");
        txnAlerts.receive().doOnNext(event -> {
            long timestamp = event.timestamp();
            Date date = new Date(timestamp);
            System.out.printf("ALERT: potential fraudulent transaction at %s: user %d amount $%.2f\n", date, event.key(), event.value());
            event.receiverOffset().commit().subscribe();
        }).blockLast();
    }

    public static KafkaReceiver<Integer, Double> createReceiver(Collection<String> topics, String groupId) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        p.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-users");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DoubleDeserializer.class);
        ReceiverOptions<Integer, Double> receiverOptions = ReceiverOptions.create(p);
        return KafkaReceiver.create(receiverOptions.subscription(topics));
    }
}
