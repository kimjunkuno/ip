package serina;

import java.util.List;
import java.util.Scanner;

/**
 * Handles console input and output for Serina.
 */
public class Ui implements AutoCloseable {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Serina's greeting and points users to the help command.
     */
    public void showGreeting() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Hello! I'm Serina");
        System.out.println(MESSAGE_PREFIX + "What can I do for you?");
        System.out.println(MESSAGE_PREFIX + "Type help to see the available commands.");
        printLine();
    }

    /**
     * Displays every command supported by Serina.
     */
    public void showHelp() {
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

    /**
     * Displays Serina's farewell message.
     */
    public void showGoodbye() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Bye. Hope to see you again soon!");
        printLine();
    }

    /**
     * Displays a newly added task and the updated task count.
     */
    public void showAddedTask(Task task, int taskCount) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Got it. I've added this task:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        System.out.println(MESSAGE_PREFIX + "Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    /**
     * Displays a task that was marked as done.
     */
    public void showMarkedTask(Task task) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Nice! I've marked this task as done:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        printLine();
    }

    /**
     * Displays a task that was marked as not done.
     */
    public void showUnmarkedTask(Task task) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "OK, I've marked this task as not done yet:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        printLine();
    }

    /**
     * Displays a deleted task and the updated task count.
     */
    public void showDeletedTask(Task task, int taskCount) {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Noted. I've removed this task:");
        System.out.println(MESSAGE_PREFIX + "  " + task);
        System.out.println(MESSAGE_PREFIX + "Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    /**
     * Displays a message in Serina's standard response box.
     */
    public void showMessage(String message) {
        printLine();
        System.out.println(MESSAGE_PREFIX + message);
        printLine();
    }

    /**
     * Displays all tasks with one-based numbering.
     */
    public void showList(List<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays tasks that match a search with one-based numbering.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    private void showNumberedTasks(String heading, List<Task> tasks) {
        printLine();
        System.out.println(MESSAGE_PREFIX + heading);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(MESSAGE_PREFIX + (i + 1) + "." + tasks.get(i));
        }
        printLine();
    }

    private void printLine() {
        System.out.println(LINE);
    }

    /**
     * Releases the scanner used to read console input.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
