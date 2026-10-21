/**
 * Base class for playlist validation rules.
 */
public abstract class PlaylistRule {
    protected String ruleType;

    /**
     * Validates whether a song satisfies this rule.
     *
     * @param song the song to validate
     * @return true if the song passes this rule, false otherwise
     * @throws NullPointerException if song is null
     */
    public abstract boolean validate(Song song);

    /**
     * Returns the rule type name.
     */
    public String getType() {
        return ruleType;
    }

    @Override
    public String toString() {
        return ruleType;
    }
}
