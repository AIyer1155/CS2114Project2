/**
 * The result of validating a playlist: whether it is valid, and which
 * songs broke which rules.
 */
public class ValidationResult {
    private boolean isValid;
    private LinkedChain<Violation> violations;

    /**
     * Creates a valid result with no violations.
     */
    public ValidationResult() {
        this.isValid = true;
        this.violations = new LinkedChain<>();
    }

    public boolean isValid() {
        return isValid;
    }

    /**
     * Adds a violation and marks the result as invalid.
     *
     * @param song the song that violated a rule
     * @param ruleType the rule type name
     * @throws NullPointerException if song or ruleType is null
     */
    public void addViolation(Song song, String ruleType) {
        violations.addToEnd(new Violation(song, ruleType));
        this.isValid = false;
    }

    /**
     * Returns the number of violations.
     */
    public int getViolationCount() {
        return violations.size();
    }

    /**
     * Returns violations as an array.
     */
    public Violation[] getViolations() {
        if (violations.isEmpty()) {
            return new Violation[0];
        }
        Violation[] result = new Violation[violations.size()];
        return violations.toArray(result);
    }

    @Override
    public String toString() {
        if (isValid) {
            return "ValidationResult{isValid=true, violations=0}";
        }
        return "ValidationResult{isValid=false, violations=" +
               violations.size() + "}";
    }

    /**
     * A song and the rule it violated.
     */
    public static class Violation {
        private Song song;
        private String ruleType;

        /**
         * @throws NullPointerException if song or ruleType is null
         */
        public Violation(Song song, String ruleType) {
            if (song == null) {
                throw new NullPointerException("Song cannot be null");
            }
            if (ruleType == null) {
                throw new NullPointerException("Rule type cannot be null");
            }
            this.song = song;
            this.ruleType = ruleType;
        }

        public Song getSong() {
            return song;
        }

        public String getRuleType() {
            return ruleType;
        }

        @Override
        public String toString() {
            return "Violation{" + song.getId() + " fails " + ruleType + "}";
        }
    }
}
