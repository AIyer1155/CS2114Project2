/**
 * Rule: All songs must fall within a specified BPM range.
 */
public class BpmRange extends PlaylistRule {
    private int minBpm;
    private int maxBpm;

    /**
     * Creates a BpmRange rule.
     *
     * @param minBpm minimum BPM (must be non-negative)
     * @param maxBpm maximum BPM (must be non-negative and >= minBpm)
     * @throws IllegalArgumentException if minBpm > maxBpm or either is negative
     */
    public BpmRange(int minBpm, int maxBpm) {
        if (minBpm < 0 || maxBpm < 0) {
            throw new IllegalArgumentException("BPM values must be non-negative");
        }
        if (minBpm > maxBpm) {
            throw new IllegalArgumentException("minBpm cannot exceed maxBpm");
        }
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
        this.ruleType = "BpmRange";
    }

    /**
     * Validates that the song's BPM is within the specified range.
     */
    @Override
    public boolean validate(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null");
        }
        return song.getBpm() >= minBpm && song.getBpm() <= maxBpm;
    }

    public int getMinBpm() {
        return minBpm;
    }

    public int getMaxBpm() {
        return maxBpm;
    }

    @Override
    public String toString() {
        return "BpmRange(" + minBpm + "-" + maxBpm + ")";
    }
}
