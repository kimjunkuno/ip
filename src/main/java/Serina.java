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
                        taskCount = addTask(tasks, taskCount, input);
                        showAddedTask(input);
                    }
                } catch (TaskLimitException e) {
                    showMessage(e.getMessage());
                    showGoodbye();
                    break;
                } catch (InvalidTaskNumberException e) {
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
    private static int addTask(Task[] tasks, int taskCount, String taskDescription) throws TaskLimitException {
        if (taskCount >= MAX_TASKS) {
            throw new TaskLimitException(MAX_TASKS_MESSAGE);
        }

        tasks[taskCount] = new Task(taskDescription);
        return taskCount + 1;
    }

    /**
     * Returns the task matching the user's one-based task number.
     *
     * @throws InvalidTaskNumberException if the given text is not a valid stored task number
     */
    private static Task getTask(Task[] tasks, int taskCount, String taskNumberText) throws InvalidTaskNumberException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
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

    private static void showAddedTask(String task) {
        showMessage("added: " + task);
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
}
