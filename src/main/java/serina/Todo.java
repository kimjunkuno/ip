package serina;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task with the given description and saved status.
     *
     * @param description text describing the task
     * @param status saved completion status
     */
    public Todo(String description, TaskStatus status) {
        super(description, status);
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }
}
