/**
 * Rule: No explicit songs are allowed.
 */
public class NoExplicit extends PlaylistRule {
    public NoExplicit() {
        this.ruleType = "NoExplicit";
    }

    /**
     * Validates that the song is not explicit.
     */
    @Override
    public boolean validate(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null, must have a value");
        }
        return !song.isExplicit();
    }
}
