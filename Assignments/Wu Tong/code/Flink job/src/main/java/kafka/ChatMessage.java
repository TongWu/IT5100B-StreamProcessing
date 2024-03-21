package kafka;

import java.util.UUID;
import java.io.Serializable;
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID senderID;
    private String message;
    private UUID recipientID;

    public ChatMessage(UUID senderID, UUID recipientID, String message) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public void setSenderID(UUID senderID) { this.senderID = senderID; }

    public UUID getRecipientID() { return recipientID; }

    public void setRecipientID(UUID recipientID) { this.recipientID = recipientID; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
