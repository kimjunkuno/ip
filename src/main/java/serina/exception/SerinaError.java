package serina.exception;

/**
 * Represents Serina-specific errors and their user-facing behavior.
 */
public enum SerinaError {
    /** Indicates that no more tasks can be added. */
    MAX_TASKS("You've reached the maximum number of tasks.", true),
    /** Indicates that the entered command is not supported. */
    UNKNOWN_COMMAND(
            "Sorry captain, could you rephrase that for me? Type help to see the available commands.", false),
    /** Indicates that a task number is missing, malformed, or outside the list. */
    INVALID_TASK_NUMBER("Sorry captain, please provide a valid task number.", false),
    /** Indicates that a todo description is empty. */
    EMPTY_TODO("Sorry captain, todo descriptions can't be empty.", false),
    /** Indicates that a deadline description is empty. */
    EMPTY_DEADLINE_DESCRIPTION("Sorry captain, deadline descriptions can't be empty.", false),
    /** Indicates that a deadline date is empty. */
    EMPTY_DEADLINE_BY("Sorry captain, deadlines need a /by date.", false),
    /** Indicates that an event description is empty. */
    EMPTY_EVENT_DESCRIPTION("Sorry captain, event descriptions can't be empty.", false),
    /** Indicates that an event start date is empty. */
    EMPTY_EVENT_FROM("Sorry captain, events need a /from date.", false),
    /** Indicates that an event end date is empty. */
    EMPTY_EVENT_TO("Sorry captain, events need a /to date.", false),
    /** Indicates that a find command has no date. */
    EMPTY_FIND_DATE("Sorry captain, find needs a date.", false),
    /** Indicates that a deadline command has an invalid structure. */
    INVALID_DEADLINE_FORMAT("Sorry captain, please use: deadline <task> /by <date>", false),
    /** Indicates that an event command has an invalid structure. */
    INVALID_EVENT_FORMAT("Sorry captain, please use: event <task> /from <start date> /to <end date>", false),
    /** Indicates that a date is invalid or has the wrong format. */
    INVALID_DATE("Sorry captain, dates must be in yyyy-MM-dd format.", false),
    /** Indicates that an event ends before it starts. */
    INVALID_EVENT_DATE_RANGE("Sorry captain, event end date can't be before its start date.", false),
    /** Indicates that a save file contains more tasks than Serina supports. */
    LOAD_TOO_MANY_TASKS("Sorry captain, I found more than 100 saved tasks.", false),
    /** Indicates that saved tasks could not be loaded. */
    LOAD_FAILED("Sorry captain, I couldn't load your saved tasks.", false),
    /** Indicates that tasks could not be saved. */
    SAVE_FAILED("Sorry captain, I couldn't save your tasks right now.", false);

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
     *
     * @return the user-facing error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether Serina should exit after this error.
     *
     * @return {@code true} if this error should end the application
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
