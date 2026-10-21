import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for PlaylistGenerator and GenerationResult.
 */
public class PlaylistGeneratorTest {
    private SongLibrary library;
    private PlaylistGenerator generator;

    @Before
    public void setUp() {
        library = new SongLibrary();
        library.addSong(new Song("s1", "A1", "Artist A", 120, false, "chill"));
        library.addSong(new Song("s2", "A2", "Artist A", 130, false, "chill"));
        library.addSong(new Song("s3", "B1", "Artist B", 100, false, "chill"));
        library.addSong(new Song("s4", "C1", "Artist C", 170, true, "party"));
        library.addSong(new Song("s5", "D1", "Artist D", 90, true, "chill"));
        generator = new PlaylistGenerator(new Random(42));
    }

    // ----- generate: normal -----

    @Test
    public void testGenerateNoRules() {
        GenerationResult result =
            generator.generate(library, new PlaylistRule[0], 3);
        assertTrue(result.isSuccess());
        assertEquals(3, result.getPlaylist().getSongCount());
        assertEquals(0, result.getUnsatisfiedRules().length);
    }

    @Test
    public void testGenerateSatisfiesAllRules() {
        PlaylistRule[] rules = {
            new BpmRange(90, 130), new NoExplicit(),
            new MoodConsistency("chill"), new NoRepeatArtist()};
        GenerationResult result = generator.generate(library, rules, 3);
        assertTrue(result.isSuccess());
        Playlist playlist = result.getPlaylist();
        assertEquals(3, playlist.getSongCount());
        assertTrue(playlist.validate().isValid());
    }

    @Test
    public void testGenerateNoRepeatArtistAcrossManySeeds() {
        PlaylistRule[] rules = {new NoRepeatArtist()};
        for (int seed = 0; seed < 50; seed++) {
            GenerationResult result = new PlaylistGenerator(new Random(seed))
                .generate(library, rules, 4);
            assertTrue(result.isSuccess());
            assertTrue(result.getPlaylist().validate().isValid());
        }
    }

    @Test
    public void testGenerateSameSeedSamePlaylist() {
        PlaylistRule[] rules = {new NoExplicit()};
        Song[] first = new PlaylistGenerator(new Random(7))
            .generate(library, rules, 3).getPlaylist().getSongs();
        Song[] second = new PlaylistGenerator(new Random(7))
            .generate(library, rules, 3).getPlaylist().getSongs();
        assertArrayEquals(first, second);
    }

    @Test
    public void testGenerateUsesNoSongTwice() {
        Song[] songs = generator.generate(library, new PlaylistRule[0], 5)
            .getPlaylist().getSongs();
        for (int i = 0; i < songs.length; i++) {
            for (int j = i + 1; j < songs.length; j++) {
                assertNotEquals(songs[i], songs[j]);
            }
        }
    }

    @Test
    public void testGenerateExactlyEligibleCount() {
        GenerationResult result = generator.generate(
            library, new PlaylistRule[] {new NoExplicit()}, 3);
        assertTrue(result.isSuccess());
    }

    // ----- generate: bad input and unsatisfiable -----

    @Test
    public void testGenerateLengthLargerThanLibrary() {
        GenerationResult result =
            generator.generate(library, new PlaylistRule[0], 6);
        assertFalse(result.isSuccess());
        assertNull(result.getPlaylist());
        assertTrue(result.getMessage().contains("need 6"));
    }

    @Test
    public void testGenerateEmptyLibrary() {
        GenerationResult result = generator.generate(
            new SongLibrary(), new PlaylistRule[0], 1);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testGenerateReportsExcludingRules() {
        PlaylistRule[] rules = {new BpmRange(90, 130), new NoExplicit()};
        GenerationResult result = generator.generate(library, rules, 4);
        assertFalse(result.isSuccess());
        String[] unmet = result.getUnsatisfiedRules();
        assertEquals(2, unmet.length);
        assertEquals("BpmRange", unmet[0]);
        assertEquals("NoExplicit", unmet[1]);
    }

    @Test
    public void testGenerateMoodThatNoSongHas() {
        GenerationResult result = generator.generate(library,
            new PlaylistRule[] {new MoodConsistency("focus")}, 1);
        assertFalse(result.isSuccess());
        assertEquals("MoodConsistency", result.getUnsatisfiedRules()[0]);
    }

    @Test
    public void testGenerateNoRepeatArtistImpossible() {
        SongLibrary sameArtist = new SongLibrary();
        sameArtist.addSong(new Song("x1", "T1", "Solo", 100, false, "chill"));
        sameArtist.addSong(new Song("x2", "T2", "Solo", 100, false, "chill"));
        GenerationResult result = generator.generate(sameArtist,
            new PlaylistRule[] {new NoRepeatArtist()}, 2);
        assertFalse(result.isSuccess());
        assertEquals("NoRepeatArtist", result.getUnsatisfiedRules()[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateZeroLength() {
        generator.generate(library, new PlaylistRule[0], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateNegativeLength() {
        generator.generate(library, new PlaylistRule[0], -3);
    }

    @Test(expected = NullPointerException.class)
    public void testGenerateNullLibrary() {
        generator.generate(null, new PlaylistRule[0], 1);
    }

    @Test(expected = NullPointerException.class)
    public void testGenerateNullRules() {
        generator.generate(library, null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testGenerateNullRuleElement() {
        generator.generate(library, new PlaylistRule[] {null}, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateDuplicateRuleType() {
        generator.generate(library,
            new PlaylistRule[] {new BpmRange(1, 2), new BpmRange(3, 4)}, 1);
    }

    // ----- constructors -----

    @Test
    public void testDefaultConstructorWorks() {
        GenerationResult result = new PlaylistGenerator()
            .generate(library, new PlaylistRule[0], 2);
        assertTrue(result.isSuccess());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullRandom() {
        new PlaylistGenerator(null);
    }

    // ----- GenerationResult -----

    @Test
    public void testResultSuccessFactory() {
        Playlist playlist = new Playlist("p", "n");
        GenerationResult result = GenerationResult.success(playlist);
        assertTrue(result.isSuccess());
        assertSame(playlist, result.getPlaylist());
        assertNotNull(result.getMessage());
        assertEquals(0, result.getUnsatisfiedRules().length);
    }

    @Test(expected = NullPointerException.class)
    public void testResultSuccessNull() {
        GenerationResult.success(null);
    }

    @Test
    public void testResultFailureFactory() {
        String[] rules = {"BpmRange"};
        GenerationResult result = GenerationResult.failure("nope", rules);
        assertFalse(result.isSuccess());
        assertNull(result.getPlaylist());
        assertEquals("nope", result.getMessage());
        rules[0] = "changed";
        assertEquals("BpmRange", result.getUnsatisfiedRules()[0]);
    }

    @Test(expected = NullPointerException.class)
    public void testResultFailureNullMessage() {
        GenerationResult.failure(null, new String[0]);
    }

    @Test(expected = NullPointerException.class)
    public void testResultFailureNullRules() {
        GenerationResult.failure("msg", null);
    }

    @Test
    public void testResultGetUnsatisfiedRulesReturnsCopy() {
        GenerationResult result =
            GenerationResult.failure("m", new String[] {"A"});
        result.getUnsatisfiedRules()[0] = "B";
        assertEquals("A", result.getUnsatisfiedRules()[0]);
    }
}
