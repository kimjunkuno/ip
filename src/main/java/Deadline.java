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

    @Override
    protected String getTaskTypeIcon() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }
}
