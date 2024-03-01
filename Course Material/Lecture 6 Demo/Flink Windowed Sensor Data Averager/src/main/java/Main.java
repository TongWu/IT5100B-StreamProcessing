import model.KafkaEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import serdes.KafkaEventDeserializer;
import serdes.KafkaEventSerializer;

import java.time.Duration;

public class Main {
    public static final String BOOTSTRAP_SERVERS = "kafka0:9094,kafka1:9094,kafka2:9092";
    public static final String SOURCE_TOPIC = "sensor-data-adjusted";
    public static final String SINK_TOPIC = "sensor-data-avg";

    public static final String ALERT_TOPIC = "sensor-data-high-alert";

    public static void main(String[] args) throws Exception {
        // cleaned temperate readings
        KafkaSource<KafkaEvent> source = KafkaSource.<KafkaEvent>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(SOURCE_TOPIC)
                .setGroupId("flink-consumer-adjusted")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setDeserializer(new KafkaEventDeserializer(SOURCE_TOPIC))
                .setProperty("partition.discovery.interval.ms", "60000")
                .setProperty("commit.offsets.on.checkpoint", "true")
                .build();

        // windowed averages sink
        KafkaSink<KafkaEvent> sink = KafkaSink.<KafkaEvent>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(new KafkaEventSerializer(SINK_TOPIC))
                .setTransactionalIdPrefix("flink-avg")
                .build();

        // fire alert sink
        KafkaSink<KafkaEvent> alerts = KafkaSink.<KafkaEvent>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(new KafkaEventSerializer(ALERT_TOPIC))
                .setTransactionalIdPrefix("flink-alerts")
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().enableCheckpointing(60000);
        WatermarkStrategy<KafkaEvent> watermarkStrategy = WatermarkStrategy.<KafkaEvent>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                .withTimestampAssigner((e, t) -> e.timestamp);
        SlidingEventTimeWindows window = SlidingEventTimeWindows.of(Time.minutes(1), Time.seconds(10));

        // obtain the sliding window average temperatures
        DataStream<KafkaEvent> windowedAverages = env.fromSource(source, watermarkStrategy, SOURCE_TOPIC)
                .keyBy(x -> x.key)
                .window(window)
                .process(new ProcessWindowFunction<KafkaEvent, KafkaEvent, Integer, TimeWindow>() {
                    @Override
                    public void process(Integer integer, ProcessWindowFunction<KafkaEvent, KafkaEvent, Integer, TimeWindow>.Context context, Iterable<KafkaEvent> elements, Collector<KafkaEvent> out) throws Exception {
                        // obtain the average value
                        double total = 0;
                        long count = 0;
                        for (KafkaEvent e : elements) {
                            total += e.value;
                            count += 1;
                        }
                        // let window start time be event time
                        long windowStart = context.window().getStart();
                        if (count > 0)
                            out.collect(new KafkaEvent(integer, total / count, windowStart));
                    }
                }).name("windowed average");
        windowedAverages.sinkTo(sink);
        windowedAverages.filter(x -> x.value > 40).sinkTo(alerts);

        env.execute("Temperature Average");
    }
}
