package sensors.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import sensors.model.SumCount;

public class SumCountSerializer implements Serializer<SumCount> {
    StringSerializer s = new StringSerializer();
    @Override
    public byte[] serialize(String topic, SumCount data) {
        return s.serialize(topic, String.format("%f %d", data.sum, data.count));
    }
}

