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
 * Processes Serina commands for console and graphical user interfaces.
 */
public class Serina {
    private final Storage storage;
    private final TaskList tasks;
    private final List<String> startupMessages;

    /**
     * Creates Serina using the default save-file location.
     */
    public Serina() {
        this(new Storage());
    }

    /**
     * Creates Serina using the supplied storage location.
     *
     * @param storage Storage used to load and save tasks.
     */
    public Serina(Storage storage) {
        this.storage = storage;

        TaskList loadedTasks;
        List<String> loadingMessages;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
            loadingMessages = List.of();
        } catch (SerinaException e) {
            loadedTasks = new TaskList();
            loadingMessages = List.of(e.getMessage());
        }

        tasks = loadedTasks;
        startupMessages = loadingMessages;
    }

    /**
     * Starts Serina's command-line interface.
     *
     * @param args Command line arguments, which are not used.
     */
    public static void main(String[] args) {
        Serina serina = new Serina();

        try (Ui ui = new Ui()) {
            ui.showMessage(serina.getGreeting());
            for (String message : serina.getStartupMessages()) {
                ui.showMessage(message);
            }

            while (ui.hasNextCommand()) {
                CommandResult result = serina.executeCommand(ui.readCommand());
                for (String response : result.getResponses()) {
                    ui.showMessage(response);
                }
                if (result.shouldExit()) {
                    break;
                }
            }
        }
    }

    /**
     * Returns Serina's greeting.
     *
     * @return Greeting shown when a conversation starts.
     */
    public String getGreeting() {
        return ResponseFormatter.formatGreeting();
    }

    /**
     * Returns messages produced while loading saved tasks.
     *
     * @return Loading warnings in display order.
     */
    public List<String> getStartupMessages() {
        return startupMessages;
    }

    /**
     * Processes one command and returns the responses to display.
     *
     * @param input Command entered by the user.
     * @return Responses and exit behavior produced by the command.
     */
    public CommandResult executeCommand(String input) {
        String command = input.trim();
        if (command.equals("bye")) {
            return new CommandResult(List.of(ResponseFormatter.formatGoodbye()), true);
        }

        try {
            String response = processCommand(command);
            return new CommandResult(List.of(response), false);
        } catch (SerinaException e) {
            if (e.shouldExit()) {
                return new CommandResult(List.of(e.getMessage(), ResponseFormatter.formatGoodbye()), true);
            }
            return new CommandResult(List.of(e.getMessage()), false);
        }
    }

    /**
     * Executes a command that does not directly end the conversation.
     */
    private String processCommand(String input) throws SerinaException {
        if (input.equals("help")) {
            return ResponseFormatter.formatHelp();
        }
        if (input.equals("list")) {
            return ResponseFormatter.formatTaskList(tasks.asList());
        }
        if (input.equals("mark") || input.startsWith("mark ")) {
            Task task = tasks.getTask(input.substring("mark".length()));
            task.markAsDone();
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatMarkedTask(task);
        }
        if (input.equals("unmark") || input.startsWith("unmark ")) {
            Task task = tasks.getTask(input.substring("unmark".length()));
            task.markAsNotDone();
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatUnmarkedTask(task);
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            Task task = tasks.delete(input.substring("delete".length()));
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatDeletedTask(task, tasks.size());
        }
        if (input.equals("find") || input.startsWith("find ")) {
            String keyword = parseFindKeyword(input.substring("find".length()));
            return ResponseFormatter.formatMatchingTasks(tasks.find(keyword));
        }

        Task task = createTask(input);
        tasks.add(task);
        storage.saveTasks(tasks.asList());
        return ResponseFormatter.formatAddedTask(task, tasks.size());
    }

    /**
     * Creates the correct task type from the user's command.
     *
     * @throws SerinaException If the command is unknown or missing required fields.
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
        String deadlineDateText = commandText.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_DESCRIPTION);
        }
        if (deadlineDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_BY);
        }

        LocalDate deadlineDate = DateParser.parseInputDate(deadlineDateText);
        return new Deadline(description, deadlineDate);
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
        String startDateText = commandText.substring(fromIndex + "/from".length(), toIndex).trim();
        String endDateText = commandText.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_DESCRIPTION);
        }
        if (startDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_FROM);
        }
        if (endDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_TO);
        }

        LocalDate startDate = DateParser.parseInputDate(startDateText);
        LocalDate endDate = DateParser.parseInputDate(endDateText);
        if (endDate.isBefore(startDate)) {
            throw new SerinaException(SerinaError.INVALID_EVENT_DATE_RANGE);
        }

        return new Event(description, startDate, endDate);
    }

    /**
     * Returns the keyword entered in a find command.
     *
     * @param keywordText Keyword text from the user command.
     * @return The trimmed keyword.
     * @throws SerinaException If the keyword is empty.
     */
    private static String parseFindKeyword(String keywordText) throws SerinaException {
        String keyword = keywordText.trim();
        if (keyword.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_FIND_KEYWORD);
        }

        return keyword;
    }
}
