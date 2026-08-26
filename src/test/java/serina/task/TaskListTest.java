package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list operations that search across stored tasks.
 */
public class TaskListTest {
    private final Todo readBook = new Todo("read book");
    private final Deadline returnBook = new Deadline("return book", LocalDate.of(2026, 8, 30));
    private final Event bookClubMeeting = new Event(
            "book club meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 1));
    private final Todo buyBread = new Todo("buy bread");
    private final TaskList tasks = new TaskList(List.of(readBook, returnBook, bookClubMeeting, buyBread));

    @Test
    public void find_keywordMatchingMultipleTasks_returnsMatchesInOriginalOrder() {
        assertEquals(List.of(readBook, returnBook, bookClubMeeting), tasks.find("book"));
    }

    @Test
    public void find_keywordWithDifferentCase_returnsCaseInsensitiveMatches() {
        assertEquals(List.of(readBook, returnBook, bookClubMeeting), tasks.find("BOOK"));
    }

    @Test
    public void find_partialKeyword_returnsSubstringMatches() {
        assertEquals(List.of(returnBook), tasks.find("turn"));
    }

    @Test
    public void find_keywordWithSurroundingWhitespace_returnsTrimmedKeywordMatches() {
        assertEquals(List.of(buyBread), tasks.find("  bread  "));
    }

    @Test
    public void find_keywordPresentOnlyInTaskMetadata_returnsNoMatches() {
        assertEquals(List.of(), tasks.find("2026"));
    }

    @Test
    public void find_keywordNotPresent_returnsNoMatches() {
        assertEquals(List.of(), tasks.find("exercise"));
    }
}
