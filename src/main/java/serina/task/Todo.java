package serina.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task with the given description and saved status.
     *
     * @param description Text describing the task.
     * @param status Saved completion status.
     */
    public Todo(String description, TaskStatus status) {
        super(description, status);
    }

    /**
     * Returns the todo task type.
     *
     * @return {@link TaskType#TODO}.
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }
}
