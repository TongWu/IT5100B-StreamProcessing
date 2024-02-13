package streamexercises.utils;

/**
 * An update to a user's name.
 */
public class UserNameChange extends UserStateChange {
    private final String newName;

    /**
     * Creates a name user state change event.
     * @param targetUserId The target user's ID.
     * @param newName The new name of the user.
     */
    public UserNameChange(String targetUserId, String newName) {
        super(targetUserId);
        this.newName = newName;
    }

    /**
     * Changes the name of the target user.
     * @param user The user whose name is to be changed.
     * @return The user with the new name.
     */
    @Override
    public User changeUserState(User user) {
        return user.ofName(newName);
    }

    public boolean equals(Object o) {
        return (o instanceof UserNameChange) &&
                ((UserNameChange) o).newName.equals(this.newName);
    }

    public String toString() {
        return String.format("%s %s", super.targetUserId, this.newName);
    }
}
