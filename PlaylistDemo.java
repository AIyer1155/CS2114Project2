/**
 * Runs the MVP path: build a playlist, add songs and rules, validate,
 * edit, and validate again. Also feeds in the bad-input cases from the
 * scope to show that each one is caught instead of crashing the program.
 *
 * Run with: java PlaylistDemo
 */
public class PlaylistDemo {

    public static void main(String[] args) {
        Playlist playlist = new Playlist("p1", "Morning Run");

        Song s1 = new Song("s1", "Sunrise", "Artist A", 120, false, "happy");
        Song s2 = new Song("s2", "Uphill", "Artist A", 135, false, "happy");
        Song s3 = new Song("s3", "Sprint", "Artist B", 170, true, "energetic");
        Song s4 = new Song("s4", "Cooldown", "Artist C", 95, false, "happy");

        System.out.println("== Adding songs ==");
        playlist.addSong(s1);
        playlist.addSong(s2);
        playlist.addSong(s3);
        playlist.addSong(s4);
        System.out.println(playlist);

        System.out.println("\n== Adding rules ==");
        playlist.addRule(new BpmRange(90, 150));
        playlist.addRule(new NoExplicit());
        playlist.addRule(new MoodConsistency("happy"));
        playlist.addRule(new NoRepeatArtist());
        System.out.println(playlist);

        System.out.println("\n== Validating ==");
        printResult(playlist.validate());

        System.out.println("\n== Removing s2 and s3, then validating again ==");
        playlist.removeSong(s2);
        playlist.removeSong(s3);
        System.out.println(playlist);
        printResult(playlist.validate());

        System.out.println("\n== Bad input (each should be rejected, not crash) ==");
        tryBadInput("add null song", () -> playlist.addSong(null));
        tryBadInput("add duplicate song id", () -> playlist.addSong(
            new Song("s1", "Other", "Artist Z", 100, false, "happy")));
        tryBadInput("remove null song", () -> playlist.removeSong(null));
        tryBadInput("add null rule", () -> playlist.addRule(null));
        tryBadInput("add second BpmRange", () ->
            playlist.addRule(new BpmRange(80, 140)));
        tryBadInput("remove null rule", () -> playlist.removeRule(null));
        tryBadInput("song with null title", () ->
            new Song("x", null, "Artist", 100, false, "happy"));
        tryBadInput("song with zero BPM", () ->
            new Song("x", "Title", "Artist", 0, false, "happy"));
        tryBadInput("BpmRange with min > max", () -> new BpmRange(150, 100));
        tryBadInput("BpmRange with negative bound", () ->
            new BpmRange(-1, 100));
        tryBadInput("MoodConsistency with null mood", () ->
            new MoodConsistency(null));
        tryBadInput("playlist with null name", () -> new Playlist("p2", null));

        System.out.println("\nRemove a song that isn't there -> "
            + playlist.removeSong(s3));

        System.out.println("\nPlaylist still intact: " + playlist);
    }

    /**
     * Prints whether a validation passed and lists each violation.
     */
    private static void printResult(ValidationResult result) {
        System.out.println(result);
        for (ValidationResult.Violation v : result.getViolations()) {
            System.out.println("  " + v);
        }
    }

    /**
     * Runs an action that should be rejected and reports how it was caught.
     */
    private static void tryBadInput(String label, Runnable action) {
        try {
            action.run();
            System.out.println("  [NOT CAUGHT] " + label);
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("  [caught] " + label + " -> "
                + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
