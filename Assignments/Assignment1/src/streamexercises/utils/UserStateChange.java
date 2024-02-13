package streamexercises.utils;

/**
 * A state change in a {@link User}.
 */
public abstract class UserStateChange {
    protected String targetUserId;
    protected UserStateChange(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    /**
     * Changes the state of the target {@link User}.
     * @param u The user whose state is to be changed.
     * @return The resulting user with the new state.
     */
    public abstract User changeUserState(User u);

    /**
     * Get the target user's ID.
     * @return The target user's ID.
     */
    public String getTargetUserId() {
        return targetUserId;
    }
}

