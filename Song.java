/**
 * A single song. Songs are identified by id.
 */
public class Song {
    private String id;
    private String title;
    private String artist;
    private int bpm;
    private boolean explicit;
    private String mood;

    /**
     * Creates a Song with the given attributes.
     *
     * @param id unique identifier for the song (must not be null)
     * @param title song name (must not be null)
     * @param artist artist name (must not be null)
     * @param bpm beats per minute (must be positive)
     * @param explicit true if song contains explicit content
     * @param mood mood tag, e.g. "happy" (may be null)
     * @throws NullPointerException if id, title, or artist is null
     * @throws IllegalArgumentException if bpm is not positive
     */
    public Song(String id, String title, String artist, int bpm,
                boolean explicit, String mood) {
        if (id == null) {
            throw new NullPointerException("Song id cannot be null");
        }
        if (title == null) {
            throw new NullPointerException("Title cannot be null");
        }
        if (artist == null) {
            throw new NullPointerException("Artist cannot be null");
        }
        if (bpm <= 0) {
            throw new IllegalArgumentException("BPM must be positive");
        }

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.bpm = bpm;
        this.explicit = explicit;
        this.mood = mood;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getBpm() {
        return bpm;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public String getMood() {
        return mood;
    }

    /**
     * Two songs are equal if they have the same id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Song)) {
            return false;
        }
        return id.equals(((Song) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", bpm=" + bpm +
                ", explicit=" + explicit +
                ", mood='" + mood + '\'' +
                '}';
    }
}
