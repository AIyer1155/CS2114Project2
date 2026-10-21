import java.util.NoSuchElementException;

/**
 * A playlist of songs and the rules they are validated against.
 * Songs and rules are each stored in a LinkedChain.
 * The playlist also tracks a playback position for nextSong/previousSong.
 */
public class Playlist {
    private String id;
    private String name;
    private LinkedChain<Song> songs;
    private LinkedChain<PlaylistRule> rules;
    private int currentIndex;

    /**
     * Creates an empty playlist.
     *
     * @param id playlist identifier (must not be null)
     * @param name playlist name (must not be null)
     * @throws NullPointerException if id or name is null
     */
    public Playlist(String id, String name) {
        if (id == null) {
            throw new NullPointerException("Playlist id cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("Playlist name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.songs = new LinkedChain<>();
        this.rules = new LinkedChain<>();
        this.currentIndex = -1;
    }

    /**
     * Adds a song to the end of the playlist. O(n).
     *
     * @param song the song to add (must not be null)
     * @throws NullPointerException if song is null
     * @throws IllegalArgumentException if a song with the same id already exists
     */
    public void addSong(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null");
        }
        if (songs.contains(song)) {
            throw new IllegalArgumentException(
                "Song with id " + song.getId() + " already in playlist");
        }
        songs.addToEnd(song);
    }

    /**
     * Removes a song from the playlist. If the song is at or before the
     * playback position, the position moves back so the same song stays
     * current. O(n).
     *
     * @param song the song to remove (must not be null)
     * @return true if removed, false if not found
     * @throws NullPointerException if song is null
     */
    public boolean removeSong(Song song) {
        if (song == null) {
            throw new NullPointerException("Song cannot be null");
        }
        int position = indexOf(song);
        if (position < 0) {
            return false;
        }
        songs.remove(song);
        if (position <= currentIndex) {
            currentIndex--;
        }
        return true;
    }

    private int indexOf(Song song) {
        Song[] array = getSongs();
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(song)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the number of songs in the playlist.
     */
    public int getSongCount() {
        return songs.size();
    }

    /**
     * Returns true if the playlist contains the given song.
     */
    public boolean containsSong(Song song) {
        return songs.contains(song);
    }

    /**
     * Returns a copy of the songs in playlist order. O(n).
     */
    public Song[] getSongs() {
        Song[] array = new Song[songs.size()];
        songs.toArray(array);
        return array;
    }

    /**
     * Moves playback to the next song and returns it. The first call
     * starts at the first song. O(n).
     *
     * @return the next song
     * @throws NoSuchElementException if there is no next song (including
     *         an empty playlist)
     */
    public Song nextSong() {
        if (currentIndex + 1 >= songs.size()) {
            throw new NoSuchElementException("No more songs");
        }
        currentIndex++;
        return songs.get(currentIndex);
    }

    /**
     * Moves playback to the previous song and returns it. O(n).
     *
     * @return the previous song
     * @throws NoSuchElementException if there is no previous song (at the
     *         first song, before playback starts, or on an empty playlist)
     */
    public Song previousSong() {
        if (currentIndex <= 0) {
            throw new NoSuchElementException("No previous songs");
        }
        currentIndex--;
        return songs.get(currentIndex);
    }

    /**
     * Returns the song at the playback position, or null if playback has
     * not started. O(n).
     */
    public Song getCurrentSong() {
        if (currentIndex < 0) {
            return null;
        }
        return songs.get(currentIndex);
    }

    /**
     * Adds a validation rule to the playlist.
     * At most one rule of each type is allowed.
     *
     * @param rule the rule to add (must not be null)
     * @throws NullPointerException if rule is null
     * @throws IllegalArgumentException if a rule of this type already exists
     */
    public void addRule(PlaylistRule rule) {
        if (rule == null) {
            throw new NullPointerException("Rule cannot be null");
        }
        for (PlaylistRule existing : getRules()) {
            if (existing.getType().equals(rule.getType())) {
                throw new IllegalArgumentException(
                    "Rule type " + rule.getType() +
                    " already exists; remove it first");
            }
        }
        rules.addToEnd(rule);
    }

    /**
     * Removes a rule from the playlist, matching by object identity.
     *
     * @param rule the rule to remove (must not be null)
     * @return true if removed, false if not found
     * @throws NullPointerException if rule is null
     */
    public boolean removeRule(PlaylistRule rule) {
        if (rule == null) {
            throw new NullPointerException("Rule cannot be null");
        }
        return rules.remove(rule);
    }

    /**
     * Removes the rule with the given type name. O(r).
     *
     * @param type the rule type, such as "BpmRange" (must not be null)
     * @return true if a rule was removed, false if no rule has that type
     * @throws NullPointerException if type is null
     */
    public boolean removeRuleByType(String type) {
        if (type == null) {
            throw new NullPointerException("Rule type cannot be null");
        }
        for (PlaylistRule rule : getRules()) {
            if (rule.getType().equals(type)) {
                return rules.remove(rule);
            }
        }
        return false;
    }

    /**
     * Returns a copy of the rules in the order they were added. O(r).
     */
    public PlaylistRule[] getRules() {
        PlaylistRule[] array = new PlaylistRule[rules.size()];
        rules.toArray(array);
        return array;
    }

    /**
     * Returns the number of rules in the playlist.
     */
    public int getRuleCount() {
        return rules.size();
    }

    /**
     * Validates all songs against all rules.
     * Runs in O(n * r), where n is the number of songs and r is the
     * number of rules.
     *
     * @return the result, with one violation per failed song/rule pair
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        if (songs.isEmpty() || rules.isEmpty()) {
            return result;
        }

        Song[] songArray = getSongs();
        PlaylistRule[] ruleArray = getRules();

        Song prevSong = null;
        for (Song song : songArray) {
            for (PlaylistRule rule : ruleArray) {
                boolean passes = rule.validate(song);

                if (rule instanceof NoRepeatArtist && prevSong != null) {
                    if (song.getArtist().equals(prevSong.getArtist())) {
                        passes = false;
                    }
                }

                if (!passes) {
                    result.addViolation(song, rule.getType());
                }
            }

            prevSong = song;
        }

        return result;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", songs=" + songs.size() +
                ", rules=" + rules.size() +
                '}';
    }
}
