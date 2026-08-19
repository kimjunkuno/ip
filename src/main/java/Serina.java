import java.util.Scanner;

/**
 * Runs Serina, a simple chatbot that stores tasks until the user says bye.
 */
public class Serina {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";
    private static final int MAX_TASKS = 100;
    private static final String MAX_TASKS_MESSAGE = "You've reached the maximum number of tasks.";

    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String[] tasks = new String[MAX_TASKS];
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
                    } else {
                        taskCount = addTask(tasks, taskCount, input);
                        showAddedTask(input);
                    }
                } catch (TaskLimitException e) {
                    showMessage(e.getMessage());
                    showGoodbye();
                    break;
                }
            }
        }
    }

    /**
     * Stores a task and returns the updated number of stored tasks.
     *
     * @throws TaskLimitException if Serina has no more storage space for tasks
     */
    private static int addTask(String[] tasks, int taskCount, String task) throws TaskLimitException {
        if (taskCount >= MAX_TASKS) {
            throw new TaskLimitException(MAX_TASKS_MESSAGE);
        }

        tasks[taskCount] = task;
        return taskCount + 1;
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

    private static void showMessage(String message) {
        printLine();
        System.out.println(MESSAGE_PREFIX + message);
        printLine();
    }

    private static void showList(String[] tasks, int taskCount) {
        printLine();
        for (int i = 0; i < taskCount; i++) {
            System.out.println(MESSAGE_PREFIX + (i + 1) + ". " + tasks[i]);
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
}
