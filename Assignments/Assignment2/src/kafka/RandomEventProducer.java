package kafka;

import kafka.model.UserAccountBalanceIncrease;
import kafka.model.UserNameChange;
import kafka.model.UserStateChange;
import org.apache.logging.log4j.Level;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * Produces random user state change events into Kafka.
 */
public class RandomEventProducer {
    private static final UUID[] randomIDs = Arrays.stream(new String[]{"6f1bb813-f82d-3f58-69d7-6726be6f4f20", "d0c92064-b359-e00f-a4fd-23c3206d2af3", "00153f72-6ffd-c072-f352-442508e218af", "fb5bd603-916b-0615-331a-324baed2c455", "14dce22a-a84b-d7cf-5db2-4d5c14a0feff"})
            .map(UUID::fromString)
            .toArray(UUID[]::new);
    private static final String[] randomNames = new String[]{"Wade", "Dave", "Seth", "Ivan", "Riley", "Gilbert", "Jorge", "Dan", "Brian", "Roberto", "Ramon", "Miles", "Liam", "Nathaniel", "Ethan", "Lewis", "Milton", "Claude", "Joshua", "Glen", "Harvey", "Blake", "Antonio", "Connor", "Julian", "Aidan", "Harold", "Conner", "Peter", "Hunter", "Eli", "Alberto", "Carlos", "Shane", "Aaron", "Marlin", "Paul", "Ricardo", "Hector", "Alexis", "Adrian", "Kingston", "Douglas", "Gerald", "Joey", "Johnny", "Charlie", "Scott", "Martin", "Tristin", "Troy", "Tommy", "Rick", "Victor", "Jessie", "Neil", "Ted", "Nick", "Wiley", "Morris", "Clark", "Stuart", "Orlando", "Keith", "Marion", "Marshall", "Noel", "Everett", "Romeo", "Sebastian", "Stefan", "Robin", "Clarence", "Sandy", "Ernest", "Samuel", "Benjamin", "Luka", "Fred", "Albert", "Greyson", "Terry", "Cedric", "Joe", "Paul", "George", "Bruce", "Christopher", "Mark", "Ron", "Craig", "Philip", "Jimmy", "Arthur", "Jaime", "Perry", "Harold", "Jerry", "Shawn", "Walter"};
    private static final Random RNG = new Random();

    // TODO: Change the delay to whatever value you want.
    private static final Duration DELAY = Duration.ofMillis(1000);

    /**
     * Produces random user state changes to Kafka.
     * @param args Not used.
     */
    public static void main(String[] args) {
        // Create the producer
        UserStateChangeEventProducer p = new UserStateChangeEventProducer(Level.INFO);
        Flux.<UserStateChange>generate(sink -> { // endlessly create events
                    UUID randomUserId = randomIDs[RNG.nextInt(randomIDs.length)]; // get a random user ID
                    if (RNG.nextDouble() > 0.3)
                        sink.next(new UserAccountBalanceIncrease(randomUserId, RNG.nextInt(20001) - 10000)); // increase by random amount
                    else
                        sink.next(new UserNameChange(randomUserId, randomNames[RNG.nextInt(randomNames.length)])); // change to random name
                })
                .delayElements(DELAY) // delay events by some interval
                .flatMap(p::send) // send to Kafka
                .blockLast(); // keep sending forever
    }
}
