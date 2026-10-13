/**
 * Rule: No two songs by the same artist can appear in sequence.
 */
public class NoRepeatArtist extends PlaylistRule {
    public NoRepeatArtist() {
        this.ruleType = "NoRepeatArtist";
    }

    /**
     * Always returns true. This rule depends on the previous song, so the
     * actual check is done in Playlist.validate().
     */
    @Override
    public boolean validate(Song song) {
        return true;
    }
}
