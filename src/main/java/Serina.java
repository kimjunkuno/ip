import java.util.Scanner;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";
    private static final int MAX_TASKS = 100;
    private static final String MAX_TASKS_MESSAGE = "You've reached the maximum number of tasks.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sorry captain, could you rephrase that for me?";
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Sorry captain, please provide a valid task number.";
    private static final String EMPTY_TODO_MESSAGE = "Sorry captain, todo descriptions can't be empty.";
    private static final String EMPTY_DEADLINE_DESCRIPTION_MESSAGE =
            "Sorry captain, deadline descriptions can't be empty.";
    private static final String EMPTY_DEADLINE_BY_MESSAGE = "Sorry captain, deadlines need a /by time.";
    private static final String EMPTY_EVENT_DESCRIPTION_MESSAGE = "Sorry captain, event descriptions can't be empty.";
    private static final String EMPTY_EVENT_FROM_MESSAGE = "Sorry captain, events need a /from time.";
    private static final String EMPTY_EVENT_TO_MESSAGE = "Sorry captain, events need a /to time.";
    private static final String INVALID_DEADLINE_FORMAT_MESSAGE =
            "Sorry captain, please use: deadline <task> /by <time>";
    private static final String INVALID_EVENT_FORMAT_MESSAGE =
            "Sorry captain, please use: event <task> /from <start> /to <end>";

    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Task[] tasks = new Task[MAX_TASKS];
            int taskCount = 0;

            showGreeting();

            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();

                if (input.equals("bye")) {
                    showGoodbye();
                    break;
                }

                try {
                    if (input.equals("list")) {
                        showList(tasks, taskCount);
                    } else if (input.equals("mark") || input.startsWith("mark ")) {
                        Task task = getTask(tasks, taskCount, input.substring("mark".length()));
                        task.markAsDone();
                        showMarkedTask(task);
                    } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                        Task task = getTask(tasks, taskCount, input.substring("unmark".length()));
                        task.markAsNotDone();
                        showUnmarkedTask(task);
                    } else {
                        Task task = createTask(input);
                        taskCount = addTask(tasks, taskCount, task);
                        showAddedTask(task, taskCount);
                    }
                } catch (TaskLimitException e) {
                    showMessage(e.getMessage());
                    showGoodbye();
                    break;
                } catch (SerinaException e) {
                    showMessage(e.getMessage());
                }
            }
        }
    }

    /**
     * Stores a task and returns the updated number of stored tasks.
     *
     * @throws TaskLimitException if Serina has no more storage space for tasks
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) throws TaskLimitException {
        if (taskCount >= MAX_TASKS) {
            throw new TaskLimitException(MAX_TASKS_MESSAGE);
        }

        tasks[taskCount] = task;
        return taskCount + 1;
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

        throw new SerinaException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Creates a todo task from the user's command text.
     */
    private static Todo createTodo(String input) throws SerinaException {
        String description = input.trim();
        if (description.isEmpty()) {
            throw new SerinaException(EMPTY_TODO_MESSAGE);
        }

        return new Todo(description);
    }

    /**
     * Creates a deadline task from text in the format {@code <task> /by <time>}.
     */
    private static Deadline createDeadline(String input) throws SerinaException {
        String commandText = input.trim();
        int byIndex = commandText.indexOf("/by");
        if (byIndex == -1) {
            throw new SerinaException(INVALID_DEADLINE_FORMAT_MESSAGE);
        }

        String description = commandText.substring(0, byIndex).trim();
        String by = commandText.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(EMPTY_DEADLINE_DESCRIPTION_MESSAGE);
        }
        if (by.isEmpty()) {
            throw new SerinaException(EMPTY_DEADLINE_BY_MESSAGE);
        }

        return new Deadline(description, by);
    }

    /**
     * Creates an event task from text in the format {@code <task> /from <start> /to <end>}.
     */
    private static Event createEvent(String input) throws SerinaException {
        String commandText = input.trim();
        int fromIndex = commandText.indexOf("/from");
        int toIndex = commandText.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new SerinaException(INVALID_EVENT_FORMAT_MESSAGE);
        }

        String description = commandText.substring(0, fromIndex).trim();
        String from = commandText.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = commandText.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(EMPTY_EVENT_DESCRIPTION_MESSAGE);
        }
        if (from.isEmpty()) {
            throw new SerinaException(EMPTY_EVENT_FROM_MESSAGE);
        }
        if (to.isEmpty()) {
            throw new SerinaException(EMPTY_EVENT_TO_MESSAGE);
        }

        return new Event(description, from, to);
    }

    /**
     * Returns the task matching the user's one-based task number.
     *
     * @throws SerinaException if the given text is not a valid stored task number
     */
    private static Task getTask(Task[] tasks, int taskCount, String taskNumberText) throws SerinaException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText.trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new SerinaException(INVALID_TASK_NUMBER_MESSAGE);
            }

            return tasks[taskNumber - 1];
        } catch (NumberFormatException e) {
            throw new SerinaException(INVALID_TASK_NUMBER_MESSAGE);
        }
    }

    private static void showGreeting() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Hello! I'm Serina");
        System.out.println(MESSAGE_PREFIX + "What can I do for you?");
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

    private static void showMessage(String message) {
        printLine();
        System.out.println(MESSAGE_PREFIX + message);
        printLine();
    }

    private static void showList(Task[] tasks, int taskCount) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(MESSAGE_PREFIX + (i + 1) + "." + tasks[i]);
        }
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }

    /**
     * Signals that Serina cannot store any more tasks in memory.
     */
    private static class TaskLimitException extends SerinaException {
        private TaskLimitException(String message) {
            super(message);
        }
    }
}
