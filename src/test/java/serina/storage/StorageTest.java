package serina.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serina.exception.SerinaException;
import serina.task.Task;
import serina.task.Todo;

/** Tests persistence failures and save-file validation. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_invalidSecondRecord_reportsLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.write(saveFile, List.of("T | 0 | valid", "invalid"));

        SerinaException exception = assertThrows(SerinaException.class, () ->
                new Storage(saveFile).loadTasks());

        assertEquals("Captain Cutter, I couldn't load your saved tasks. Invalid record on line 2.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_duplicateRecords_throwsSerinaException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.write(saveFile, List.of("T | 0 | Inspect engines", "T | 1 | inspect   ENGINES"));

        assertThrows(SerinaException.class, () -> new Storage(saveFile).loadTasks());
    }

    @Test
    public void saveAndLoad_specialCharacters_roundTrips() throws SerinaException {
        Path saveFile = temporaryDirectory.resolve("nested").resolve("serina.txt");
        Storage storage = new Storage(saveFile);
        List<Task> tasks = List.of(new Todo("review | path \\ notes"));

        storage.saveTasks(tasks);

        assertEquals(tasks.get(0).toString(), storage.loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_directoryPath_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> new Storage(temporaryDirectory).loadTasks());
    }
}
