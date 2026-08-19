import java.util.Scanner;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";
    private static final int MAX_TASKS = 100;
    private static final String MAX_TASKS_MESSAGE = "You've reached the maximum number of tasks.";
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Please provide a valid task number.";
    private static final String INVALID_DEADLINE_FORMAT_MESSAGE = "Please use: deadline <task> /by <time>";
    private static final String INVALID_EVENT_FORMAT_MESSAGE = "Please use: event <task> /from <start> /to <end>";

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
                String input = scanner.nextLine();

                if (input.equals("bye")) {
                    showGoodbye();
                    break;
                }

                try {
                    if (input.equals("list")) {
                        showList(tasks, taskCount);
                    } else if (input.startsWith("mark ")) {
                        Task task = getTask(tasks, taskCount, input.substring("mark ".length()));
                        task.markAsDone();
                        showMarkedTask(task);
                    } else if (input.startsWith("unmark ")) {
                        Task task = getTask(tasks, taskCount, input.substring("unmark ".length()));
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
                } catch (InvalidTaskNumberException e) {
                    showMessage(e.getMessage());
                } catch (InvalidTaskFormatException e) {
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
     * @throws InvalidTaskFormatException if a deadline or event command is missing its required fields
     */
    private static Task createTask(String input) throws InvalidTaskFormatException {
        if (input.startsWith("todo ")) {
            return new Todo(input.substring("todo ".length()));
        }

        if (input.startsWith("deadline ")) {
            return createDeadline(input.substring("deadline ".length()));
        }

        if (input.startsWith("event ")) {
            return createEvent(input.substring("event ".length()));
        }

        return new Todo(input);
    }

    /**
     * Creates a deadline task from text in the format {@code <task> /by <time>}.
     */
    private static Deadline createDeadline(String input) throws InvalidTaskFormatException {
        int byIndex = input.indexOf(" /by ");
        if (byIndex == -1) {
            throw new InvalidTaskFormatException(INVALID_DEADLINE_FORMAT_MESSAGE);
        }

        String description = input.substring(0, byIndex);
        String by = input.substring(byIndex + " /by ".length());
        if (description.isEmpty() || by.isEmpty()) {
            throw new InvalidTaskFormatException(INVALID_DEADLINE_FORMAT_MESSAGE);
        }

        return new Deadline(description, by);
    }

    /**
     * Creates an event task from text in the format {@code <task> /from <start> /to <end>}.
     */
    private static Event createEvent(String input) throws InvalidTaskFormatException {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new InvalidTaskFormatException(INVALID_EVENT_FORMAT_MESSAGE);
        }

        String description = input.substring(0, fromIndex);
        String from = input.substring(fromIndex + " /from ".length(), toIndex);
        String to = input.substring(toIndex + " /to ".length());
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new InvalidTaskFormatException(INVALID_EVENT_FORMAT_MESSAGE);
        }

        return new Event(description, from, to);
    }

    /**
     * Returns the task matching the user's one-based task number.
     *
     * @throws InvalidTaskNumberException if the given text is not a valid stored task number
     */
    private static Task getTask(Task[] tasks, int taskCount, String taskNumberText) throws InvalidTaskNumberException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText.trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new InvalidTaskNumberException(INVALID_TASK_NUMBER_MESSAGE);
            }

            return tasks[taskNumber - 1];
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException(INVALID_TASK_NUMBER_MESSAGE);
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
    private static class TaskLimitException extends Exception {
        private TaskLimitException(String message) {
            super(message);
        }
    }

    /**
     * Signals that the user has entered an invalid task number.
     */
    private static class InvalidTaskNumberException extends Exception {
        private InvalidTaskNumberException(String message) {
            super(message);
        }
    }

    /**
     * Signals that the user has entered a task command in an invalid format.
     */
    private static class InvalidTaskFormatException extends Exception {
        private InvalidTaskFormatException(String message) {
            super(message);
        }
    }
}
