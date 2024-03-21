import kafka.ChatMessage;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import serdes.ChatMessageDeserializationSchema;
import serdes.ChatMessageSerializationSchema;

public class Main {
    public static final String BOOTSTRAP_SERVERS = "kafka0:9094,kafka1:9093,kafka2:9092";
    public static final String SOURCE_TOPIC = "users";
    public static final String SINK_TOPIC = "message-adjusted";

    public static void main(String[] args) throws Exception {
        // Initialize an environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //Set Kafka datasource and sink
        KafkaSource<ChatMessage> source = KafkaSource.<ChatMessage>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(SOURCE_TOPIC)
                .setGroupId("flink-kafkaevent-adjuster")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new ChatMessageDeserializationSchema())
                .build();

        KafkaSink<ChatMessage> sink = KafkaSink.<ChatMessage>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(SINK_TOPIC)
                        .setValueSerializationSchema(new ChatMessageSerializationSchema())
                        .build())
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        // Set sensitive words list
        final String[] sensitiveWords = new String[]{"bruh", "shot", "fux"};

        // Process the message replacing
        env.fromSource(source, WatermarkStrategy.noWatermarks(), SOURCE_TOPIC)
                        .map((MapFunction<ChatMessage, ChatMessage>) value -> {
                            String messageContent = value.getMessage();
                            for (String word : sensitiveWords) {
                                messageContent = messageContent.replaceAll(word, "***");
                            }
                            value.setMessage(messageContent);
                            return value;
                        }).name("Sensitive Words Filtering")
                        .sinkTo(sink).name("KafkaEvent Sink");

        env.execute("Sensitive Message");
    }
}
