package serina.task;

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
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Creates a task with the given description and saved status.
     *
     * @param description Text describing the task.
     * @param status Saved completion status.
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
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return The status icon.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns this task in Serina's simple save-file format.
     *
     * @return The serialized task.
     */
    public String toFileString() {
        return getTaskType().getIcon() + " | " + status.getFileValue() + " | " + escapeFileField(description)
                + getFileDetails();
    }

    /**
     * Returns the type of this task.
     *
     * @return The task type.
     */
    protected abstract TaskType getTaskType();

    /**
     * Returns extra details to display after the task description.
     *
     * @return Display details, or an empty string when there are none.
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns extra fields to append when saving this task to disk.
     *
     * @return Serialized detail fields, or an empty string when there are none.
     */
    protected String getFileDetails() {
        return "";
    }

    /**
     * Returns whether this task happens on the given date.
     *
     * @param date Date to check.
     * @return {@code true} if this task occurs on {@code date}.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Escapes characters that have special meaning in Serina's save-file format.
     *
     * @param field Field to escape.
     * @return The escaped field.
     */
    protected static String escapeFileField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task's type, status, description, and type-specific display details.
     *
     * @return The formatted task text shown to the user.
     */
    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description + getDetails();
    }
}
