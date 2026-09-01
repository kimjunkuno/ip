package serina;

import java.util.List;

import serina.task.Task;

/**
 * Builds the user-facing messages shared by Serina's console and graphical interfaces.
 */
final class ResponseFormatter {
    private static final String GREETING = String.join("\n",
            "Hello! I'm Serina",
            "What can I do for you?",
            "Type help to see the available commands.");
    private static final String HELP = String.join("\n",
            "Here are the commands I can respond to:",
            "help - show this command list",
            "todo <task> - add a todo",
            "deadline <task> /by <yyyy-MM-dd> - add a deadline",
            "event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event",
            "list - show all tasks",
            "mark <number> - mark a task as done",
            "unmark <number> - mark a task as not done",
            "delete <number> - remove a task",
            "find <keyword> - find tasks containing a keyword",
            "bye - exit Serina");
    private static final String GOODBYE = "Bye. Hope to see you again soon!";

    /**
     * Prevents instantiation of this response-formatting utility class.
     */
    private ResponseFormatter() {
    }

    /**
     * Returns Serina's greeting.
     *
     * @return Greeting shown when Serina starts.
     */
    static String formatGreeting() {
        return GREETING;
    }

    /**
     * Returns Serina's command reference.
     *
     * @return Help message containing every supported command.
     */
    static String formatHelp() {
        return HELP;
    }

    /**
     * Returns Serina's farewell message.
     *
     * @return Farewell shown when a conversation ends.
     */
    static String formatGoodbye() {
        return GOODBYE;
    }

    /**
     * Returns the message shown after adding a task.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     * @return Task-added response.
     */
    static String formatAddedTask(Task task, int taskCount) {
        return String.join("\n",
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Returns the message shown after marking a task as done.
     *
     * @param task Task that was marked.
     * @return Task-marked response.
     */
    static String formatMarkedTask(Task task) {
        return String.join("\n",
                "Nice! I've marked this task as done:",
                "  " + task);
    }

    /**
     * Returns the message shown after marking a task as not done.
     *
     * @param task Task that was unmarked.
     * @return Task-unmarked response.
     */
    static String formatUnmarkedTask(Task task) {
        return String.join("\n",
                "OK, I've marked this task as not done yet:",
                "  " + task);
    }

    /**
     * Returns the message shown after deleting a task.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks after the deletion.
     * @return Task-deleted response.
     */
    static String formatDeletedTask(Task task, int taskCount) {
        return String.join("\n",
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Returns all tasks with one-based numbering.
     *
     * @param tasks Tasks to display.
     * @return Numbered task-list response.
     */
    static String formatTaskList(List<Task> tasks) {
        return formatNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns matching tasks with one-based numbering.
     *
     * @param tasks Matching tasks to display.
     * @return Numbered matching-task response.
     */
    static String formatMatchingTasks(List<Task> tasks) {
        return formatNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Returns a heading followed by tasks numbered from one.
     */
    private static String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append('\n').append(i + 1).append('.').append(tasks.get(i));
        }
        return response.toString();
    }
}
