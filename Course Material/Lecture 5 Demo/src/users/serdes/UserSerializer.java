package users.serdes;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import users.model.User;

public class UserSerializer implements Serializer<User> {
    StringSerializer s = new StringSerializer();

    @Override
    public byte[] serialize(String topic, User data) {
        return s.serialize(topic, String.format("%d=%f=%s", data.id, data.accountBalance, data.name));
    }
}
