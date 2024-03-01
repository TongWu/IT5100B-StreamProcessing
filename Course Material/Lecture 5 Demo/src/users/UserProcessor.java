package users;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import users.model.User;
import users.serdes.UserDeserializer;
import users.serdes.UserSerializer;
import utils.KafkaConfiguration;

import java.util.Properties;

public class UserProcessor {
    private static final String NAMES_TOPIC = "user-names";
    private static final String BALANCES_TOPIC = "user-balances";
    private static final String SINK_TOPIC = "users";
    public static void main(String[] args) {
        Properties p = KafkaConfiguration.getKafkaStreamsConfiguration("user-processor");
        Serde<User> userSerde = Serdes.serdeFrom(new UserSerializer(), new UserDeserializer());

        StreamsBuilder builder = new StreamsBuilder();

        KTable<Integer, String> names = builder.table(NAMES_TOPIC, Consumed.with(Serdes.Integer(), Serdes.String()));
        KStream<Integer, Double> balances = builder.stream(BALANCES_TOPIC, Consumed.with(Serdes.Integer(), Serdes.Double()));
        KTable<Integer, Double> aggregatedBalances = balances.groupByKey()
                .reduce(Double::sum);

        KTable<Integer, User> joinedTable = names.join(aggregatedBalances, (x, y) -> User.empty(-1).ofName(x).ofAccountBalance(y), Materialized.with(Serdes.Integer(), userSerde));

        joinedTable.toStream().map((k, v) -> KeyValue.pair(k, v.ofId(k)))
                .peek((k, v) -> System.out.println(v))
                .to(SINK_TOPIC, Produced.with(Serdes.Integer(), userSerde));

        KafkaStreams s = new KafkaStreams(builder.build(), p);
        s.start();
    }
}
