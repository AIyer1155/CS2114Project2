/**
 * Tests for Playlist, Song, ValidationResult, and the rule classes.
 *
 * Run with: java PlaylistTest
 */
public class PlaylistTest {
    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        PlaylistTest test = new PlaylistTest();

        // Song tests
        test.testAddSong();
        test.testAddMultipleSongs();
        test.testRemoveSong();
        test.testSongEqualsById();
        test.testAddNullSong();
        test.testAddDuplicateSong();
        test.testRemoveNullSong();
        test.testRemoveSongNotInPlaylist();
        test.testRemoveFromEmptyPlaylist();
        test.testSongNullTitle();
        test.testSongNullArtist();
        test.testSongNullId();
        test.testSongNegativeBpm();
        test.testSongZeroBpm();
        test.testSongAccessors();

        // BpmRange tests
        test.testAddBpmRangeRule();
        test.testBpmRangeMinExceedsMax();
        test.testBpmRangeNegativeMin();
        test.testBpmRangeNegativeMax();
        test.testBpmRangeValidation();

        // NoExplicit tests
        test.testNoExplicitRule();
        test.testNoExplicitValidation();

        // MoodConsistency tests
        test.testMoodConsistencyRule();
        test.testMoodConsistencyNullMood();
        test.testMoodConsistencyValidation();

        // NoRepeatArtist tests
        test.testNoRepeatArtistRule();

        // Duplicate rule tests
        test.testAddDuplicateRuleType();
        test.testAddNullRule();
        test.testRemoveNullRule();
        test.testRemoveRuleNotInPlaylist();
        test.testRemoveRule();

        // Validation tests
        test.testValidateEmptyPlaylist();
        test.testValidateNoRules();
        test.testValidateAllPass();
        test.testValidateWithViolation();
        test.testValidateMultipleViolations();
        test.testValidateNoRepeatArtist();
        test.testValidateMoodConsistency();
        test.testValidateViolationPairs();

        // Constructor tests
        test.testPlaylistNullId();
        test.testPlaylistNullName();

        // ValidationResult tests
        test.testValidationResultEmpty();
        test.testValidationResultAddViolation();
        test.testValidationResultMultipleViolations();

