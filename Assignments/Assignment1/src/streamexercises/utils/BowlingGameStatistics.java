package streamexercises.utils;

/**
 * This class keeps track of the current statistics of a bowling game. Objects of this class are immutable.
 */
public class BowlingGameStatistics {
    private final char current;
    private final int totalScore;

    /**
     * Creates a new bowling game tracker whose total score is 0.
     */
    public BowlingGameStatistics() {
        this.current = 0;
        this.totalScore = 0;
    }
    private BowlingGameStatistics(char current, int totalScore) {
        this.current = current;
        this.totalScore = totalScore;
    }

    /**
     * Adds a new roll to the statistics.
     * @param c The character representing the new roll.
     * @return The updated statistics including that new roll.
     */
    public BowlingGameStatistics put(char c) {
        int newTotalScore = this.totalScore;
        if (c == 'X') {
            newTotalScore += 30;
        } else if (c == '/') {
            newTotalScore += 20 - this.current + '0';
        } else {
            newTotalScore += c - '0';
        }
        return new BowlingGameStatistics(c, newTotalScore);
    }

    /**
     * Gets the total score as kept track by this object.
     * @return The total score.
     */
    public int get() {
        return this.totalScore;
    }
}