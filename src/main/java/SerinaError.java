/**
 * Represents Serina-specific errors and their user-facing behavior.
 */
public enum SerinaError {
    MAX_TASKS("You've reached the maximum number of tasks.", true),
    UNKNOWN_COMMAND("Sorry captain, could you rephrase that for me?", false),
    INVALID_TASK_NUMBER("Sorry captain, please provide a valid task number.", false),
    EMPTY_TODO("Sorry captain, todo descriptions can't be empty.", false),
    EMPTY_DEADLINE_DESCRIPTION("Sorry captain, deadline descriptions can't be empty.", false),
    EMPTY_DEADLINE_BY("Sorry captain, deadlines need a /by time.", false),
    EMPTY_EVENT_DESCRIPTION("Sorry captain, event descriptions can't be empty.", false),
    EMPTY_EVENT_FROM("Sorry captain, events need a /from time.", false),
    EMPTY_EVENT_TO("Sorry captain, events need a /to time.", false),
    INVALID_DEADLINE_FORMAT("Sorry captain, please use: deadline <task> /by <time>", false),
    INVALID_EVENT_FORMAT("Sorry captain, please use: event <task> /from <start> /to <end>", false);

    private final String message;
    private final boolean shouldExit;

    /**
     * Creates an error with its display message and exit behavior.
     *
     * @param message explanation to show to the user
     * @param shouldExit whether Serina should exit after showing the message
     */
    SerinaError(String message, boolean shouldExit) {
        this.message = message;
        this.shouldExit = shouldExit;
    }

    /**
     * Returns the message shown to the user.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether Serina should exit after this error.
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
