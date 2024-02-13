package streamexercises.utils;

/**
 * An increase in a user's account balance. A negative increase indicates a decrease.
 */
public class UserAccountBalanceIncrease extends UserStateChange {
    private final long accountBalanceIncrease;

    /**
     * Creates a new user account balance increase state change event.
     * @param targetUserId The target user's ID.
     * @param accountBalanceIncrease The amount to increase the user's account balance by.
     */
    public UserAccountBalanceIncrease(String targetUserId, long accountBalanceIncrease) {
        super(targetUserId);
        this.accountBalanceIncrease = accountBalanceIncrease;
    }

    /**
     * Increases the target user's account balance.
     * @param user The user whose account balance is to be increased.
     * @return The user with the increased account balance.
     */
    @Override
    public User changeUserState(User user) {
        return user.ofAccountBalance(user.getAccountBalance() + accountBalanceIncrease);
    }

    public boolean equals(Object o) {
        return (o instanceof UserAccountBalanceIncrease) &&
                ((UserAccountBalanceIncrease) o).accountBalanceIncrease == this.accountBalanceIncrease;
    }

    @Override
    public String toString() {
        return String.format("%s +%d", super.targetUserId, accountBalanceIncrease);
    }
}