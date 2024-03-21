package serdes;

import kafka.ChatMessage;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ChatMessageSerializationSchema implements SerializationSchema<ChatMessage> {
    @Override
    public byte[] serialize(ChatMessage chatMessage) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(chatMessage);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Serialization error for object: " + chatMessage, e);
        }
    }
}
