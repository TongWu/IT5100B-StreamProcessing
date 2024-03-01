package sensors;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import utils.KafkaConfiguration;

import java.util.Properties;

public class SensorDataAdjuster {
    private static final String SOURCE_TOPIC = "sensor-data-raw";
    private static final String SINK_TOPIC = "sensor-data-adjusted";

    public static void main(String[] args) {
        Properties p = KafkaConfiguration.getKafkaStreamsConfiguration("sensor-data-adjuster");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<Integer, Double> source = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.Integer(), Serdes.Double()));

        source.peek((k, v) -> System.out.printf("key: %d -> value: %.2f\n", k, v))
                .filter((k, v) -> v >= 0)
                .mapValues(v -> v - 273.15)
                .peek((k, v) -> System.out.printf("key: %d -> new value: %.2f\n", k, v))
                .to(SINK_TOPIC, Produced.with(Serdes.Integer(), Serdes.Double()));

        KafkaStreams s = new KafkaStreams(builder.build(), p);
        s.start();
    }
}
