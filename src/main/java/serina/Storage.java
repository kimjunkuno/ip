package serina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving Serina's tasks on the hard disk.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "serina.txt");
    private static final int MAX_TASKS = 100;

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

    private static List<String> toFileLines(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }
        return lines;
    }

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

    private static Event parseEvent(String description, String fromText, String toText, TaskStatus status)
            throws SerinaException {
        LocalDate from = DateParser.parseFileDate(fromText);
        LocalDate to = DateParser.parseFileDate(toText);
        if (to.isBefore(from)) {
            throw new SerinaException(SerinaError.LOAD_FAILED);
        }

        return new Event(description, from, to, status);
    }

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

    private static boolean isEscapedCharacter(char character) {
        return character == '\\' || character == '|';
    }
}
