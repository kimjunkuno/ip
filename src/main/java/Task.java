/**
 * Represents a task that can be marked as done or not done.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates a task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the task text without its status icon.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status icon used when displaying this task.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns the type of this task.
     */
    protected abstract TaskType getTaskType();

    /**
     * Returns extra details to display after the task description.
     */
    protected String getDetails() {
        return "";
    }

    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description + getDetails();
    }
}
