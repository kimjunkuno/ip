package serina.task;

import java.time.LocalDate;

import serina.parser.DateParser;

/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description text describing the task
     * @param by deadline for the task
     */
    public Deadline(String description, LocalDate by) {
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
    public Deadline(String description, LocalDate by, TaskStatus status) {
        super(description, status);
        this.by = by;
    }

    /**
     * Returns the deadline task type.
     *
     * @return {@link TaskType#DEADLINE}
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the deadline date formatted for display.
     *
     * @return display text containing the deadline date
     */
    @Override
    protected String getDetails() {
        return " (by: " + DateParser.formatDisplayDate(by) + ")";
    }

    /**
     * Returns the deadline date field used in the save file.
     *
     * @return serialized deadline details
     */
    @Override
    protected String getFileDetails() {
        return " | " + DateParser.formatFileDate(by);
    }

    /**
     * Checks whether this task is due on the given date.
     *
     * @param date date to check
     * @return {@code true} if the deadline is on {@code date}
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        return by.equals(date);
    }
}
