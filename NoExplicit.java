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
        return !song.isExplicit();
    }
}
