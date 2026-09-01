package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serina.storage.Storage;

/**
 * Tests Serina's shared command-processing behavior.
 */
public class SerinaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getGreeting_newConversation_returnsConsoleGreeting() {
        Serina serina = createSerina();

        assertEquals(String.join("\n",
                "Hello! I'm Serina",
                "What can I do for you?",
                "Type help to see the available commands."), serina.getGreeting());
    }

    @Test
    public void executeCommand_help_returnsExistingHelpText() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("help");

        assertEquals(List.of(String.join("\n",
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
                "bye - exit Serina")), result.getResponses());
        assertFalse(result.shouldExit());
    }

    @Test
    public void executeCommand_addListAndReload_preservesResponsesAndTaskData() {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Serina serina = new Serina(new Storage(saveFile));

        CommandResult addResult = serina.executeCommand("todo read book");
        CommandResult listResult = serina.executeCommand("list");
        Serina reloadedSerina = new Serina(new Storage(saveFile));
        CommandResult reloadedListResult = reloadedSerina.executeCommand("list");

        assertEquals(List.of(String.join("\n",
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list.")), addResult.getResponses());
        assertEquals(List.of(String.join("\n",
                "Here are the tasks in your list:",
                "1.[T][ ] read book")), listResult.getResponses());
        assertEquals(listResult.getResponses(), reloadedListResult.getResponses());
    }

    @Test
    public void executeCommand_unknownCommand_returnsExistingErrorAndContinues() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("nonsense");

        assertEquals(List.of(
                "Sorry captain, could you rephrase that for me? Type help to see the available commands."),
                result.getResponses());
        assertFalse(result.shouldExit());
    }

    @Test
    public void executeCommand_bye_returnsGoodbyeAndExits() {
        Serina serina = createSerina();

        CommandResult result = serina.executeCommand("bye");

        assertEquals(List.of("Bye. Hope to see you again soon!"), result.getResponses());
        assertTrue(result.shouldExit());
    }

    @Test
    public void constructor_corruptSaveFile_reportsLoadErrorAndStartsEmpty() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.writeString(saveFile, "invalid save data");

        Serina serina = new Serina(new Storage(saveFile));

        assertEquals(List.of("Sorry captain, I couldn't load your saved tasks."), serina.getStartupMessages());
        assertEquals(List.of("Here are the tasks in your list:"),
                serina.executeCommand("list").getResponses());
    }

    /**
     * Returns a Serina instance backed by an isolated temporary save file.
     */
    private Serina createSerina() {
        return new Serina(new Storage(temporaryDirectory.resolve("serina.txt")));
    }
}
