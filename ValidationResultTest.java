import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for ValidationResult and ValidationResult.Violation.
 * One normal case and one bad-input case per public method.
 */
public class ValidationResultTest {
    private ValidationResult result;
    private Song song1;
    private Song song2;

    @Before
    public void setUp() {
        result = new ValidationResult();
        song1 = new Song("s1", "Song One", "Artist A", 120, false, "happy");
        song2 = new Song("s2", "Song Two", "Artist B", 140, true, "sad");
    }

    // ----- constructor / isValid -----

    @Test
    public void testNewResultIsValid() {
        assertTrue(result.isValid());
        assertEquals(0, result.getViolationCount());
    }

    @Test
    public void testIsValidFalseAfterViolation() {
        result.addViolation(song1, "BpmRange");
        assertFalse(result.isValid());
    }

    // ----- addViolation -----

    @Test
    public void testAddViolation() {
        result.addViolation(song1, "BpmRange");
        result.addViolation(song2, "NoExplicit");
        assertEquals(2, result.getViolationCount());
    }

    @Test
    public void testAddViolationNullSong() {
        assertThrows(NullPointerException.class,
            () -> result.addViolation(null, "BpmRange"));
        assertTrue(result.isValid());
        assertEquals(0, result.getViolationCount());
    }

    @Test
    public void testAddViolationNullRuleType() {
        assertThrows(NullPointerException.class,
            () -> result.addViolation(song1, null));
        assertTrue(result.isValid());
    }

    // ----- getViolationCount -----

    @Test
    public void testGetViolationCountNormal() {
        result.addViolation(song1, "BpmRange");
        result.addViolation(song2, "NoExplicit");
        result.addViolation(song2, "BpmRange");
        assertEquals(3, result.getViolationCount());
    }

    @Test
    public void testGetViolationCountEmpty() {
        assertEquals(0, result.getViolationCount());
    }

    // ----- getViolations -----

    @Test
    public void testGetViolationsInOrder() {
        result.addViolation(song1, "BpmRange");
        result.addViolation(song2, "NoExplicit");
        ValidationResult.Violation[] v = result.getViolations();
        assertEquals(2, v.length);
        assertEquals(song1, v[0].getSong());
        assertEquals("BpmRange", v[0].getRuleType());
        assertEquals(song2, v[1].getSong());
        assertEquals("NoExplicit", v[1].getRuleType());
    }

    @Test
    public void testGetViolationsEmpty() {
        ValidationResult.Violation[] v = result.getViolations();
        assertNotNull(v);
        assertEquals(0, v.length);
    }

    // ----- toString -----

    @Test
    public void testToStringInvalid() {
        result.addViolation(song1, "BpmRange");
        assertEquals("ValidationResult{isValid=false, violations=1}",
            result.toString());
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("ValidationResult{isValid=true, violations=0}",
            result.toString());
    }

    // ===== Violation =====

    @Test
    public void testViolationGetters() {
        ValidationResult.Violation v =
            new ValidationResult.Violation(song1, "NoExplicit");
        assertEquals(song1, v.getSong());
        assertEquals("NoExplicit", v.getRuleType());
        assertEquals("Violation{s1 fails NoExplicit}", v.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testViolationNullSong() {
        new ValidationResult.Violation(null, "NoExplicit");
    }

    @Test(expected = NullPointerException.class)
    public void testViolationNullRuleType() {
        new ValidationResult.Violation(song1, null);
    }
}
