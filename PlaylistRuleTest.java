import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for PlaylistRule and its four subclasses: BpmRange, NoExplicit,
 * MoodConsistency, and NoRepeatArtist.
 * One normal case and one bad-input case per public method.
 */
public class PlaylistRuleTest {
    private Song clean;
    private Song explicit;
    private Song noMood;

    @Before
    public void setUp() {
        clean = new Song("s1", "Clean", "Artist A", 120, false, "happy");
        explicit = new Song("s2", "Explicit", "Artist B", 100, true, "sad");
        noMood = new Song("s3", "No Mood", "Artist C", 120, false, null);
    }

    // ----- BpmRange -----

    @Test
    public void testBpmRangeConstructor() {
        BpmRange rule = new BpmRange(100, 150);
        assertEquals(100, rule.getMinBpm());
        assertEquals(150, rule.getMaxBpm());
        assertEquals("BpmRange", rule.getType());
    }

    @Test
    public void testBpmRangeSingleValue() {
        BpmRange rule = new BpmRange(120, 120);
        assertEquals(rule.getMinBpm(), rule.getMaxBpm());
        assertTrue(rule.validate(clean));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBpmRangeMinExceedsMax() {
        new BpmRange(150, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBpmRangeNegativeMin() {
        new BpmRange(-10, 150);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBpmRangeNegativeMax() {
        new BpmRange(100, -10);
    }

    @Test
    public void testBpmRangeValidate() {
        BpmRange rule = new BpmRange(100, 150);
        assertTrue(rule.validate(clean));     // 120, inside
        assertTrue(rule.validate(explicit));  // 100, on the boundary
        assertFalse(rule.validate(new Song("x", "T", "A", 99, false, "h")));
        assertFalse(rule.validate(new Song("y", "T", "A", 151, false, "h")));
    }

    @Test(expected = NullPointerException.class)
    public void testBpmRangeValidateNull() {
        new BpmRange(100, 150).validate(null);
    }

    // ----- NoExplicit -----

    @Test
    public void testNoExplicitType() {
        assertEquals("NoExplicit", new NoExplicit().getType());
    }

    @Test
    public void testNoExplicitValidate() {
        NoExplicit rule = new NoExplicit();
        assertTrue(rule.validate(clean));
        assertFalse(rule.validate(explicit));
    }

    @Test(expected = NullPointerException.class)
    public void testNoExplicitValidateNull() {
        new NoExplicit().validate(null);
    }

    // ----- MoodConsistency -----

    @Test
    public void testMoodConsistencyConstructor() {
        MoodConsistency rule = new MoodConsistency("happy");
        assertEquals("happy", rule.getRequiredMood());
        assertEquals("MoodConsistency", rule.getType());
    }

    @Test(expected = NullPointerException.class)
    public void testMoodConsistencyNullMood() {
        new MoodConsistency(null);
    }

    @Test
    public void testMoodConsistencyValidate() {
        MoodConsistency rule = new MoodConsistency("happy");
        assertTrue(rule.validate(clean));
        assertFalse(rule.validate(explicit));
    }

    @Test
    public void testMoodConsistencyValidateSongWithNoMood() {
        // a song with no mood tag fails instead of crashing
        assertFalse(new MoodConsistency("happy").validate(noMood));
    }

    @Test(expected = NullPointerException.class)
    public void testMoodConsistencyValidateNull() {
        new MoodConsistency("happy").validate(null);
    }

    // ----- NoRepeatArtist -----

    @Test
    public void testNoRepeatArtistType() {
        assertEquals("NoRepeatArtist", new NoRepeatArtist().getType());
    }

    @Test
    public void testNoRepeatArtistValidateSingleSong() {
        // a song on its own can't repeat an artist; the adjacency
        // check lives in Playlist.validate() (see PlaylistTest)
        assertTrue(new NoRepeatArtist().validate(clean));
    }

    @Test(expected = NullPointerException.class)
    public void testNoRepeatArtistValidateNull() {
        new NoRepeatArtist().validate(null);
    }
}
