package serina.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Stores Serina's tasks and provides operations that act on the task collection.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks, typically loaded from storage
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     * @throws SerinaException if the task list has reached its maximum size
     */
    public void add(Task task) throws SerinaException {
        if (tasks.size() >= MAX_TASKS) {
            throw new SerinaException(SerinaError.MAX_TASKS);
        }

        tasks.add(task);
    }

    /**
     * Returns the task matching a one-based task number entered by the user.
     *
     * @param taskNumberText one-based task number entered by the user
     * @return the matching task
     * @throws SerinaException if the given text is not a valid stored task number
     */
    public Task getTask(String taskNumberText) throws SerinaException {
        return tasks.get(getTaskIndex(taskNumberText));
    }

    /**
     * Removes and returns the task matching a one-based task number.
     *
     * @param taskNumberText one-based task number entered by the user
     * @return the removed task
     * @throws SerinaException if the given text is not a valid stored task number
     */
    public Task delete(String taskNumberText) throws SerinaException {
        return tasks.remove(getTaskIndex(taskNumberText));
    }

    /**
     * Returns tasks that occur on the given date.
     *
     * @param date date to match
     * @return tasks occurring on {@code date}
     */
    public List<Task> find(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOccurringOn(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the current number of tasks.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the tasks for display and persistence.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Converts a one-based user task number into a valid zero-based list index.
     *
     * @param taskNumberText task number entered by the user
     * @return the corresponding zero-based index
     * @throws SerinaException if the text is not an integer or refers to a missing task
     */
    private int getTaskIndex(String taskNumberText) throws SerinaException {
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
