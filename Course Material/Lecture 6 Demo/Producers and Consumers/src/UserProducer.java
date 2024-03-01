import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import utils.KafkaConfiguration;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserProducer {

    private static final String TOPIC_BALANCE = "user-balances";

    private static final Random RNG = new Random();

    public static void main(String[] args) {
        KafkaSender<Integer, Double> balanceSender = KafkaConfiguration.createSender(IntegerSerializer.class, DoubleSerializer.class, "user-balance-producer");
        AtomicBoolean b = new AtomicBoolean(false);
        Flux.<ProducerRecord<Integer, Double>>generate(sink -> {
            if (b.get()) {
                sink.next(new ProducerRecord<>(TOPIC_BALANCE, 1, RNG.nextDouble(-1000, -501)));
                b.set(false);
                return;
            }
            int userId = RNG.nextInt(10);
            if (userId == 1 && RNG.nextDouble() < 0.5) {
                b.set(true);
                sink.next(new ProducerRecord<>(TOPIC_BALANCE, userId, RNG.nextDouble(-0.09, -0.01)));
            } else {
                double balance = RNG.nextDouble(-1000, 1000);
                sink.next(new ProducerRecord<>(TOPIC_BALANCE, userId, balance));
            }
        })
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(x -> System.out.printf("%d -> %f\n", x.key(), x.value()))
                .map(x -> Mono.just(SenderRecord.create(x, System.currentTimeMillis())))
                .flatMap(balanceSender::send)
                .then()
                .block();
    }

}
