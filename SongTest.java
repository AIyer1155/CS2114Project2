import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Song.
 * One normal case and one bad-input case per public method.
 */
public class SongTest {
    private Song song;
    private Song noMood;

    @Before
    public void setUp() {
        song = new Song("s1", "Song One", "Artist A", 120, true, "happy");
        noMood = new Song("s2", "Song Two", "Artist B", 1, false, null);
    }

    // ----- constructor -----

    @Test
    public void testConstructorNormal() {
        assertEquals("s1", song.getId());
        assertEquals(120, song.getBpm());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullId() {
        new Song(null, "Title", "Artist", 100, false, "happy");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullTitle() {
        new Song("s1", null, "Artist", 100, false, "happy");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullArtist() {
        new Song("s1", "Title", null, 100, false, "happy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroBpm() {
        new Song("s1", "Title", "Artist", 0, false, "happy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeBpm() {
        new Song("s1", "Title", "Artist", -10, false, "happy");
    }

    // ----- getters -----

    @Test
    public void testGettersNormal() {
        assertEquals("s1", song.getId());
        assertEquals("Song One", song.getTitle());
        assertEquals("Artist A", song.getArtist());
        assertEquals(120, song.getBpm());
        assertTrue(song.isExplicit());
        assertEquals("happy", song.getMood());
    }

    @Test
    public void testGettersEdgeValues() {
        // lowest legal BPM, not explicit, and the optional mood left null
        assertEquals(1, noMood.getBpm());
        assertFalse(noMood.isExplicit());
        assertNull(noMood.getMood());
    }

    // ----- equals / hashCode -----

    @Test
    public void testEqualsSameId() {
        Song same = new Song("s1", "Other", "Other Artist", 90, false, "sad");
        assertEquals(song, same);
        assertEquals(song.hashCode(), same.hashCode());
    }

    @Test
    public void testEqualsBadInput() {
        assertNotEquals(song, null);
        assertNotEquals(song, "s1");
        assertNotEquals(song, noMood);
    }

    // ----- toString -----

    @Test
    public void testToStringNormal() {
        String text = song.toString();
        assertTrue(text.contains("s1"));
        assertTrue(text.contains("Song One"));
    }

    @Test
    public void testToStringNullMood() {
        assertTrue(noMood.toString().contains("mood='null'"));
    }
}
