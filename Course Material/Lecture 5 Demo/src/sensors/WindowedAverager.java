package sensors;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import sensors.model.SumCount;
import sensors.serdes.SumCountDeserializer;
import sensors.serdes.SumCountSerializer;
import utils.KafkaConfiguration;

import java.time.Duration;
import java.util.Properties;

public class WindowedAverager {
    private static final String SOURCE_TOPIC = "sensor-data-adjusted";
    private static final String SINK_TOPIC = "sensor-data-windowed";
    public static void main(String[] args) {
        Properties p = KafkaConfiguration.getKafkaStreamsConfiguration("windowed-averager");
        Serde<SumCount> sumCountSerde = Serdes.serdeFrom(new SumCountSerializer(), new SumCountDeserializer());
        SlidingWindows window = SlidingWindows
                .ofTimeDifferenceAndGrace(Duration.ofSeconds(5), Duration.ofSeconds(2));

        StreamsBuilder builder = new StreamsBuilder();

        KStream<Integer, Double> source = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.Integer(), Serdes.Double()));

        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.Integer(), Serdes.Double()))
                .groupByKey()
                .windowedBy(window)
                .aggregate(SumCount::new, (k, v, s) -> s.put(v), Materialized.with(Serdes.Integer(), sumCountSerde))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .mapValues(SumCount::get)
                .peek((k, v) -> System.out.printf("%s: %.2f\n", k.window(), v))
                .map((k, v) -> KeyValue.pair(k.key(), v))
                .to(SINK_TOPIC, Produced.with(Serdes.Integer(), Serdes.Double()));

        KafkaStreams s = new KafkaStreams(builder.build(), p);
        s.start();

    }
}
