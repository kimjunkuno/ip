package serina.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import serina.exception.SerinaError;
import serina.exception.SerinaException;
import serina.parser.DateParser;
import serina.task.Deadline;
import serina.task.Event;
import serina.task.Task;
import serina.task.TaskStatus;
import serina.task.TaskType;
import serina.task.Todo;

/**
 * Handles loading and saving Serina's tasks on the hard disk.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "serina.txt");
    private static final int MAX_TASKS = 100;

    /**
     * Prevents instantiation of this static storage utility class.
     */
    private Storage() {
    }

    /**
     * Loads tasks from Serina's save file.
     *
     * @return saved tasks, or an empty list if there is no save file yet
     * @throws SerinaException if Serina is unable to read the save file
     */
    public static List<Task> loadTasks() throws SerinaException {
        try {
            if (!Files.exists(FILE_PATH)) {
                return new ArrayList<>();
            }

            List<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (tasks.size() >= MAX_TASKS) {
                    throw new SerinaException(SerinaError.LOAD_TOO_MANY_TASKS);
                }
                tasks.add(parseTask(line));
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }
    }

    /**
     * Saves all current tasks to Serina's save file.
     *
     * @param tasks tasks to write to disk
     * @throws SerinaException if Serina is unable to create or write the save file
     */
    public static void saveTasks(List<Task> tasks) throws SerinaException {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.write(FILE_PATH, toFileLines(tasks));
        } catch (IOException | SecurityException e) {
            throw new SerinaException(SerinaError.SAVE_FAILED);
        }
    }

    /**
     * Converts tasks into the lines used by Serina's save-file format.
     *
     * @param tasks tasks to serialize
     * @return one serialized line for each task
     */
    private static List<String> toFileLines(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }
        return lines;
    }

    /**
     * Reconstructs a task from one line of the save file.
     *
     * @param line serialized task data
     * @return the reconstructed task
     * @throws SerinaException if the line does not follow the expected format
     */
    private static Task parseTask(String line) throws SerinaException {
        List<String> parts = splitFileLine(line);
        if (parts.size() < 3) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        TaskType type = TaskType.parseFileValue(parts.get(0));
        TaskStatus status = TaskStatus.parseFileValue(parts.get(1));
        String description = parts.get(2);
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        switch (type) {
            case TODO:
                if (parts.size() != 3) {
                    throw new SerinaException(SerinaError.LOAD_FAILED);
                }
                return new Todo(description, status);
            case DEADLINE:
                if (parts.size() != 4 || parts.get(3).isEmpty()) {
                    throw new SerinaException(SerinaError.LOAD_FAILED);
                }
                return new Deadline(description, DateParser.parseFileDate(parts.get(3)), status);
            case EVENT:
                if (parts.size() != 5 || parts.get(3).isEmpty() || parts.get(4).isEmpty()) {
                    throw new SerinaException(SerinaError.LOAD_FAILED);
                }
                return parseEvent(description, parts.get(3), parts.get(4), status);
            default:
                throw new SerinaException(SerinaError.LOAD_FAILED);
        }
    }

    /**
     * Reconstructs an event and validates that its end date is not before its start date.
     *
     * @param description event description
     * @param fromText event start date in save-file format
     * @param toText event end date in save-file format
     * @param status saved completion status
     * @return the reconstructed event
     * @throws SerinaException if either date is invalid or the date range is reversed
     */
    private static Event parseEvent(String description, String fromText, String toText, TaskStatus status)
            throws SerinaException {
        LocalDate from = DateParser.parseFileDate(fromText);
        LocalDate to = DateParser.parseFileDate(toText);
        if (to.isBefore(from)) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        return new Event(description, from, to, status);
    }

    /**
     * Splits a save-file line on unescaped delimiters and restores escaped characters.
     *
     * @param line serialized task data
     * @return the decoded fields in the line
     */
    private static List<String> splitFileLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\' && i + 1 < line.length() && isEscapedCharacter(line.charAt(i + 1))) {
                currentPart.append(line.charAt(i + 1));
                i++;
            } else if (current == '|') {
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0);
            } else {
                currentPart.append(current);
            }
        }

        parts.add(currentPart.toString().trim());
        return parts;
    }

    /**
     * Checks whether a character can be escaped in the save-file format.
     *
     * @param character character following an escape marker
     * @return {@code true} for a backslash or field delimiter
     */
    private static boolean isEscapedCharacter(char character) {
        return character == '\\' || character == '|';
    }
}
