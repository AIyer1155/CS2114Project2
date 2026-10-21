import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Text menu for the Smart Playlist Manager. Bad input is reported and the
 * user is prompted again; nothing here should crash the program.
 *
 * Run with: java PlaylistApp
 */
public class PlaylistApp {
    private static final String[] MOODS =
        {"energetic", "chill", "focus", "party"};
    private static final int MAX_CHOICE = 14;

    private final BufferedReader in;
    private final PrintStream out;
    private final Random random;
    private final SongLibrary library;
    private Playlist playlist;
    private int idCounter;

    /** Signals that the input stream ended. */
    private static class InputEnded extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Creates an app that reads from System.in and writes to System.out.
     */
    public PlaylistApp() {
        this(System.in, System.out);
    }

    /**
     * Creates an app with the given streams.
     *
     * @throws NullPointerException if a stream is null
     */
    public PlaylistApp(InputStream input, PrintStream output) {
        this(input, output, new Random());
    }

    /**
     * Creates an app with the given streams and random source.
     *
     * @throws NullPointerException if an argument is null
     */
    public PlaylistApp(InputStream input, PrintStream output, Random random) {
        if (input == null || output == null || random == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.in = new BufferedReader(new InputStreamReader(input));
        this.out = output;
        this.random = random;
        this.library = new SongLibrary();
        this.playlist = new Playlist("p1", "My Playlist");
        this.idCounter = 0;
    }

    public static void main(String[] args) {
        new PlaylistApp().run();
    }

    /**
     * Runs the menu loop until the user quits or input ends.
     */
    public void run() {
        out.println("Smart Playlist Manager");
        try {
            boolean running = true;
            while (running) {
                printMenu();
                String line = prompt("Choice");
                int choice = parseChoice(line);
                switch (choice) {
                    case 0:
                        out.println("Goodbye.");
                        running = false;
                        break;
                    case 1: addLibrarySong(); break;
                    case 2: removeLibrarySong(); break;
                    case 3: showLibrary(); break;
                    case 4: addToPlaylist(); break;
                    case 5: removeFromPlaylist(); break;
                    case 6: showPlaylist(); break;
                    case 7: addRule(); break;
                    case 8: removeRule(); break;
                    case 9: showRules(); break;
                    case 10: validate(); break;
                    case 11: next(); break;
                    case 12: previous(); break;
                    case 13: generate(); break;
                    case 14: loadSamples(); break;
                    default:
                        out.println("Invalid choice. Enter a number from 0 to "
                            + MAX_CHOICE + ".");
                }
            }
        } catch (InputEnded e) {
            out.println();
            out.println("Input ended.");
        }
    }

    // ---- menu actions ----

    private void printMenu() {
        out.println();
        out.println(" 1 Add song to library      8 Remove rule");
        out.println(" 2 Remove song from library 9 Show rules");
        out.println(" 3 Show library            10 Validate playlist");
        out.println(" 4 Add song to playlist    11 Next song");
        out.println(" 5 Remove from playlist    12 Previous song");
        out.println(" 6 Show playlist           13 Generate playlist");
        out.println(" 7 Add rule                14 Load sample songs");
        out.println(" 0 Quit");
    }

    private void addLibrarySong() {
        String title = readNonEmpty("Title");
        String artist = readNonEmpty("Artist");
        int bpm = readBpm();
        boolean explicit = readYesNo("Explicit? (y/n)");
        String mood = readMood();
        Song song = new Song(newId(), title, artist, bpm, explicit, mood);
        library.addSong(song);
        out.println("Added to library: " + describe(song));
    }

    private void removeLibrarySong() {
        if (library.size() == 0) {
            out.println("Library is empty");
            return;
        }
        showLibrary();
        Song song = library.findById(prompt("Song id to remove"));
        if (song == null) {
            out.println("Song not found in library");
            return;
        }
        library.removeSong(song);
        out.println("Removed from library: " + describe(song));
    }

    private void showLibrary() {
        Song[] songs = library.getSongs();
        if (songs.length == 0) {
            out.println("Library is empty");
            return;
        }
        out.println("Library (" + songs.length + " songs):");
        for (Song song : songs) {
            out.println("  " + describe(song));
        }
    }

    private void addToPlaylist() {
        if (library.size() == 0) {
            out.println("Library is empty");
            return;
        }
        showLibrary();
        Song song = library.findById(prompt("Song id to add"));
        if (song == null) {
            out.println("Song not found in library");
            return;
        }
        try {
            playlist.addSong(song);
            out.println("Added to playlist: " + describe(song));
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
        }
    }

    private void removeFromPlaylist() {
        if (playlist.getSongCount() == 0) {
            out.println("Playlist is empty");
            return;
        }
        showPlaylist();
        String id = prompt("Song id to remove");
        Song match = null;
        for (Song song : playlist.getSongs()) {
            if (song.getId().equals(id)) {
                match = song;
            }
        }
        if (match == null) {
            out.println("Song not found in playlist");
            return;
        }
        playlist.removeSong(match);
        out.println("Removed from playlist: " + describe(match));
    }

    private void showPlaylist() {
        Song[] songs = playlist.getSongs();
        if (songs.length == 0) {
            out.println("Playlist is empty");
            return;
        }
        Song current = playlist.getCurrentSong();
        out.println("Playlist (" + songs.length + " songs):");
        for (int i = 0; i < songs.length; i++) {
            String marker = songs[i].equals(current) ? "> " : "  ";
            out.println(marker + (i + 1) + ". " + describe(songs[i]));
        }
    }

    private void addRule() {
        out.println("Rule type: 1 BpmRange, 2 NoExplicit, "
            + "3 MoodConsistency, 4 NoRepeatArtist");
        String line = prompt("Rule type");
        String type;
        switch (line) {
            case "1": type = "BpmRange"; break;
            case "2": type = "NoExplicit"; break;
            case "3": type = "MoodConsistency"; break;
            case "4": type = "NoRepeatArtist"; break;
            default:
                out.println("Invalid rule type. Enter a number from 1 to 4.");
                return;
        }
        for (PlaylistRule existing : playlist.getRules()) {
            if (existing.getType().equals(type)) {
                out.println("Rule type " + type
                    + " already exists; remove it first");
                return;
            }
        }
        PlaylistRule rule;
        try {
            switch (type) {
                case "BpmRange":
                    String min = prompt("Minimum BPM");
                    String max = prompt("Maximum BPM");
                    if (min.isEmpty() || max.isEmpty()) {
                        out.println("All rule parameters required before adding");
                        return;
                    }
                    rule = new BpmRange(Integer.parseInt(min),
                                        Integer.parseInt(max));
                    break;
                case "NoExplicit":
                    rule = new NoExplicit();
                    break;
                case "MoodConsistency":
                    rule = new MoodConsistency(readMood());
                    break;
                default:
                    rule = new NoRepeatArtist();
            }
            playlist.addRule(rule);
            out.println("Added rule: " + rule);
        } catch (NumberFormatException e) {
            out.println("BPM values must be whole numbers");
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
        }
    }

    private void removeRule() {
        PlaylistRule[] rules = playlist.getRules();
        if (rules.length == 0) {
            out.println("No rules to remove");
            return;
        }
        showRules();
        String line = prompt("Rule number to remove");
        int number;
        try {
            number = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            number = -1;
        }
        if (number < 1 || number > rules.length) {
            out.println("Rule not found. Enter a number from 1 to "
                + rules.length + ".");
            return;
        }
        PlaylistRule rule = rules[number - 1];
        playlist.removeRuleByType(rule.getType());
        out.println("Removed rule: " + rule);
    }

    private void showRules() {
        PlaylistRule[] rules = playlist.getRules();
        if (rules.length == 0) {
            out.println("No rules set");
            return;
        }
        out.println("Rules:");
        for (int i = 0; i < rules.length; i++) {
            out.println("  " + (i + 1) + ". " + rules[i]);
        }
    }

    private void validate() {
        if (playlist.getSongCount() == 0) {
            out.println("Playlist is empty");
            return;
        }
        PlaylistRule[] rules = playlist.getRules();
        if (rules.length == 0) {
            out.println("No rules set; nothing to validate against");
            return;
        }
        ValidationResult result = playlist.validate();
        ValidationResult.Violation[] violations = result.getViolations();
        for (PlaylistRule rule : rules) {
            StringBuilder failed = new StringBuilder();
            for (ValidationResult.Violation v : violations) {
                if (v.getRuleType().equals(rule.getType())) {
                    if (failed.length() > 0) {
                        failed.append(", ");
                    }
                    failed.append(v.getSong().getId()).append(" ")
                          .append(v.getSong().getTitle());
                }
            }
            if (failed.length() == 0) {
                out.println("  PASS " + rule);
            } else {
                out.println("  FAIL " + rule + ": " + failed);
            }
        }
        if (result.isValid()) {
            out.println("Playlist is valid");
        } else {
            out.println("Playlist is not valid: " + result.getViolationCount()
                + " violation(s)");
        }
    }

    private void next() {
        try {
            out.println("Now playing: " + describe(playlist.nextSong()));
        } catch (NoSuchElementException e) {
            out.println(playlist.getSongCount() == 0
                ? "Playlist is empty" : e.getMessage());
        }
    }

    private void previous() {
        try {
            out.println("Now playing: " + describe(playlist.previousSong()));
        } catch (NoSuchElementException e) {
            out.println(playlist.getSongCount() == 0
                ? "Playlist is empty" : e.getMessage());
        }
    }

    private void generate() {
        if (library.size() == 0) {
            out.println("Library is empty");
            return;
        }
        int length = readPositiveInt("Playlist length", "Length");
        PlaylistGenerator generator = new PlaylistGenerator(random);
        GenerationResult result =
            generator.generate(library, playlist.getRules(), length);
        if (result.isSuccess()) {
            playlist = result.getPlaylist();
            out.println("Generated playlist replaced the current one.");
            showPlaylist();
        } else {
            out.println("Could not generate: " + result.getMessage());
            String[] unmet = result.getUnsatisfiedRules();
            if (unmet.length > 0) {
                out.println("Unsatisfied rules: " + String.join(", ", unmet));
            }
        }
    }

    private void loadSamples() {
        Object[][] data = {
            {"Sunrise", "Artist A", 120, false, "chill"},
            {"Uphill", "Artist A", 135, false, "energetic"},
            {"Sprint", "Artist B", 170, true, "energetic"},
            {"Cooldown", "Artist C", 95, false, "chill"},
            {"Deep Work", "Artist C", 100, false, "focus"},
            {"Late Night", "Artist D", 90, true, "chill"},
            {"Block Party", "Artist B", 128, true, "party"},
            {"Flow State", "Artist D", 110, false, "focus"},
            {"Headliner", "Artist E", 140, false, "party"},
            {"Warm Up", "Artist E", 105, false, "energetic"}
        };
        for (Object[] row : data) {
            library.addSong(new Song(newId(), (String) row[0],
                (String) row[1], (Integer) row[2], (Boolean) row[3],
                (String) row[4]));
        }
        out.println("Loaded " + data.length + " sample songs.");
    }

    // ---- input helpers ----

    private String prompt(String label) {
        out.print(label + ": ");
        out.flush();
        try {
            String line = in.readLine();
            if (line == null) {
                throw new InputEnded();
            }
            return line.trim();
        } catch (IOException e) {
            throw new InputEnded();
        }
    }

    private int parseChoice(String line) {
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String readNonEmpty(String label) {
        while (true) {
            String value = prompt(label);
            if (!value.isEmpty()) {
                return value;
            }
            out.println(label + " cannot be empty");
        }
    }

    private int readBpm() {
        while (true) {
            String value = prompt("BPM");
            try {
                int bpm = Integer.parseInt(value);
                if (bpm > 0) {
                    return bpm;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            out.println("BPM must be a positive integer");
        }
    }

    private int readPositiveInt(String label, String name) {
        while (true) {
            String value = prompt(label);
            try {
                int n = Integer.parseInt(value);
                if (n > 0) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            out.println(name + " must be a positive integer");
        }
    }

    private boolean readYesNo(String label) {
        while (true) {
            String value = prompt(label).toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            out.println("Enter y or n");
        }
    }

    private String readMood() {
        while (true) {
            String value =
                prompt("Mood (energetic, chill, focus, party)").toLowerCase();
            for (String mood : MOODS) {
                if (mood.equals(value)) {
                    return mood;
                }
            }
            out.println("Invalid mood. Valid options: "
                + java.util.Arrays.toString(MOODS));
        }
    }

    private String newId() {
        String id;
        do {
            idCounter++;
            id = "s" + idCounter;
        } while (library.findById(id) != null);
        return id;
    }

    private static String describe(Song song) {
        return song.getId() + " | " + song.getTitle() + " - "
            + song.getArtist() + " | " + song.getBpm() + " BPM | "
            + song.getMood() + (song.isExplicit() ? " | explicit" : "");
    }
}
