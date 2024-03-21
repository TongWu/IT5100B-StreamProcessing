package kafka.serdes;

import kafka.ChatMessage;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class ChatMessageDeserializer implements Deserializer<ChatMessage> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public ChatMessage deserialize(String topic, byte[] data) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (ChatMessage) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error in deserializing object", e);
        }
    }

    @Override
    public void close() {
    }
}


