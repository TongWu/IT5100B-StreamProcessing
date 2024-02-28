package kafka.model;

import java.util.UUID;

/**
 * A state change in a {@link User}.
 */
public abstract class UserStateChange {
    protected UUID targetUserId;
    protected UserStateChange(UUID targetUserId) {
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
    public UUID getTargetUserId() {
        return targetUserId;
    }
}


