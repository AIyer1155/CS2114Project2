/**
 * Rule: All songs must have the same mood.
 */
public class MoodConsistency extends PlaylistRule {
    private String requiredMood;

    /**
     * Creates a MoodConsistency rule.
     *
     * @param requiredMood the mood all songs must have (must not be null)
     * @throws NullPointerException if requiredMood is null
     */
    public MoodConsistency(String requiredMood) {
        if (requiredMood == null) {
            throw new NullPointerException("Mood cannot be null");
        }
        this.requiredMood = requiredMood;
        this.ruleType = "MoodConsistency";
    }

    /**
     * Validates that the song's mood matches the required mood.
     */
    @Override
    public boolean validate(Song song) {
        if (song == null) {
            throw new NullPointerException("Song can't be null");
        }
        return song.getMood() != null && song.getMood().equals(requiredMood);
    }

    public String getRequiredMood() {
        return requiredMood;
    }

    @Override
    public String toString() {
        return "MoodConsistency(" + requiredMood + ")";
    }
}
