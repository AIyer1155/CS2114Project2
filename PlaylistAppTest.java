import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for PlaylistApp, driven by scripted input. Each test feeds menu
 * choices and checks the printed output.
 */
public class PlaylistAppTest {

    /** Runs the app on the given lines and returns everything it printed. */
    private String run(String... lines) {
        String script = String.join("\n", lines) + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PlaylistApp app = new PlaylistApp(
            new ByteArrayInputStream(script.getBytes()),
            new PrintStream(bytes), new Random(1));
        app.run();
        return bytes.toString();
    }

    // ----- run / quit -----

    @Test
    public void testQuit() {
        assertTrue(run("0").contains("Goodbye."));
    }

    @Test
    public void testInputEndsWithoutQuit() {
        assertTrue(run().contains("Input ended."));
    }

    @Test
    public void testInvalidChoiceNumber() {
        String out = run("99", "0");
        assertTrue(out.contains("Invalid choice. Enter a number from 0 to 14."));
        assertTrue(out.contains("Goodbye."));
    }

    @Test
    public void testInvalidChoiceText() {
        String out = run("abc", "", "-1", "0");
        assertTrue(out.contains("Invalid choice"));
        assertTrue(out.contains("Goodbye."));
    }

    // ----- add song to library -----

    @Test
    public void testAddLibrarySong() {
        String out = run("1", "Sunrise", "Artist A", "120", "n", "chill", "3", "0");
        assertTrue(out.contains("Added to library: s1 | Sunrise - Artist A"));
        assertTrue(out.contains("Library (1 songs):"));
    }

    @Test
    public void testAddLibrarySongRepromptsBadBpm() {
        String out = run("1", "T", "A", "abc", "", "0", "-5", "100", "y",
            "party", "0");
        int count = out.split("BPM must be a positive integer", -1).length - 1;
        assertEquals(4, count);
        assertTrue(out.contains("Added to library: s1 | T - A | 100 BPM | party | explicit"));
    }

    @Test
    public void testAddLibrarySongRepromptsBadMood() {
        String out = run("1", "T", "A", "100", "n", "happy", "", "FOCUS", "0");
        assertTrue(out.contains(
            "Invalid mood. Valid options: [energetic, chill, focus, party]"));
        assertTrue(out.contains("| focus"));
    }

    @Test
    public void testAddLibrarySongRepromptsEmptyTitleAndExplicit() {
        String out = run("1", "", "T", "", "A", "100", "maybe", "y", "chill", "0");
        assertTrue(out.contains("Title cannot be empty"));
        assertTrue(out.contains("Artist cannot be empty"));
        assertTrue(out.contains("Enter y or n"));
        assertTrue(out.contains("Added to library"));
    }

    // ----- remove song from library -----

    @Test
    public void testRemoveLibrarySong() {
        String out = run("14", "2", "s1", "3", "0");
        assertTrue(out.contains("Removed from library: s1"));
        assertTrue(out.contains("Library (9 songs):"));
    }

    @Test
    public void testRemoveLibrarySongNotFound() {
        assertTrue(run("14", "2", "zzz", "0").contains("Song not found in library"));
    }

    @Test
    public void testRemoveLibrarySongEmptyLibrary() {
        assertTrue(run("2", "0").contains("Library is empty"));
    }

    // ----- show library -----

    @Test
    public void testShowLibraryEmpty() {
        assertTrue(run("3", "0").contains("Library is empty"));
    }

    @Test
    public void testLoadSamplesThenShow() {
        String out = run("14", "3", "0");
        assertTrue(out.contains("Loaded 10 sample songs."));
        assertTrue(out.contains("Library (10 songs):"));
    }

    @Test
    public void testLoadSamplesTwiceGivesUniqueIds() {
        assertTrue(run("14", "14", "3", "0").contains("Library (20 songs):"));
    }

    // ----- add to playlist -----

    @Test
    public void testAddToPlaylist() {
        String out = run("14", "4", "s1", "6", "0");
        assertTrue(out.contains("Added to playlist: s1"));
        assertTrue(out.contains("Playlist (1 songs):"));
    }

