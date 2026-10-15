import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Playlist.
 * One normal case and one bad-input case per public method, plus
 * empty-collection and remove-something-not-there cases.
 */
public class PlaylistTest {
    private Playlist playlist;
    private Song song1;
    private Song song2;
    private Song song3;

    @Before
    public void setUp() {
        playlist = new Playlist("p1", "Test Playlist");
        song1 = new Song("s1", "Song One", "Artist A", 120, false, "happy");
        song2 = new Song("s2", "Song Two", "Artist B", 140, false, "energetic");
        song3 = new Song("s3", "Song Three", "Artist A", 100, true, "melancholy");
    }

    // ----- constructor -----

    @Test
    public void testConstructorNormal() {
        assertEquals("p1", playlist.getId());
        assertEquals("Test Playlist", playlist.getName());
        assertEquals(0, playlist.getSongCount());
        assertEquals(0, playlist.getRuleCount());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullId() {
        new Playlist(null, "Name");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullName() {
        new Playlist("id", null);
    }

    // ----- addSong -----

    @Test
    public void testAddSong() {
        playlist.addSong(song1);
        assertEquals(1, playlist.getSongCount());
        assertTrue(playlist.containsSong(song1));
    }

    @Test
    public void testAddMultipleSongs() {
        playlist.addSong(song1);
        playlist.addSong(song2);
        playlist.addSong(song3);
        assertEquals(3, playlist.getSongCount());
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullSong() {
        playlist.addSong(null);
    }

    @Test
    public void testAddDuplicateSong() {
        playlist.addSong(song1);
        Song dup = new Song("s1", "Different", "Different", 90, false, "happy");
        assertThrows(IllegalArgumentException.class,
            () -> playlist.addSong(dup));
        assertEquals(1, playlist.getSongCount());
    }

    // ----- removeSong -----

    @Test
    public void testRemoveSong() {
        playlist.addSong(song1);
        playlist.addSong(song2);
        assertTrue(playlist.removeSong(song1));
        assertEquals(1, playlist.getSongCount());
        assertFalse(playlist.containsSong(song1));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullSong() {
        playlist.removeSong(null);
    }

    @Test
    public void testRemoveSongNotInPlaylist() {
        playlist.addSong(song1);
        assertFalse(playlist.removeSong(song2));
        assertEquals(1, playlist.getSongCount());
    }

    @Test
    public void testRemoveSongFromEmptyPlaylist() {
        assertFalse(playlist.removeSong(song1));
        assertEquals(0, playlist.getSongCount());
    }

    // ----- getSongCount -----

    @Test
    public void testGetSongCountNormal() {
        playlist.addSong(song1);
        playlist.addSong(song2);
        assertEquals(2, playlist.getSongCount());
    }

    @Test
    public void testGetSongCountAfterRejectedAdd() {
        playlist.addSong(song1);
        try {
            playlist.addSong(null);
        }
        catch (NullPointerException e) {
            // expected
        }
        assertEquals(1, playlist.getSongCount());
    }

    // ----- containsSong -----

    @Test
    public void testContainsSongById() {
        Song sameId = new Song("s1", "Other", "Other", 90, true, "sad");
        playlist.addSong(song1);
        assertTrue(playlist.containsSong(sameId));
        assertFalse(playlist.containsSong(song2));
    }

    @Test
    public void testContainsSongNull() {
        playlist.addSong(song1);
        assertFalse(playlist.containsSong(null));
    }

    // ----- addRule -----

    @Test
    public void testAddRule() {
        playlist.addRule(new BpmRange(100, 150));
        playlist.addRule(new NoExplicit());
        assertEquals(2, playlist.getRuleCount());
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullRule() {
        playlist.addRule(null);
    }

    @Test
    public void testAddDuplicateRuleType() {
        playlist.addRule(new BpmRange(100, 150));
        assertThrows(IllegalArgumentException.class,
            () -> playlist.addRule(new BpmRange(80, 140)));
        assertEquals(1, playlist.getRuleCount());
    }

    // ----- removeRule -----

    @Test
    public void testRemoveRule() {
        NoExplicit rule = new NoExplicit();
        playlist.addRule(rule);
        assertTrue(playlist.removeRule(rule));
        assertEquals(0, playlist.getRuleCount());
        playlist.addRule(new NoExplicit()); // type can be added again
        assertEquals(1, playlist.getRuleCount());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullRule() {
        playlist.removeRule(null);
    }

    @Test
    public void testRemoveRuleNotInPlaylist() {
        playlist.addRule(new BpmRange(100, 150));
        assertFalse(playlist.removeRule(new BpmRange(80, 140)));
        assertEquals(1, playlist.getRuleCount());
    }

    @Test
    public void testRemoveRuleFromEmptyPlaylist() {
        assertFalse(playlist.removeRule(new NoExplicit()));
    }

    // ----- getRuleCount -----

    @Test
    public void testGetRuleCountNormal() {
        playlist.addRule(new NoExplicit());
        playlist.addRule(new NoRepeatArtist());
        playlist.addRule(new MoodConsistency("happy"));
        assertEquals(3, playlist.getRuleCount());
    }

    @Test
    public void testGetRuleCountAfterRejectedAdd() {
        playlist.addRule(new NoExplicit());
        try {
            playlist.addRule(new NoExplicit());
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(1, playlist.getRuleCount());
    }

    // ----- validate -----

    @Test
    public void testValidateAllPass() {
        playlist.addRule(new BpmRange(100, 150));
        playlist.addSong(song1);
        playlist.addSong(song2);
        ValidationResult result = playlist.validate();
        assertTrue(result.isValid());
        assertEquals(0, result.getViolationCount());
    }

    @Test
    public void testValidateEmptyPlaylist() {
        playlist.addRule(new NoExplicit());
        ValidationResult result = playlist.validate();
        assertTrue(result.isValid());
        assertEquals(0, result.getViolationCount());
    }

    @Test
    public void testValidateNoRules() {
        playlist.addSong(song1);
        playlist.addSong(song3);
        assertTrue(playlist.validate().isValid());
    }

    @Test
    public void testValidateWithViolation() {
        playlist.addRule(new NoExplicit());
        playlist.addSong(song1);
        playlist.addSong(song3); // explicit
        ValidationResult result = playlist.validate();
        assertFalse(result.isValid());
        assertEquals(1, result.getViolationCount());
    }

    @Test
    public void testValidateMultipleViolations() {
        playlist.addRule(new BpmRange(100, 130));
        playlist.addRule(new NoExplicit());
        playlist.addSong(song1); // passes both
        playlist.addSong(song2); // fails BpmRange (140 > 130)
        playlist.addSong(new Song("s4", "Four", "Artist C", 90, true, "sad"));
        ValidationResult result = playlist.validate();
        assertFalse(result.isValid());
        assertEquals(3, result.getViolationCount()); // 1 + 2
    }

    @Test
    public void testValidateNoRepeatArtist() {
        playlist.addRule(new NoRepeatArtist());
        playlist.addSong(new Song("a1", "T1", "Artist A", 120, false, "happy"));
        playlist.addSong(new Song("a2", "T2", "Artist A", 120, false, "happy"));
        playlist.addSong(new Song("b1", "T3", "Artist B", 120, false, "happy"));
        ValidationResult result = playlist.validate();
        assertFalse(result.isValid());
        assertEquals(1, result.getViolationCount());
        assertEquals("a2", result.getViolations()[0].getSong().getId());
    }

    @Test
    public void testValidateNoRepeatArtistNonAdjacent() {
        // same artist is fine as long as the songs aren't back to back
        playlist.addRule(new NoRepeatArtist());
        playlist.addSong(song1); // Artist A
        playlist.addSong(song2); // Artist B
        playlist.addSong(song3); // Artist A
        assertTrue(playlist.validate().isValid());
    }

    @Test
    public void testValidateMoodConsistency() {
        playlist.addRule(new MoodConsistency("happy"));
        playlist.addSong(new Song("h1", "H1", "Artist A", 120, false, "happy"));
        playlist.addSong(new Song("h2", "H2", "Artist B", 120, false, "happy"));
        playlist.addSong(new Song("x", "Sad", "Artist C", 120, false, "sad"));
        ValidationResult result = playlist.validate();
        assertFalse(result.isValid());
        assertEquals(1, result.getViolationCount());
    }

    @Test
    public void testValidateViolationPairs() {
        playlist.addRule(new BpmRange(110, 130));
        playlist.addRule(new NoExplicit());
        playlist.addSong(song1);
        playlist.addSong(song2);
        playlist.addSong(song3);
        ValidationResult.Violation[] v = playlist.validate().getViolations();
        assertEquals(3, v.length);
        assertEquals(song2, v[0].getSong());
        assertEquals("BpmRange", v[0].getRuleType());
        assertEquals(song3, v[1].getSong());
        assertEquals("BpmRange", v[1].getRuleType());
        assertEquals(song3, v[2].getSong());
        assertEquals("NoExplicit", v[2].getRuleType());
    }

    @Test
    public void testValidateAfterRemovingOffendingSong() {
        playlist.addRule(new NoExplicit());
        playlist.addSong(song1);
        playlist.addSong(song3);
        assertFalse(playlist.validate().isValid());
        playlist.removeSong(song3);
        assertTrue(playlist.validate().isValid());
    }

    // ----- toString -----

    @Test
    public void testToStringNormal() {
        playlist.addSong(song1);
        playlist.addRule(new NoExplicit());
        String text = playlist.toString();
        assertTrue(text.contains("songs=1"));
        assertTrue(text.contains("rules=1"));
    }

    @Test
    public void testToStringEmpty() {
        String text = playlist.toString();
        assertTrue(text.contains("songs=0"));
        assertTrue(text.contains("rules=0"));
    }
}
