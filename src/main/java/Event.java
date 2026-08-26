/**
 * Represents a task that happens from a start date or time to an end date or time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description text describing the task
     * @param from start date or time
     * @param to end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event task with the given description, start, end, and saved status.
     *
     * @param description text describing the task
     * @param from start date or time
     * @param to end date or time
     * @param status saved completion status
     */
    public Event(String description, String from, String to, TaskStatus status) {
        super(description, status);
        this.from = from;
        this.to = to;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    @Override
    protected String getFileDetails() {
        return " | " + escapeFileField(from) + " | " + escapeFileField(to);
    }
}
