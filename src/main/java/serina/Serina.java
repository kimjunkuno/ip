package serina;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";
    private static final int MAX_TASKS = 100;

    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            showGreeting();
            List<Task> tasks = loadTasks();

            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();

                if (input.equals("bye")) {
                    showGoodbye();
                    break;
                }

                try {
                    if (input.equals("help")) {
                        showHelp();
                    } else if (input.equals("list")) {
                        showList(tasks);
                    } else if (input.equals("mark") || input.startsWith("mark ")) {
                        Task task = getTask(tasks, input.substring("mark".length()));
                        task.markAsDone();
                        Storage.saveTasks(tasks);
                        showMarkedTask(task);
                    } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                        Task task = getTask(tasks, input.substring("unmark".length()));
                        task.markAsNotDone();
                        Storage.saveTasks(tasks);
                        showUnmarkedTask(task);
                    } else if (input.equals("delete") || input.startsWith("delete ")) {
                        Task task = deleteTask(tasks, input.substring("delete".length()));
                        Storage.saveTasks(tasks);
                        showDeletedTask(task, tasks.size());
                    } else if (input.equals("find") || input.startsWith("find ")) {
                        List<Task> matchingTasks = findTasks(tasks, input.substring("find".length()));
                        showMatchingTasks(matchingTasks);
                    } else {
                        Task task = createTask(input);
                        addTask(tasks, task);
                        Storage.saveTasks(tasks);
                        showAddedTask(task, tasks.size());
                    }
                } catch (SerinaException e) {
                    showMessage(e.getMessage());
                    if (e.shouldExit()) {
                        showGoodbye();
                        break;
                    }
                }
            }
        }
    }

    private static List<Task> loadTasks() {
        try {
            return Storage.loadTasks();
        } catch (SerinaException e) {
            showMessage(e.getMessage());
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

    private static void showGreeting() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Hello! I'm Serina");
        System.out.println(MESSAGE_PREFIX + "What can I do for you?");
        System.out.println(MESSAGE_PREFIX + "Type help to see the available commands.");
        printLine();
    }

    private static void showHelp() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Here are the commands I can respond to:");
        System.out.println(MESSAGE_PREFIX + "help - show this command list");
        System.out.println(MESSAGE_PREFIX + "todo <task> - add a todo");
        System.out.println(MESSAGE_PREFIX + "deadline <task> /by <yyyy-MM-dd> - add a deadline");
        System.out.println(MESSAGE_PREFIX
                + "event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event");
        System.out.println(MESSAGE_PREFIX + "list - show all tasks");
        System.out.println(MESSAGE_PREFIX + "mark <number> - mark a task as done");
        System.out.println(MESSAGE_PREFIX + "unmark <number> - mark a task as not done");
        System.out.println(MESSAGE_PREFIX + "delete <number> - remove a task");
        System.out.println(MESSAGE_PREFIX + "find <yyyy-MM-dd> - find tasks occurring on a date");
        System.out.println(MESSAGE_PREFIX + "bye - exit Serina");
        printLine();
    }

    private static void showGoodbye() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Bye. Hope to see you again soon!");
        printLine();
    }

    private static void showAddedTask(Task task, int taskCount) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Got it. I've added this task:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        System.out.println(MESSAGE_PREFIX + "Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    private static void showMarkedTask(Task task) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Nice! I've marked this task as done:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        printLine();
    }

    private static void showUnmarkedTask(Task task) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "OK, I've marked this task as not done yet:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        printLine();
    }

    private static void showDeletedTask(Task task, int taskCount) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Noted. I've removed this task:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        System.out.println(MESSAGE_PREFIX + "Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    private static void showMessage(String message) {
        printLine();
        System.out.println(MESSAGE_PREFIX + message);
        printLine();
    }

    private static void showList(List<Task> tasks) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(MESSAGE_PREFIX + (i + 1) + "." + tasks.get(i));
        }
        printLine();
    }

    private static void showMatchingTasks(List<Task> tasks) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(MESSAGE_PREFIX + (i + 1) + "." + tasks.get(i));
        }
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }
}
