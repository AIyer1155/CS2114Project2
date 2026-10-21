import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for SongLibrary: a normal and a bad-input case per public method.
 */
public class SongLibraryTest {
    private SongLibrary library;
    private Song song1;
    private Song song2;

    @Before
    public void setUp() {
        library = new SongLibrary();
        song1 = new Song("s1", "One", "Artist A", 120, false, "chill");
        song2 = new Song("s2", "Two", "Artist B", 140, true, "party");
    }

    @Test
    public void testNewLibraryIsEmpty() {
        assertEquals(0, library.size());
        assertEquals(0, library.getSongs().length);
    }

    @Test
    public void testAddSongKeepsOrder() {
        library.addSong(song1);
        library.addSong(song2);
        assertEquals(2, library.size());
        assertEquals(song1, library.getSongs()[0]);
        assertEquals(song2, library.getSongs()[1]);
    }

    @Test(expected = NullPointerException.class)
    public void testAddSongNull() {
        library.addSong(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSongDuplicateId() {
        library.addSong(song1);
        library.addSong(new Song("s1", "Other", "Artist Z", 100, false, null));
    }

    @Test
    public void testRemoveSong() {
        library.addSong(song1);
        library.addSong(song2);
        assertTrue(library.removeSong(song1));
        assertEquals(1, library.size());
        assertNull(library.findById("s1"));
    }

    @Test
    public void testRemoveSongNotPresent() {
        library.addSong(song1);
        assertFalse(library.removeSong(song2));
        assertEquals(1, library.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveSongNull() {
        library.removeSong(null);
    }

    @Test
    public void testFindById() {
        library.addSong(song1);
        library.addSong(song2);
        assertSame(song2, library.findById("s2"));
    }

    @Test
    public void testFindByIdMissing() {
        library.addSong(song1);
        assertNull(library.findById("nope"));
        assertNull(new SongLibrary().findById("s1"));
    }

    @Test(expected = NullPointerException.class)
    public void testFindByIdNull() {
        library.findById(null);
    }

    @Test
    public void testGetSongsReturnsCopy() {
        library.addSong(song1);
        Song[] copy = library.getSongs();
        copy[0] = null;
        assertEquals(song1, library.getSongs()[0]);
    }

    @Test
    public void testSizeAfterAddAndRemove() {
        library.addSong(song1);
        library.removeSong(song1);
        assertEquals(0, library.size());
    }
}
