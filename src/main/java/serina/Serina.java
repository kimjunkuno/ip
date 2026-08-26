package serina;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    private static final int MAX_TASKS = 100;

    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            ui.showGreeting();
            List<Task> tasks = loadTasks(ui);

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
                        ui.showList(tasks);
                    } else if (input.equals("mark") || input.startsWith("mark ")) {
                        Task task = getTask(tasks, input.substring("mark".length()));
                        task.markAsDone();
                        Storage.saveTasks(tasks);
                        ui.showMarkedTask(task);
                    } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                        Task task = getTask(tasks, input.substring("unmark".length()));
                        task.markAsNotDone();
                        Storage.saveTasks(tasks);
                        ui.showUnmarkedTask(task);
                    } else if (input.equals("delete") || input.startsWith("delete ")) {
                        Task task = deleteTask(tasks, input.substring("delete".length()));
                        Storage.saveTasks(tasks);
                        ui.showDeletedTask(task, tasks.size());
                    } else if (input.equals("find") || input.startsWith("find ")) {
                        List<Task> matchingTasks = findTasks(tasks, input.substring("find".length()));
                        ui.showMatchingTasks(matchingTasks);
                    } else {
                        Task task = createTask(input);
                        addTask(tasks, task);
                        Storage.saveTasks(tasks);
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

    private static List<Task> loadTasks(Ui ui) {
        try {
            return Storage.loadTasks();
        } catch (SerinaException e) {
            ui.showMessage(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Stores a task in the task list.
     *
     * @throws SerinaException if Serina has no more storage space for tasks
     */
    private static void addTask(List<Task> tasks, Task task) throws SerinaException {
        if (tasks.size() >= MAX_TASKS) {
            throw new SerinaException(SerinaError.MAX_TASKS);
        }

        tasks.add(task);
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
     * Returns the task matching the user's one-based task number.
     *
     * @throws SerinaException if the given text is not a valid stored task number
     */
    private static Task getTask(List<Task> tasks, String taskNumberText) throws SerinaException {
        return tasks.get(getTaskIndex(tasks, taskNumberText));
    }

    /**
     * Removes and returns the task matching the user's one-based task number.
     *
     * @throws SerinaException if the given text is not a valid stored task number
     */
    private static Task deleteTask(List<Task> tasks, String taskNumberText) throws SerinaException {
        return tasks.remove(getTaskIndex(tasks, taskNumberText));
    }

    /**
     * Returns tasks that happen on the date entered by the user.
     *
     * @throws SerinaException if the date is empty or not in the expected format
     */
    private static List<Task> findTasks(List<Task> tasks, String dateText) throws SerinaException {
        String trimmedDateText = dateText.trim();
        if (trimmedDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_FIND_DATE);
        }

        LocalDate date = DateParser.parseInputDate(trimmedDateText);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOccurringOn(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Converts the user's one-based task number to a zero-based list index.
     *
     * @throws SerinaException if the given text is not a valid stored task number
     */
    private static int getTaskIndex(List<Task> tasks, String taskNumberText) throws SerinaException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText.trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new SerinaException(SerinaError.INVALID_TASK_NUMBER);
            }

            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new SerinaException(SerinaError.INVALID_TASK_NUMBER);
        }
    }

}
