package kafka;

import kafka.model.ImmutableMap;
import kafka.model.User;
import kafka.model.UserStateChange;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import kafka.serdes.UserStateChangeDeserializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes state changes from Kafka and shows the updated data base on each state change event.
 */
public class UserStateChangeEventConsumer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    private static final String TOPIC = "users";
    private final ReceiverOptions<UUID, UserStateChange> receiverOptions;

    /**
     * Creates a new Kafka consumer.
     */
    public UserStateChangeEventConsumer() {
        // Create properties.
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "userstatechange-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserStateChangeDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
    }

    /**
     * Returns a publisher that emits new events from Kafka.
     * @return A publisher that emits new events from Kafka.
     */
    public Flux<UserStateChange> consume() {
        // Subscribe to users topic
        ReceiverOptions<UUID, UserStateChange> options = receiverOptions.subscription(Collections.singleton(TOPIC));
        // Create the publisher
        Flux<ReceiverRecord<UUID, UserStateChange>> kafkaFlux = KafkaReceiver.create(options).receive();
        // Map each ConsumerRecord to just the UserStateChange
        return kafkaFlux.map(ConsumerRecord::value);
    }

    /**
     * Consumes user state change events from Kafka, and on each event, print the most updated version of the datastore
     * to the console.
     * @param args Not used.
     */
    public static void main(String[] args) {
        // Create the consumer
        UserStateChangeEventConsumer consumer = new UserStateChangeEventConsumer();
        // Get the events from Kafka
        Flux<UserStateChange> events = consumer.consume();
        // TODO: Do something with the events so that when running the main method, every time a state change is streamed from Kafka, it will print out the latest state of the data store.
        // Write your solution after this line.

        ImmutableMap<UUID, User> InitialState = ImmutableMap.empty();
        events.scan(InitialState, (cur, e) -> {
            return changeStateOfUserInMap(cur, e);
        }).doOnNext(update -> {
            System.out.println(update);
        }).blockLast();
    }
    /**
     * Performs changes on a map of users using a {@link UserStateChange} event. If the target of the state change is not in the map, and empty {@link User} of the same ID will be created.
     * @param map The map of users.
     * @param u The state change event.
     * @return The resulting map with the updated user.
     */
    public static ImmutableMap<UUID, User> changeStateOfUserInMap(ImmutableMap<UUID, User> map, UserStateChange u) {
        // TODO: Adapt your solution from Assignment 1 into this one.
        UUID targetUserId = u.getTargetUserId();
        User current = map.getOrDefault(targetUserId, User.empty(targetUserId));
        User newUser = u.changeUserState(current);
        return map.put(targetUserId, newUser);
        // return null;
    }
}