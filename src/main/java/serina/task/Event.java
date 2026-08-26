package serina.task;

import java.time.LocalDate;

import serina.parser.DateParser;

/**
 * Represents a task that happens from a start date or time to an end date or time.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description text describing the task
     * @param from start date or time
     * @param to end date or time
     */
    public Event(String description, LocalDate from, LocalDate to) {
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
    public Event(String description, LocalDate from, LocalDate to, TaskStatus status) {
        super(description, status);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event task type.
     *
     * @return {@link TaskType#EVENT}
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the event's start and end dates formatted for display.
     *
     * @return display text containing the event date range
     */
    @Override
    protected String getDetails() {
        return " (from: " + DateParser.formatDisplayDate(from)
                + " to: " + DateParser.formatDisplayDate(to) + ")";
    }

    /**
     * Returns the event date fields used in the save file.
     *
     * @return serialized event details
     */
    @Override
    protected String getFileDetails() {
        return " | " + DateParser.formatFileDate(from) + " | " + DateParser.formatFileDate(to);
    }

    /**
     * Checks whether the given date falls within this event's inclusive date range.
     *
     * @param date date to check
     * @return {@code true} if {@code date} is between the start and end dates, inclusive
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
