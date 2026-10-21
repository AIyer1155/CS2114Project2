/**
 * The pool of songs a user can pick from. Stored in a LinkedChain,
 * the same generic class Playlist uses for songs and rules.
 */
public class SongLibrary {
    private LinkedChain<Song> songs;

    /**
     * Creates an empty library.
     */
    public SongLibrary() {
        this.songs = new LinkedChain<>();
    }

    /**
     * Adds a song to the end of the library. O(n).
     *
     * @param song the song to add (must not be null)
     * @throws NullPointerException if song is null
     * @throws IllegalArgumentException if a song with the same id exists
     */
    public void addSong(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null");
        }
        if (songs.contains(song)) {
            throw new IllegalArgumentException(
                "Song with id " + song.getId() + " already in library");
        }
        songs.addToEnd(song);
    }

    /**
     * Removes a song from the library. O(n).
     *
     * @param song the song to remove (must not be null)
     * @return true if removed, false if not found
     * @throws NullPointerException if song is null
     */
    public boolean removeSong(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null");
        }
        return songs.remove(song);
    }

    /**
     * Finds a song by id. O(n).
     *
     * @param id the song id (must not be null)
     * @return the song, or null if no song has that id
     * @throws NullPointerException if id is null
     */
    public Song findById(String id) {
        if (id == null) {
            throw new NullPointerException("Song id cannot be null");
        }
        for (Song song : getSongs()) {
            if (song.getId().equals(id)) {
                return song;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the songs in the order they were added. O(n).
     */
    public Song[] getSongs() {
        Song[] array = new Song[songs.size()];
        songs.toArray(array);
        return array;
    }

    /**
     * Returns the number of songs in the library. O(1).
     */
    public int size() {
        return songs.size();
    }
}
