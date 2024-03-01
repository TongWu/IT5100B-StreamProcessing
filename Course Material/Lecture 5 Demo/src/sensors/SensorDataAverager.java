package sensors;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import sensors.model.SumCount;
import sensors.serdes.SumCountDeserializer;
import sensors.serdes.SumCountSerializer;
import utils.KafkaConfiguration;

import java.util.Properties;

public class SensorDataAverager {
    private static final String SOURCE_TOPIC = "sensor-data-adjusted";
    private static final String SINK_TOPIC = "sensor-data-avg";

    public static void main(String[] args) {
        Properties p = KafkaConfiguration.getKafkaStreamsConfiguration("sensor-data-averager");
        Serde<SumCount> sumCountSerde = Serdes.serdeFrom(new SumCountSerializer(), new SumCountDeserializer());

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.Integer(), Serdes.Double()))
                .groupByKey()
                .aggregate(SumCount::new,
                        (k, v, s) -> s.put(v),
                        Materialized.with(Serdes.Integer(), sumCountSerde))
                .mapValues(SumCount::get)
                .toStream()
                .peek((k, v) -> System.out.printf("%d: %.2f\n", k, v))
                .to(SINK_TOPIC, Produced.with(Serdes.Integer(), Serdes.Double()));

        KafkaStreams s = new KafkaStreams(builder.build(), p);
        s.start();

    }
}