    @Test
    public void testAddToPlaylistDuplicate() {
        String out = run("14", "4", "s1", "4", "s1", "0");
        assertTrue(out.contains("Song with id s1 already in playlist"));
    }

    @Test
    public void testAddToPlaylistNotInLibrary() {
        assertTrue(run("14", "4", "zzz", "0").contains("Song not found in library"));
    }

    @Test
    public void testAddToPlaylistEmptyLibrary() {
        assertTrue(run("4", "0").contains("Library is empty"));
    }

    // ----- remove from playlist -----

    @Test
    public void testRemoveFromPlaylist() {
        String out = run("14", "4", "s1", "5", "s1", "6", "0");
        assertTrue(out.contains("Removed from playlist: s1"));
        assertTrue(out.contains("Playlist is empty"));
    }

    @Test
    public void testRemoveFromPlaylistNotFound() {
        String out = run("14", "4", "s1", "5", "s2", "0");
        assertTrue(out.contains("Song not found in playlist"));
    }

    @Test
    public void testRemoveFromPlaylistEmpty() {
        assertTrue(run("5", "0").contains("Playlist is empty"));
    }

    // ----- show playlist -----

    @Test
    public void testShowPlaylistEmpty() {
        assertTrue(run("6", "0").contains("Playlist is empty"));
    }

    // ----- add rule -----

    @Test
    public void testAddBpmRule() {
        String out = run("7", "1", "90", "150", "9", "0");
        assertTrue(out.contains("Added rule: BpmRange(90-150)"));
        assertTrue(out.contains("1. BpmRange(90-150)"));
    }

    @Test
    public void testAddOtherRules() {
        String out = run("7", "2", "7", "3", "chill", "7", "4", "9", "0");
        assertTrue(out.contains("Added rule: NoExplicit"));
        assertTrue(out.contains("Added rule: MoodConsistency(chill)"));
        assertTrue(out.contains("Added rule: NoRepeatArtist"));
    }

    @Test
    public void testAddRuleEmptyParameters() {
        String out = run("7", "1", "", "", "9", "0");
        assertTrue(out.contains("All rule parameters required before adding"));
        assertTrue(out.contains("No rules set"));
    }

    @Test
    public void testAddRuleNonNumericBpm() {
        String out = run("7", "1", "fast", "slow", "9", "0");
        assertTrue(out.contains("BPM values must be whole numbers"));
        assertTrue(out.contains("No rules set"));
    }

    @Test
    public void testAddRuleMinAboveMax() {
        String out = run("7", "1", "150", "90", "0");
        assertTrue(out.contains("minBpm cannot exceed maxBpm"));
    }

    @Test
    public void testAddRuleNegativeBpm() {
        assertTrue(run("7", "1", "-1", "90", "0")
            .contains("BPM values must be non-negative"));
    }

    @Test
    public void testAddRuleInvalidType() {
        String out = run("7", "9", "7", "x", "0");
        assertEquals(2, out.split("Invalid rule type", -1).length - 1);
    }

    @Test
    public void testAddDuplicateRuleType() {
        String out = run("7", "2", "7", "2", "0");
        assertTrue(out.contains(
            "Rule type NoExplicit already exists; remove it first"));
    }

    @Test
    public void testAddMoodRuleRepromptsBadMood() {
        String out = run("7", "3", "sad", "party", "9", "0");
        assertTrue(out.contains("Invalid mood"));
        assertTrue(out.contains("MoodConsistency(party)"));
    }

    // ----- remove rule -----

    @Test
    public void testRemoveRule() {
        String out = run("7", "2", "7", "4", "8", "1", "9", "0");
        assertTrue(out.contains("Removed rule: NoExplicit"));
        assertTrue(out.contains("1. NoRepeatArtist"));
    }

    @Test
    public void testRemoveRuleBadNumber() {
        String out = run("7", "2", "8", "5", "8", "x", "8", "0", "0");
        assertEquals(3, out.split("Rule not found", -1).length - 1);
    }

    @Test
    public void testRemoveRuleNoneSet() {
        assertTrue(run("8", "0").contains("No rules to remove"));
    }

