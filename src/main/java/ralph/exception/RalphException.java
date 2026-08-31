package ralph.exception;

/**
 * Represents an error specific to Ralph's command parsing and validation.
 */
public class RalphException extends Exception {
    public RalphException(String message) {
        super(message);
    }
}
