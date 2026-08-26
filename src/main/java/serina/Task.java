package serina;

import java.time.LocalDate;

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
     * Creates a task with the given description and saved status.
     *
     * @param description text describing the task
     * @param status saved completion status
     */
    public Task(String description, TaskStatus status) {
        this.description = description;
        this.status = status;
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
     * Returns this task in Serina's simple save-file format.
     */
    public String toFileString() {
        return getTaskType().getIcon() + " | " + status.getFileValue() + " | " + escapeFileField(description)
                + getFileDetails();
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

    /**
     * Returns extra fields to append when saving this task to disk.
     */
    protected String getFileDetails() {
        return "";
    }

    /**
     * Returns whether this task happens on the given date.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Escapes characters that have special meaning in Serina's save-file format.
     */
    protected static String escapeFileField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description + getDetails();
    }
}
