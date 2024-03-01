package users.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import users.model.User;

public class UserDeserializer implements Deserializer<User> {
    StringDeserializer s = new StringDeserializer();
    @Override
    public User deserialize(String topic, byte[] data) {
        String r = s.deserialize(topic, data);
        String[] arr = r.split("=");
        return User.empty(Integer.parseInt(arr[0])).ofAccountBalance(Double.parseDouble(arr[1])).ofName(arr[2]);
    }
}
