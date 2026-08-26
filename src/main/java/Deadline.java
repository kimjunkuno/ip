/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description text describing the task
     * @param by deadline for the task
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline task with the given description, deadline, and saved status.
     *
     * @param description text describing the task
     * @param by deadline for the task
     * @param status saved completion status
     */
    public Deadline(String description, String by, TaskStatus status) {
        super(description, status);
        this.by = by;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }

    @Override
    protected String getFileDetails() {
        return " | " + escapeFileField(by);
    }
}
