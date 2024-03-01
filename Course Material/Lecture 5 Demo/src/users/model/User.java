package users.model;

/**
 * A user in our simple banking system. Objects of this class are immutable.
 */
public class User {
    public final int id;
    public final String name;
    public final double accountBalance;

    private User(int id) {
        this(id, null, 0);
    }

    private User(int id, String name, double accountBalance) {
        this.id = id;
        this.name = name;
        this.accountBalance = accountBalance;
    }

    /**
     * Creates a user with no name and an account balance of 0.
     * @param id The ID of the user.
     * @return The empty user.
     */
    public static User empty(int id) {
        return new User(id);
    }

    /**
     * Creates a new users.model.User where the name is replaced with a new name.
     * @param name The new name of the user.
     * @return The user with the new name.
     */
    public User ofName(String name) {
        return new User(id, name, accountBalance);
    }

    /**
     * Creates a new users.model.User where the account balance is replaced with a new account balance.
     * @param accountBalance The new account balance of the user.
     * @return The user with the new account balance.
     */
    public User ofAccountBalance(double accountBalance) {
        return new User(id, name, accountBalance);
    }

    public User ofId(int id) {
        return new User(id, name, accountBalance);
    }

    /**
     * Get the account balance of the user.
     * @return The account balance of the user.
     */
    public double getAccountBalance() {
        return accountBalance;
    }

    @Override
    public String toString() {
        return String.format("id: %d\n    name: %s\n    accountBalance: %f", id, name, accountBalance);
    }
    @Override
    public boolean equals(Object o) {
        User u;
        return (o instanceof User)
                && (u = (User) o).id == id
                && u.name.equals(name)
                && u.accountBalance == accountBalance;
    }
    @Override
    public int hashCode() {
        return (id + name + accountBalance).hashCode();
    }
}
