/**
 * Represents a task that can be marked as done or not done.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
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
        return isDone ? "X" : " ";
    }

    /**
     * Returns the icon that identifies the task type.
     */
    protected abstract String getTaskTypeIcon();

    /**
     * Returns extra details to display after the task description.
     */
    protected String getDetails() {
        return "";
    }

    @Override
    public String toString() {
        return "[" + getTaskTypeIcon() + "][" + getStatusIcon() + "] " + description + getDetails();
    }
}
