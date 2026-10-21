/**
 * The outcome of PlaylistGenerator.generate: either a playlist, or a
 * message and the rules that could not be satisfied.
 */
public class GenerationResult {
    private boolean success;
    private Playlist playlist;
    private String message;
    private String[] unsatisfiedRules;

    private GenerationResult(boolean success, Playlist playlist,
                             String message, String[] unsatisfiedRules) {
        this.success = success;
        this.playlist = playlist;
        this.message = message;
        this.unsatisfiedRules = unsatisfiedRules;
    }

    /**
     * Creates a successful result.
     *
     * @param playlist the generated playlist (must not be null)
     * @throws NullPointerException if playlist is null
     */
    public static GenerationResult success(Playlist playlist) {
        if (playlist == null) {
            throw new NullPointerException("Playlist cannot be null");
        }
        return new GenerationResult(true, playlist,
            "Generated " + playlist.getSongCount() + " songs", new String[0]);
    }

    /**
     * Creates a failed result.
     *
     * @param message why generation failed (must not be null)
     * @param unsatisfiedRules rule types that could not be satisfied
     *        (must not be null)
     * @throws NullPointerException if either argument is null
     */
    public static GenerationResult failure(String message,
                                           String[] unsatisfiedRules) {
        if (message == null) {
            throw new NullPointerException("Message cannot be null");
        }
        if (unsatisfiedRules == null) {
            throw new NullPointerException("Rules cannot be null");
        }
        return new GenerationResult(false, null, message,
            unsatisfiedRules.clone());
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the generated playlist, or null if generation failed.
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns a copy of the unsatisfied rule types (empty on success).
     */
    public String[] getUnsatisfiedRules() {
        return unsatisfiedRules.clone();
    }
}
