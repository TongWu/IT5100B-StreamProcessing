package users;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import utils.KafkaConfiguration;

import java.time.Duration;
import java.util.Random;

public class RandomUserEventProducer {
    private static final String TOPIC_NAMES = "user-names";
    private static final String TOPIC_BALANCE = "user-balances";

    private static final String[] randomNames = new String[]{"Wade", "Dave", "Seth", "Ivan", "Riley", "Gilbert", "Jorge", "Dan", "Brian", "Roberto", "Ramon", "Miles", "Liam", "Nathaniel", "Ethan", "Lewis", "Milton", "Claude", "Joshua", "Glen", "Harvey", "Blake", "Antonio", "Connor", "Julian", "Aidan", "Harold", "Conner", "Peter", "Hunter", "Eli", "Alberto", "Carlos", "Shane", "Aaron", "Marlin", "Paul", "Ricardo", "Hector", "Alexis", "Adrian", "Kingston", "Douglas", "Gerald", "Joey", "Johnny", "Charlie", "Scott", "Martin", "Tristin", "Troy", "Tommy", "Rick", "Victor", "Jessie", "Neil", "Ted", "Nick", "Wiley", "Morris", "Clark", "Stuart", "Orlando", "Keith", "Marion", "Marshall", "Noel", "Everett", "Romeo", "Sebastian", "Stefan", "Robin", "Clarence", "Sandy", "Ernest", "Samuel", "Benjamin", "Luka", "Fred", "Albert", "Greyson", "Terry", "Cedric", "Joe", "Paul", "George", "Bruce", "Christopher", "Mark", "Ron", "Craig", "Philip", "Jimmy", "Arthur", "Jaime", "Perry", "Harold", "Jerry", "Shawn", "Walter"};
    private static final Random RNG = new Random();

    public static void main(String[] args) {
        KafkaSender<Integer, Double> balanceSender = KafkaConfiguration.createSender(IntegerSerializer.class, DoubleSerializer.class, "user-balance-producer");
        KafkaSender<Integer, String> nameSender = KafkaConfiguration.createSender(IntegerSerializer.class, StringSerializer.class, "user-name-producer");
        Mono<Void> balances = Flux.<ProducerRecord<Integer, Double>>generate(sink -> {
            int userId = RNG.nextInt(10);
            double balance = RNG.nextDouble(-1000, 1000);
            sink.next(new ProducerRecord<Integer, Double>(TOPIC_BALANCE, userId, balance));
        }).delayElements(Duration.ofSeconds(1)).doOnNext(x -> System.out.printf("%d -> %f\n", x.key(), x.value())).map(x -> Mono.just(SenderRecord.create(x, System.currentTimeMillis()))).flatMap(balanceSender::send).then();
        Mono<Void> names = Flux.<ProducerRecord<Integer, String>>generate(sink -> {
            int userId = RNG.nextInt(10);
            String name = randomNames[RNG.nextInt(randomNames.length)];
            sink.next(new ProducerRecord<Integer, String>(TOPIC_NAMES, userId, name));
        }).delayElements(Duration.ofSeconds(10)).doOnNext(x -> System.out.printf("%d -> %s\n", x.key(), x.value())).map(x -> Mono.just(SenderRecord.create(x, System.currentTimeMillis()))).flatMap(nameSender::send).then();
        Mono.zip(balances, names).block();

    }

}
