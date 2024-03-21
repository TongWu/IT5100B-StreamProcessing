package serdes;

import kafka.ChatMessage;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;


public class ChatMessageDeserializationSchema implements DeserializationSchema<ChatMessage> {

    @Override
    public ChatMessage deserialize(byte[] message) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (ChatMessage) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Deserialization error", e);
        }
    }

    @Override
    public boolean isEndOfStream(ChatMessage nextElement) {
        return false;
    }

    @Override
    public TypeInformation<ChatMessage> getProducedType() {
        return TypeExtractor.getForClass(ChatMessage.class);
    }
}
