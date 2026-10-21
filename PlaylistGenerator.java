import java.util.Random;

/**
 * Mode 2: builds a playlist of a requested length from a song library so
 * that it satisfies a set of rules. It filters out songs that break a
 * rule on their own, then shuffles and picks songs, and finally checks
 * the result with Playlist.validate().
 */
public class PlaylistGenerator {
    private static final int MAX_ATTEMPTS = 1000;

    private Random random;

    /**
     * Creates a generator with a new random source.
     */
    public PlaylistGenerator() {
        this(new Random());
    }

    /**
     * Creates a generator with the given random source (a seeded Random
     * makes results repeatable).
     *
     * @throws NullPointerException if random is null
     */
    public PlaylistGenerator(Random random) {
        if (random == null) {
            throw new NullPointerException("Random cannot be null");
        }
        this.random = random;
    }

    /**
     * Tries to build a playlist.
     *
     * @param library the songs to choose from (must not be null)
     * @param rules the rules the playlist must satisfy (must not be null,
     *        no null elements, at most one rule per type)
     * @param length the number of songs wanted (must be positive)
     * @return a successful result with the playlist, or a failed result
     *         that names the rules that could not be satisfied
     * @throws NullPointerException if an argument or a rule is null
     * @throws IllegalArgumentException if length is not positive, or two
     *         rules share a type
     */
    public GenerationResult generate(SongLibrary library,
                                     PlaylistRule[] rules, int length) {
        if (library == null) {
            throw new NullPointerException("Library cannot be null");
        }
        if (rules == null) {
            throw new NullPointerException("Rules cannot be null");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        Playlist check = new Playlist("generated", "Generated Playlist");
        for (PlaylistRule rule : rules) {
            check.addRule(rule); // rejects null and duplicate types
        }

        boolean noRepeat = false;
        for (PlaylistRule rule : rules) {
            if (rule instanceof NoRepeatArtist) {
                noRepeat = true;
            }
        }

        // Keep only songs that pass every rule on their own.
        Song[] all = library.getSongs();
        Song[] eligible = new Song[all.length];
        int eligibleCount = 0;
        boolean[] excludedBy = new boolean[rules.length];
        for (Song song : all) {
            boolean ok = true;
            for (int r = 0; r < rules.length; r++) {
                if (!rules[r].validate(song)) {
                    ok = false;
                    excludedBy[r] = true;
                }
            }
            if (ok) {
                eligible[eligibleCount++] = song;
            }
        }

        if (eligibleCount < length) {
            String[] blamed = ruleTypes(rules, excludedBy);
            return GenerationResult.failure(
                "Only " + eligibleCount + " of " + all.length +
                " songs satisfy the rules; need " + length, blamed);
        }

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            shuffle(eligible, eligibleCount);
            Song[] picked = new Song[length];
            int count = 0;
            for (int i = 0; i < eligibleCount && count < length; i++) {
                Song candidate = eligible[i];
                if (noRepeat && count > 0 && candidate.getArtist()
                        .equals(picked[count - 1].getArtist())) {
                    continue;
                }
                picked[count++] = candidate;
            }
            if (count < length) {
                continue;
            }

            Playlist playlist = new Playlist("generated", "Generated Playlist");
            for (PlaylistRule rule : rules) {
                playlist.addRule(rule);
            }
            for (Song song : picked) {
                playlist.addSong(song);
            }
            if (playlist.validate().isValid()) {
                return GenerationResult.success(playlist);
            }
        }

        return GenerationResult.failure(
            "Could not order " + length + " songs without repeating an artist"
            + " back to back", new String[] {"NoRepeatArtist"});
    }

    private static String[] ruleTypes(PlaylistRule[] rules, boolean[] flags) {
        int n = 0;
        for (boolean flag : flags) {
            if (flag) {
                n++;
            }
        }
        String[] types = new String[n];
        int k = 0;
        for (int r = 0; r < rules.length; r++) {
            if (flags[r]) {
                types[k++] = rules[r].getType();
            }
        }
        return types;
    }

    private void shuffle(Song[] array, int count) {
        for (int i = count - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Song temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
