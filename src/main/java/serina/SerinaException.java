package serina;

/**
 * Represents errors that Serina can explain to the user.
 */
public class SerinaException extends Exception {
    private final SerinaError error;

    /**
     * Creates a Serina-specific exception with the given error.
     *
     * @param error error that explains what went wrong
     */
    public SerinaException(SerinaError error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Returns whether Serina should exit after this exception is shown.
     */
    public boolean shouldExit() {
        return error.shouldExit();
    }
}
