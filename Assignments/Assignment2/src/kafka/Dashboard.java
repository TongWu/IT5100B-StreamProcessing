package kafka;

import kafka.model.UserAccountBalanceIncrease;
import kafka.model.UserNameChange;
import kafka.model.UserStateChange;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Scanner;
import java.util.UUID;
import java.util.function.Function;

/**
 * Runs a very simple interactive Kafka Producer which produces events to Kafka.
 */
public class Dashboard {
    private static final Logger LOG = LoggerFactory.getLogger(Dashboard.class);

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Runs the dashboard.
     * @param args Not used.
     */
    public static void main(String[] args) {
        // Create the producer
        UserStateChangeEventProducer p = new UserStateChangeEventProducer(Level.WARN);
        // Endlessly read user commands from the console and publish events
        Flux.<Command>generate(sink -> {
                    sink.next(Command.build()); // build commands from user input
                })
                .takeWhile(Command::isNotExit) // stop once exit command is produced
                .map(Command::get) // get the state change event
                .flatMapSequential(p::send) // send to Kafka
                .blockLast(); // block until exit event
        p.close(); // close the producer
    }


    private static abstract class Command {
        private static final String EXIT_CMD_STRING = "exit";
        private static final String RENAME_CMD_STRING = "rename";
        private static final String BALANCE_CMD_STRING = "balance";

        private static Command build() {
            String command;
            while (true) {
                System.out.print("enter command [rename/balance/exit] > ");
                command = SCANNER.nextLine();
                if (isValidCommand(command))
                    break;
                LOG.error("Invalid command: {}", command);
            }
            Function<UUID, Command> builder;
            switch (command) {
                case EXIT_CMD_STRING:
                    return new ExitCommand();
                case RENAME_CMD_STRING:
                    builder = NameChangeCommand::build;
                    break;
                default:
                    builder = BalanceIncreaseCommand::build;
            }
            UUID id;
            while (true) {
                System.out.print("enter user ID (a long value) > ");
                String uid = SCANNER.nextLine();
                try {
                    id = new UUID(0L, Long.parseLong(uid));
                    break;
                } catch (Exception e) {
                    LOG.error("Invalid ID: {}", uid);
                }
            }
            return builder.apply(id);
        }

        private static boolean isValidCommand(String cmd) {
            return cmd.equals(EXIT_CMD_STRING) || cmd.equals(RENAME_CMD_STRING) || cmd.equals(BALANCE_CMD_STRING);
        }

        abstract UserStateChange get();

        abstract boolean isNotExit();

    }

    private static class NameChangeCommand extends Command {
        private final UserNameChange event;

        private NameChangeCommand(UserNameChange event) {
            this.event = event;
        }

        private static NameChangeCommand build(UUID id) {
            System.out.print("enter new name > ");
            String newName = SCANNER.nextLine();
            return new NameChangeCommand(new UserNameChange(id, newName));
        }

        UserNameChange get() {
            return event;
        }

        boolean isNotExit() {
            return true;
        }
    }

    private static class ExitCommand extends Command {
        @Override
        public boolean equals(Object obj) {
            return obj.getClass().equals(this.getClass());
        }

        UserStateChange get() {
            return null;
        }

        boolean isNotExit() {
            return false;
        }
    }

    private static class BalanceIncreaseCommand extends Command {
        private final UserAccountBalanceIncrease event;

        private BalanceIncreaseCommand(UserAccountBalanceIncrease event) {
            this.event = event;
        }

        private static BalanceIncreaseCommand build(UUID id) {
            long amount;
            while (true) {
                System.out.print("enter increase in balance (a long value) > ");
                String input = SCANNER.nextLine();
                try {
                    amount = Long.parseLong(input);
                    break;
                } catch (Exception e) {
                    LOG.error("Invalid balance increase: {}", input);
                }
            }
            return new BalanceIncreaseCommand(new UserAccountBalanceIncrease(id, amount));
        }

        UserAccountBalanceIncrease get() {
            return event;
        }

        boolean isNotExit() {
            return true;
        }
    }

}