    // ----- show rules -----

    @Test
    public void testShowRulesNone() {
        assertTrue(run("9", "0").contains("No rules set"));
    }

    // ----- validate -----

    @Test
    public void testValidateShowsPassAndFail() {
        String out = run("14", "4", "s1", "4", "s3", "7", "1", "90", "150",
            "7", "2", "10", "0");
        assertFalse(out.contains("PASS BpmRange(90-150)"));
        assertTrue(out.contains("FAIL BpmRange(90-150): s3 Sprint"));
        assertTrue(out.contains("FAIL NoExplicit: s3 Sprint"));
        assertTrue(out.contains("Playlist is not valid: 2 violation(s)"));
    }

    @Test
    public void testValidatePassing() {
        String out = run("14", "4", "s1", "7", "1", "90", "150", "10", "0");
        assertTrue(out.contains("PASS BpmRange(90-150)"));
        assertTrue(out.contains("Playlist is valid"));
    }

    @Test
    public void testValidateEmptyPlaylist() {
        assertTrue(run("10", "0").contains("Playlist is empty"));
    }

    @Test
    public void testValidateNoRules() {
        String out = run("14", "4", "s1", "10", "0");
        assertTrue(out.contains("No rules set; nothing to validate against"));
    }

    @Test
    public void testValidateSameArtistBackToBack() {
        String out = run("14", "4", "s1", "4", "s2", "7", "4", "10", "0");
        assertTrue(out.contains("FAIL NoRepeatArtist: s2 Uphill"));
    }

    // ----- next / previous -----

    @Test
    public void testNextAndPrevious() {
        String out = run("14", "4", "s1", "4", "s2", "11", "11", "12", "6", "0");
        assertTrue(out.contains("Now playing: s1"));
        assertTrue(out.contains("Now playing: s2"));
        assertTrue(out.contains("> 1. s1"));
    }

    @Test
    public void testNextAtEnd() {
        String out = run("14", "4", "s1", "11", "11", "0");
        assertTrue(out.contains("No more songs"));
    }

    @Test
    public void testPreviousAtStart() {
        String out = run("14", "4", "s1", "12", "11", "12", "0");
        assertEquals(2, out.split("No previous songs", -1).length - 1);
    }

    @Test
    public void testNextAndPreviousEmptyPlaylist() {
        String out = run("11", "12", "0");
        assertEquals(2, out.split("Playlist is empty", -1).length - 1);
    }

    // ----- generate -----

    @Test
    public void testGenerateReplacesPlaylist() {
        String out = run("14", "7", "2", "13", "3", "6", "0");
        assertTrue(out.contains("Generated playlist replaced the current one."));
        assertTrue(out.contains("Playlist (3 songs):"));
        assertFalse(out.contains("| explicit"));
    }

    @Test
    public void testGenerateRepromptsBadLength() {
        String out = run("14", "13", "abc", "0", "-2", "2", "0");
        assertEquals(3, out.split("Length must be a positive integer", -1).length - 1);
        assertTrue(out.contains("Playlist (2 songs):"));
    }

    @Test
    public void testGenerateEmptyLibrary() {
        assertTrue(run("13", "0").contains("Library is empty"));
    }

    @Test
    public void testGenerateTooLong() {
        String out = run("14", "13", "11", "0");
        assertTrue(out.contains("Could not generate:"));
        assertTrue(out.contains("need 11"));
    }

    @Test
    public void testGenerateReportsUnsatisfiedRules() {
        String out = run("14", "7", "3", "focus", "13", "5", "0");
        assertTrue(out.contains("Could not generate:"));
        assertTrue(out.contains("Unsatisfied rules: MoodConsistency"));
    }

    // ----- constructors -----

    @Test(expected = NullPointerException.class)
    public void testConstructorNullInput() {
        new PlaylistApp(null, System.out);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullOutput() {
        new PlaylistApp(new ByteArrayInputStream(new byte[0]), null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullRandom() {
        new PlaylistApp(new ByteArrayInputStream(new byte[0]), System.out, null);
    }
}
