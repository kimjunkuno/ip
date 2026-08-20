/**
 * Represents errors that Serina can explain to the user.
 */
public class SerinaException extends Exception {
    /**
     * Creates a Serina-specific exception with the given message.
     *
     * @param message explanation to show to the user
     */
    public SerinaException(String message) {
        super(message);
    }
}
