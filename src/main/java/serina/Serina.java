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
    private static final String COMMAND_ARGUMENT_SEPARATOR = " ";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String DEADLINE_DATE_SEPARATOR = "/by";
    private static final String EVENT_START_SEPARATOR = "/from";
    private static final String EVENT_END_SEPARATOR = "/to";

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
        assert input != null : "Commands should come from the UI as non-null text.";

        String command = input.trim();
        if (command.equals(COMMAND_BYE)) {
            return new CommandResult(true, ResponseFormatter.formatGoodbye());
        }

        try {
            String response = processCommand(command);
            return new CommandResult(false, response);
        } catch (SerinaException e) {
            if (e.shouldExit()) {
                return new CommandResult(true, e.getMessage(), ResponseFormatter.formatGoodbye());
            }
            return new CommandResult(false, e.getMessage());
        }
    }

    /**
     * Executes a command that does not directly end the conversation.
     */
    private String processCommand(String input) throws SerinaException {
        assert input.equals(input.trim()) : "Commands should be trimmed before they are dispatched.";

        if (input.equals(COMMAND_HELP)) {
            return ResponseFormatter.formatHelp();
        }
        if (input.equals(COMMAND_LIST)) {
            return ResponseFormatter.formatTaskList(tasks.asList());
        }
        if (isCommand(input, COMMAND_MARK)) {
            Task task = tasks.getTask(getCommandArguments(input, COMMAND_MARK));
            task.markAsDone();
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatMarkedTask(task);
        }
        if (isCommand(input, COMMAND_UNMARK)) {
            Task task = tasks.getTask(getCommandArguments(input, COMMAND_UNMARK));
            task.markAsNotDone();
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatUnmarkedTask(task);
        }
        if (isCommand(input, COMMAND_DELETE)) {
            Task task = tasks.delete(getCommandArguments(input, COMMAND_DELETE));
            storage.saveTasks(tasks.asList());
            return ResponseFormatter.formatDeletedTask(task, tasks.size());
        }
        if (isCommand(input, COMMAND_FIND)) {
            String keyword = parseFindKeyword(getCommandArguments(input, COMMAND_FIND));
            assert !keyword.isBlank() : "Find keywords should be validated before searching.";
            return ResponseFormatter.formatMatchingTasks(tasks.find(keyword));
        }

        Task task = createTask(input);
        assert task != null : "Task creation should return a task or throw a SerinaException.";
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
        if (isCommand(input, COMMAND_TODO)) {
            return createTodo(getCommandArguments(input, COMMAND_TODO));
        }

        if (isCommand(input, COMMAND_DEADLINE)) {
            return createDeadline(getCommandArguments(input, COMMAND_DEADLINE));
        }

        if (isCommand(input, COMMAND_EVENT)) {
            return createEvent(getCommandArguments(input, COMMAND_EVENT));
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

        assert !description.isBlank() : "Todo descriptions should be validated before task construction.";
        return new Todo(description);
    }

    /**
     * Creates a deadline task from text in the format {@code <task> /by <date>}.
     */
    private static Deadline createDeadline(String input) throws SerinaException {
        String commandText = input.trim();
        int byIndex = commandText.indexOf(DEADLINE_DATE_SEPARATOR);
        if (byIndex == -1) {
            throw new SerinaException(SerinaError.INVALID_DEADLINE_FORMAT);
        }
        assert byIndex >= 0 : "Deadline commands should be sliced only after /by is found.";

        String description = commandText.substring(0, byIndex).trim();
        String deadlineDateText = commandText.substring(byIndex + DEADLINE_DATE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_DESCRIPTION);
        }
        if (deadlineDateText.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_BY);
        }

        LocalDate deadlineDate = DateParser.parseInputDate(deadlineDateText);
        assert !description.isBlank() : "Deadline descriptions should be validated before task construction.";
        assert !deadlineDateText.isBlank() : "Deadline dates should be validated before date parsing.";
        assert deadlineDate != null : "Date parsing should return a deadline date or throw a SerinaException.";
        return new Deadline(description, deadlineDate);
    }

    /**
     * Creates an event task from text in the format {@code <task> /from <start date> /to <end date>}.
     */
    private static Event createEvent(String input) throws SerinaException {
        String commandText = input.trim();
        int fromIndex = commandText.indexOf(EVENT_START_SEPARATOR);
        int toIndex = commandText.indexOf(EVENT_END_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new SerinaException(SerinaError.INVALID_EVENT_FORMAT);
        }
        assert fromIndex >= 0 && toIndex > fromIndex
                : "Event commands should be sliced only after /from and /to are found in order.";

        String description = commandText.substring(0, fromIndex).trim();
        String startDateText = commandText.substring(fromIndex + EVENT_START_SEPARATOR.length(), toIndex).trim();
        String endDateText = commandText.substring(toIndex + EVENT_END_SEPARATOR.length()).trim();
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
        assert !description.isBlank() : "Event descriptions should be validated before task construction.";
        assert !startDateText.isBlank() : "Event start dates should be validated before date parsing.";
        assert !endDateText.isBlank() : "Event end dates should be validated before date parsing.";
        if (endDate.isBefore(startDate)) {
            throw new SerinaException(SerinaError.INVALID_EVENT_DATE_RANGE);
        }

        assert !endDate.isBefore(startDate) : "Events should be chronological before task construction.";
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

        assert !keyword.isBlank() : "Find keyword parsing should reject blank keywords.";
        return keyword;
    }

    /**
     * Returns whether the input is exactly the command word or starts with that command followed by arguments.
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + COMMAND_ARGUMENT_SEPARATOR);
    }

    /**
     * Returns the text after the command word.
     */
    private static String getCommandArguments(String input, String commandWord) {
        assert isCommand(input, commandWord) : "Command arguments should be extracted only from matching commands.";

        return input.substring(commandWord.length());
    }
}
