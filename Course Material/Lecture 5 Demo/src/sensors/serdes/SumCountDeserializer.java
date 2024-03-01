package sensors.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import sensors.model.SumCount;

public class SumCountDeserializer implements Deserializer<SumCount> {
    StringDeserializer s = new StringDeserializer();

    @Override
    public SumCount deserialize(String topic, byte[] data) {
        String res = s.deserialize(topic, data);
        String[] i = res.split(" ");
        return new SumCount(Double.parseDouble(i[0]), Long.parseLong(i[1]));
    }
}
