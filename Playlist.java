/**
 * A playlist of songs and the rules they are validated against.
 * Songs and rules are each stored in a LinkedChain.
 */
public class Playlist {
    private String id;
    private String name;
    private LinkedChain<Song> songs;
    private LinkedChain<PlaylistRule> rules;

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
    }

    /**
     * Adds a song to the playlist.
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
     * Removes a song from the playlist.
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
        PlaylistRule[] ruleArray = new PlaylistRule[rules.size()];
        rules.toArray(ruleArray);
        for (PlaylistRule existing : ruleArray) {
            if (existing.getType().equals(rule.getType())) {
                throw new IllegalArgumentException(
                    "Rule type " + rule.getType() +
                    " already exists; remove it first");
            }
        }
        rules.addToEnd(rule);
    }

    /**
     * Removes a rule from the playlist.
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

        Song[] songArray = new Song[songs.size()];
        songs.toArray(songArray);

        PlaylistRule[] ruleArray = new PlaylistRule[rules.size()];
        rules.toArray(ruleArray);

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