        printResults();
    }

    private Playlist playlist;
    private Song song1, song2, song3;

    private void setUp() {
        playlist = new Playlist("p1", "Test Playlist");
        song1 = new Song("s1", "Song One", "Artist A", 120, false, "happy");
        song2 = new Song("s2", "Song Two", "Artist B", 140, false, "energetic");
        song3 = new Song("s3", "Song Three", "Artist A", 100, true, "melancholy");
    }

    // ===== Song Tests (Normal Cases) =====

    public void testAddSong() {
        setUp();
        try {
            playlist.addSong(song1);
            assertTrue(playlist.getSongCount() == 1);
            assertTrue(playlist.containsSong(song1));
            pass("testAddSong");
        } catch (Exception e) {
            fail("testAddSong: " + e.getMessage());
        }
    }

    public void testAddMultipleSongs() {
        setUp();
        try {
            playlist.addSong(song1);
            playlist.addSong(song2);
            playlist.addSong(song3);
            assertTrue(playlist.getSongCount() == 3);
            pass("testAddMultipleSongs");
        } catch (Exception e) {
            fail("testAddMultipleSongs: " + e.getMessage());
        }
    }

    public void testRemoveSong() {
        setUp();
        try {
            playlist.addSong(song1);
            playlist.addSong(song2);
            boolean removed = playlist.removeSong(song1);
            assertTrue(removed);
            assertTrue(playlist.getSongCount() == 1);
            assertTrue(!playlist.containsSong(song1));
            pass("testRemoveSong");
        } catch (Exception e) {
            fail("testRemoveSong: " + e.getMessage());
        }
    }

    public void testSongEqualsById() {
        setUp();
        try {
            Song same = new Song("s1", "Different Title", "Different Artist", 90, true, "sad");
            playlist.addSong(song1);
            assertTrue(playlist.containsSong(same));
            pass("testSongEqualsById");
        } catch (Exception e) {
            fail("testSongEqualsById: " + e.getMessage());
        }
    }

    // ===== Song Tests (Bad Input) =====

    public void testAddNullSong() {
        setUp();
        try {
            playlist.addSong(null);
            fail("testAddNullSong: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testAddNullSong");
        } catch (Exception e) {
            fail("testAddNullSong: wrong exception type");
        }
    }

    public void testAddDuplicateSong() {
        setUp();
        try {
            playlist.addSong(song1);
            Song dup = new Song("s1", "Different Title", "Different Artist", 90, false, "happy");
            playlist.addSong(dup);
            fail("testAddDuplicateSong: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testAddDuplicateSong");
        } catch (Exception e) {
            fail("testAddDuplicateSong: wrong exception type");
        }
    }

    public void testRemoveNullSong() {
        setUp();
        try {
            playlist.removeSong(null);
            fail("testRemoveNullSong: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testRemoveNullSong");
        } catch (Exception e) {
            fail("testRemoveNullSong: wrong exception type");
        }
    }

    public void testRemoveSongNotInPlaylist() {
        setUp();
        try {
            playlist.addSong(song1);
            boolean removed = playlist.removeSong(song2);
            assertTrue(!removed);
            assertTrue(playlist.getSongCount() == 1);
            pass("testRemoveSongNotInPlaylist");
        } catch (Exception e) {
            fail("testRemoveSongNotInPlaylist: " + e.getMessage());
        }
    }

    public void testRemoveFromEmptyPlaylist() {
        setUp();
        try {
            boolean removed = playlist.removeSong(song1);
            assertTrue(!removed);
            pass("testRemoveFromEmptyPlaylist");
        } catch (Exception e) {
            fail("testRemoveFromEmptyPlaylist: " + e.getMessage());
        }
    }

    public void testSongNullTitle() {
        try {
            new Song("s1", null, "Artist", 100, false, "happy");
            fail("testSongNullTitle: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testSongNullTitle");
        } catch (Exception e) {
            fail("testSongNullTitle: wrong exception type");
        }
    }

    public void testSongNullArtist() {
        try {
            new Song("s1", "Title", null, 100, false, "happy");
            fail("testSongNullArtist: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testSongNullArtist");
        } catch (Exception e) {
            fail("testSongNullArtist: wrong exception type");
        }
    }

    public void testSongNullId() {
        try {
            new Song(null, "Title", "Artist", 100, false, "happy");
            fail("testSongNullId: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testSongNullId");
        } catch (Exception e) {
            fail("testSongNullId: wrong exception type");
        }
    }

    public void testSongNegativeBpm() {
        try {
            new Song("s1", "Title", "Artist", -10, false, "happy");
            fail("testSongNegativeBpm: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testSongNegativeBpm");
        } catch (Exception e) {
            fail("testSongNegativeBpm: wrong exception type");
        }
    }

    public void testSongZeroBpm() {
        try {
            new Song("s1", "Title", "Artist", 0, false, "happy");
            fail("testSongZeroBpm: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testSongZeroBpm");
        } catch (Exception e) {
            fail("testSongZeroBpm: wrong exception type");
        }
    }

    public void testSongAccessors() {
        setUp();
        try {
            assertTrue(song3.getId().equals("s3"));
            assertTrue(song3.getTitle().equals("Song Three"));
            assertTrue(song3.getArtist().equals("Artist A"));
            assertTrue(song3.getBpm() == 100);
            assertTrue(song3.isExplicit());
            assertTrue(song3.getMood().equals("melancholy"));
            pass("testSongAccessors");
        } catch (Exception e) {
            fail("testSongAccessors: " + e.getMessage());
        }
    }

    // ===== Rule Tests (BpmRange) =====

    public void testAddBpmRangeRule() {
        setUp();
        try {
            BpmRange rule = new BpmRange(100, 150);
            playlist.addRule(rule);
            assertTrue(playlist.getRuleCount() == 1);
            pass("testAddBpmRangeRule");
        } catch (Exception e) {
            fail("testAddBpmRangeRule: " + e.getMessage());
        }
    }

    public void testBpmRangeMinExceedsMax() {
        try {
            new BpmRange(150, 100);
            fail("testBpmRangeMinExceedsMax: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testBpmRangeMinExceedsMax");
        } catch (Exception e) {
            fail("testBpmRangeMinExceedsMax: wrong exception type");
        }
    }

    public void testBpmRangeNegativeMin() {
        try {
            new BpmRange(-10, 150);
            fail("testBpmRangeNegativeMin: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testBpmRangeNegativeMin");
        } catch (Exception e) {
            fail("testBpmRangeNegativeMin: wrong exception type");
        }
    }

    public void testBpmRangeNegativeMax() {
        try {
            new BpmRange(100, -10);
            fail("testBpmRangeNegativeMax: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testBpmRangeNegativeMax");
        } catch (Exception e) {
            fail("testBpmRangeNegativeMax: wrong exception type");
        }
    }

    public void testBpmRangeValidation() {
        setUp();
        try {
            BpmRange rule = new BpmRange(100, 150);
            assertTrue(rule.validate(song1)); // 120 is in range
            assertTrue(rule.validate(song3)); // 100 is at boundary
            assertTrue(!rule.validate(new Song("x", "T", "A", 99, false, "happy")));
            assertTrue(!rule.validate(new Song("y", "T", "A", 151, false, "happy")));
            pass("testBpmRangeValidation");
        } catch (Exception e) {
            fail("testBpmRangeValidation: " + e.getMessage());
        }
    }

    // ===== Rule Tests (NoExplicit) =====

    public void testNoExplicitRule() {
        setUp();
        try {
            NoExplicit rule = new NoExplicit();
            playlist.addRule(rule);
            assertTrue(playlist.getRuleCount() == 1);
            pass("testNoExplicitRule");
        } catch (Exception e) {
            fail("testNoExplicitRule: " + e.getMessage());
        }
    }

    public void testNoExplicitValidation() {
        setUp();
        try {
            NoExplicit rule = new NoExplicit();
            assertTrue(rule.validate(song1)); // not explicit
            assertTrue(!rule.validate(song3)); // explicit
            pass("testNoExplicitValidation");
        } catch (Exception e) {
            fail("testNoExplicitValidation: " + e.getMessage());
        }
    }

    // ===== Rule Tests (MoodConsistency) =====

    public void testMoodConsistencyRule() {
        setUp();
        try {
            MoodConsistency rule = new MoodConsistency("happy");
            playlist.addRule(rule);
            assertTrue(playlist.getRuleCount() == 1);
            pass("testMoodConsistencyRule");
        } catch (Exception e) {
            fail("testMoodConsistencyRule: " + e.getMessage());
        }
    }

    public void testMoodConsistencyNullMood() {
        try {
            new MoodConsistency(null);
            fail("testMoodConsistencyNullMood: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testMoodConsistencyNullMood");
        } catch (Exception e) {
            fail("testMoodConsistencyNullMood: wrong exception type");
        }
    }

    public void testMoodConsistencyValidation() {
        setUp();
        try {
            MoodConsistency rule = new MoodConsistency("happy");
            assertTrue(rule.validate(song1)); // mood matches
            assertTrue(!rule.validate(song2)); // mood doesn't match
            pass("testMoodConsistencyValidation");
        } catch (Exception e) {
            fail("testMoodConsistencyValidation: " + e.getMessage());
        }
    }

    // ===== Rule Tests (NoRepeatArtist) =====

    public void testNoRepeatArtistRule() {
        setUp();
        try {
            NoRepeatArtist rule = new NoRepeatArtist();
            playlist.addRule(rule);
            assertTrue(playlist.getRuleCount() == 1);
            pass("testNoRepeatArtistRule");
        } catch (Exception e) {
            fail("testNoRepeatArtistRule: " + e.getMessage());
        }
    }

    // ===== Duplicate Rule Tests =====

    public void testAddDuplicateRuleType() {
        setUp();
        try {
            BpmRange rule1 = new BpmRange(100, 150);
            BpmRange rule2 = new BpmRange(80, 140);
            playlist.addRule(rule1);
            playlist.addRule(rule2);
            fail("testAddDuplicateRuleType: should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("testAddDuplicateRuleType");
        } catch (Exception e) {
            fail("testAddDuplicateRuleType: wrong exception type");
        }
    }

    public void testAddNullRule() {
        setUp();
        try {
            playlist.addRule(null);
            fail("testAddNullRule: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testAddNullRule");
        } catch (Exception e) {
            fail("testAddNullRule: wrong exception type");
        }
    }

    public void testRemoveNullRule() {
        setUp();
        try {
            playlist.removeRule(null);
            fail("testRemoveNullRule: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testRemoveNullRule");
        } catch (Exception e) {
            fail("testRemoveNullRule: wrong exception type");
        }
    }

    public void testRemoveRuleNotInPlaylist() {
        setUp();
        try {
            BpmRange rule1 = new BpmRange(100, 150);
            BpmRange rule2 = new BpmRange(80, 140);
            playlist.addRule(rule1);
            boolean removed = playlist.removeRule(rule2);
            assertTrue(!removed);
            pass("testRemoveRuleNotInPlaylist");
        } catch (Exception e) {
            fail("testRemoveRuleNotInPlaylist: " + e.getMessage());
        }
    }

    public void testRemoveRule() {
        setUp();
        try {
            NoExplicit rule = new NoExplicit();
            playlist.addRule(rule);
            assertTrue(playlist.removeRule(rule));
            assertTrue(playlist.getRuleCount() == 0);
            playlist.addRule(new NoExplicit()); // type can be added again
            assertTrue(playlist.getRuleCount() == 1);
            pass("testRemoveRule");
        } catch (Exception e) {
            fail("testRemoveRule: " + e.getMessage());
        }
    }

    // ===== Validation Tests (Normal Cases) =====

    public void testValidateEmptyPlaylist() {
        setUp();
        try {
            ValidationResult result = playlist.validate();
            assertTrue(result.isValid());
            assertTrue(result.getViolationCount() == 0);
            pass("testValidateEmptyPlaylist");
        } catch (Exception e) {
            fail("testValidateEmptyPlaylist: " + e.getMessage());
        }
    }

    public void testValidateNoRules() {
        setUp();
        try {
            playlist.addSong(song1);
            playlist.addSong(song2);
            ValidationResult result = playlist.validate();
            assertTrue(result.isValid());
            pass("testValidateNoRules");
        } catch (Exception e) {
            fail("testValidateNoRules: " + e.getMessage());
        }
    }

    public void testValidateAllPass() {
        setUp();
        try {
            BpmRange rule = new BpmRange(100, 150);
            playlist.addRule(rule);
            playlist.addSong(song1);
            playlist.addSong(song2);
            ValidationResult result = playlist.validate();
            assertTrue(result.isValid());
            assertTrue(result.getViolationCount() == 0);
            pass("testValidateAllPass");
        } catch (Exception e) {
            fail("testValidateAllPass: " + e.getMessage());
        }
    }

    public void testValidateWithViolation() {
        setUp();
        try {
            NoExplicit rule = new NoExplicit();
            playlist.addRule(rule);
            playlist.addSong(song1);
            playlist.addSong(song3); // explicit
            ValidationResult result = playlist.validate();
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 1);
            pass("testValidateWithViolation");
        } catch (Exception e) {
            fail("testValidateWithViolation: " + e.getMessage());
        }
    }

    public void testValidateMultipleViolations() {
        setUp();
        try {
            BpmRange rule1 = new BpmRange(100, 130);
            NoExplicit rule2 = new NoExplicit();
            playlist.addRule(rule1);
            playlist.addRule(rule2);
            playlist.addSong(song1); // passes both
            playlist.addSong(song2); // fails BpmRange (140 > 130)
            Song song4 = new Song("s4", "Song Four", "Artist C", 90, true, "sad"); // fails both
            playlist.addSong(song4);
            ValidationResult result = playlist.validate();
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 3); // 1 from song2 + 2 from song4
            pass("testValidateMultipleViolations");
        } catch (Exception e) {
            fail("testValidateMultipleViolations: " + e.getMessage());
        }
    }

    public void testValidateNoRepeatArtist() {
        setUp();
        try {
            NoRepeatArtist rule = new NoRepeatArtist();
            playlist.addRule(rule);
            Song a1 = new Song("a1", "Track 1", "Artist A", 120, false, "happy");
            Song a2 = new Song("a2", "Track 2", "Artist A", 120, false, "happy");
            Song b1 = new Song("b1", "Track 3", "Artist B", 120, false, "happy");
            playlist.addSong(a1);
            playlist.addSong(a2); // same artist as previous
            playlist.addSong(b1);
            ValidationResult result = playlist.validate();
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 1); // a2 violates
            pass("testValidateNoRepeatArtist");
        } catch (Exception e) {
            fail("testValidateNoRepeatArtist: " + e.getMessage());
        }
    }

    public void testValidateMoodConsistency() {
        setUp();
        try {
            MoodConsistency rule = new MoodConsistency("happy");
            playlist.addRule(rule);
            Song happy1 = new Song("h1", "Happy 1", "Artist A", 120, false, "happy");
            Song happy2 = new Song("h2", "Happy 2", "Artist B", 120, false, "happy");
            Song sad = new Song("s", "Sad", "Artist C", 120, false, "sad");
            playlist.addSong(happy1);
            playlist.addSong(happy2);
            playlist.addSong(sad);
            ValidationResult result = playlist.validate();
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 1); // sad fails
            pass("testValidateMoodConsistency");
        } catch (Exception e) {
            fail("testValidateMoodConsistency: " + e.getMessage());
        }
    }

    public void testValidateViolationPairs() {
        setUp();
        try {
            playlist.addRule(new BpmRange(110, 130));
            playlist.addRule(new NoExplicit());
            playlist.addSong(song1);
            playlist.addSong(song2);
            playlist.addSong(song3);
            ValidationResult.Violation[] v = playlist.validate().getViolations();
            assertTrue(v.length == 3);
            assertTrue(v[0].getSong().equals(song2));
            assertTrue(v[0].getRuleType().equals("BpmRange"));
            assertTrue(v[1].getSong().equals(song3));
            assertTrue(v[1].getRuleType().equals("BpmRange"));
            assertTrue(v[2].getSong().equals(song3));
            assertTrue(v[2].getRuleType().equals("NoExplicit"));
            pass("testValidateViolationPairs");
        } catch (Exception e) {
            fail("testValidateViolationPairs: " + e.getMessage());
        }
    }

    // ===== Playlist Constructor Tests =====

    public void testPlaylistNullId() {
        try {
            new Playlist(null, "Name");
            fail("testPlaylistNullId: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testPlaylistNullId");
        } catch (Exception e) {
            fail("testPlaylistNullId: wrong exception type");
        }
    }

    public void testPlaylistNullName() {
        try {
            new Playlist("id", null);
            fail("testPlaylistNullName: should throw NullPointerException");
        } catch (NullPointerException e) {
            pass("testPlaylistNullName");
        } catch (Exception e) {
            fail("testPlaylistNullName: wrong exception type");
        }
    }

    // ===== ValidationResult Tests =====

    public void testValidationResultEmpty() {
        try {
            ValidationResult result = new ValidationResult();
            assertTrue(result.isValid());
            assertTrue(result.getViolationCount() == 0);
            pass("testValidationResultEmpty");
        } catch (Exception e) {
            fail("testValidationResultEmpty: " + e.getMessage());
        }
    }

    public void testValidationResultAddViolation() {
        setUp();
        try {
            ValidationResult result = new ValidationResult();
            result.addViolation(song1, "BpmRange");
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 1);
            pass("testValidationResultAddViolation");
        } catch (Exception e) {
            fail("testValidationResultAddViolation: " + e.getMessage());
        }
    }

    public void testValidationResultMultipleViolations() {
        setUp();
        try {
            ValidationResult result = new ValidationResult();
            result.addViolation(song1, "BpmRange");
            result.addViolation(song2, "NoExplicit");
            result.addViolation(song3, "BpmRange");
            assertTrue(!result.isValid());
            assertTrue(result.getViolationCount() == 3);
            pass("testValidationResultMultipleViolations");
        } catch (Exception e) {
            fail("testValidationResultMultipleViolations: " + e.getMessage());
        }
    }

    // ===== Test Helpers =====

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new RuntimeException("Assertion failed");
        }
    }

    private static void pass(String testName) {
        passCount++;
        System.out.println("[PASS] " + testName);
    }

    private static void fail(String message) {
        failCount++;
        System.out.println("[FAIL] " + message);
    }

    private static void printResults() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST RESULTS");
        System.out.println("=".repeat(60));
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Total:  " + (passCount + failCount));
        System.out.println("=".repeat(60));
        if (failCount == 0) {
            System.out.println("✓ ALL TESTS PASSED");
        } else {
            System.out.println("✗ SOME TESTS FAILED");
        }
    }
}
