package kafka.serdes;

import kafka.model.UserAccountBalanceIncrease;
import kafka.model.UserNameChange;
import kafka.model.UserStateChange;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.UUID;

public class UserStateChangeDeserializer implements Deserializer<UserStateChange> {
    public final StringDeserializer s = new StringDeserializer();
    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic topic associated with the data
     * @param data  serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public UserStateChange deserialize(String topic, byte[] data) {
        String res =  s.deserialize(topic, data);
        char type = res.charAt(0);
        UUID id = UUID.fromString(res.substring(1, 37));
        if (type == 'b') {
            long increase = Long.parseLong(res.substring(37));
            return new UserAccountBalanceIncrease(id, increase);
        }
        String newName = res.substring(37);
        return new UserNameChange(id, newName);
    }
}
