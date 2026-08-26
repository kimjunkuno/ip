package serina.exception;

/**
 * Represents errors that Serina can explain to the user.
 */
public class SerinaException extends Exception {
    /** Serina-specific error represented by this exception. */
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
     *
     * @return {@code true} if this exception should end the application
     */
    public boolean shouldExit() {
        return error.shouldExit();
    }
}
