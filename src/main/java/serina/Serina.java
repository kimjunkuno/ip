package serina;

import java.time.LocalDate;
import java.util.List;

import serina.exception.SerinaError;
import serina.exception.SerinaException;
import serina.parser.DateParser;
import serina.storage.Storage;
import serina.task.Deadline;
import serina.task.Event;
import serina.task.Task;
import serina.task.TaskList;
import serina.task.Todo;
import serina.ui.Ui;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            ui.showGreeting();
            TaskList tasks = loadTasks(ui);

            while (ui.hasNextCommand()) {
                String input = ui.readCommand();

                if (input.equals("bye")) {
                    ui.showGoodbye();
                    break;
                }

                try {
                    if (input.equals("help")) {
                        ui.showHelp();
                    } else if (input.equals("list")) {
                        ui.showList(tasks.asList());
                    } else if (input.equals("mark") || input.startsWith("mark ")) {
                        Task task = tasks.getTask(input.substring("mark".length()));
                        task.markAsDone();
                        Storage.saveTasks(tasks.asList());
                        ui.showMarkedTask(task);
                    } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                        Task task = tasks.getTask(input.substring("unmark".length()));
                        task.markAsNotDone();
                        Storage.saveTasks(tasks.asList());
                        ui.showUnmarkedTask(task);
                    } else if (input.equals("delete") || input.startsWith("delete ")) {
                        Task task = tasks.delete(input.substring("delete".length()));
                        Storage.saveTasks(tasks.asList());
                        ui.showDeletedTask(task, tasks.size());
                    } else if (input.equals("find") || input.startsWith("find ")) {
                        LocalDate date = parseFindDate(input.substring("find".length()));
                        List<Task> matchingTasks = tasks.find(date);
                        ui.showMatchingTasks(matchingTasks);
                    } else {
                        Task task = createTask(input);
                        tasks.add(task);
                        Storage.saveTasks(tasks.asList());
                        ui.showAddedTask(task, tasks.size());
                    }
                } catch (SerinaException e) {
                    ui.showMessage(e.getMessage());
                    if (e.shouldExit()) {
                        ui.showGoodbye();
                        break;
                    }
                }
            }
        }
    }

    private static TaskList loadTasks(Ui ui) {
        try {
            return new TaskList(Storage.loadTasks());
        } catch (SerinaException e) {
            ui.showMessage(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Creates the correct task type from the user's command.
     *
     * @throws SerinaException if the command is unknown or missing required fields
     */
    private static Task createTask(String input) throws SerinaException {
        if (input.equals("todo") || input.startsWith("todo ")) {
            return createTodo(input.substring("todo".length()));
        }

        if (input.equals("deadline") || input.startsWith("deadline ")) {
            return createDeadline(input.substring("deadline".length()));
        }

        if (input.equals("event") || input.startsWith("event ")) {
            return createEvent(input.substring("event".length()));
        }

        throw new SerinaException(SerinaError.UNKNOWN_COMMAND);
    }

    /**
     * Creates a todo task from the user's command text.
     */
    private static Todo createTodo(String input) throws SerinaException {
        String description = input.trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_TODO);
        }

        return new Todo(description);
    }

    /**
     * Creates a deadline task from text in the format {@code <task> /by <date>}.
     */
    private static Deadline createDeadline(String input) throws SerinaException {
        String commandText = input.trim();
        int byIndex = commandText.indexOf("/by");
        if (byIndex == -1) {
            throw new SerinaException(SerinaError.INVALID_DEADLINE_FORMAT);
        }

        String description = commandText.substring(0, byIndex).trim();
        String byText = commandText.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_DESCRIPTION);
        }
        if (byText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_BY);
        }

        LocalDate by = DateParser.parseInputDate(byText);
        return new Deadline(description, by);
    }

    /**
     * Creates an event task from text in the format {@code <task> /from <start date> /to <end date>}.
     */
    private static Event createEvent(String input) throws SerinaException {
        String commandText = input.trim();
        int fromIndex = commandText.indexOf("/from");
        int toIndex = commandText.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new SerinaException(SerinaError.INVALID_EVENT_FORMAT);
        }

        String description = commandText.substring(0, fromIndex).trim();
        String fromText = commandText.substring(fromIndex + "/from".length(), toIndex).trim();
        String toText = commandText.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_DESCRIPTION);
        }
        if (fromText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_FROM);
        }
        if (toText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_TO);
        }

        LocalDate from = DateParser.parseInputDate(fromText);
        LocalDate to = DateParser.parseInputDate(toText);
        if (to.isBefore(from)) {
            throw new SerinaException(SerinaError.INVALID_EVENT_DATE_RANGE);
        }

        return new Event(description, from, to);
    }

    /**
     * Parses the date entered in a find command.
     *
     * @throws SerinaException if the date is empty or not in the expected format
     */
    private static LocalDate parseFindDate(String dateText) throws SerinaException {
        String trimmedDateText = dateText.trim();
        if (trimmedDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_FIND_DATE);
        }

        return DateParser.parseInputDate(trimmedDateText);
    }

}
