package kafka.serdes;

import kafka.model.UserStateChange;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class UserStateChangeSerializer implements Serializer<UserStateChange> {
    public final StringSerializer s = new StringSerializer();
    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data  typed data
     * @return serialized bytes
     */
    @Override
    public byte[] serialize(String topic, UserStateChange data) {
        return s.serialize(topic, data.toString());
    }
}
