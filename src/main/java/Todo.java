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

    @Override
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }
}
