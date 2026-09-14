package serina.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import serina.exception.SerinaException;

/** Tests command grammar and normalization. */
public class CommandParserTest {
    @Test
    public void parse_repeatedWhitespace_returnsSemanticArguments() throws SerinaException {
        ParsedCommand command = CommandParser.parse("  event   briefing /from 2026-09-20   /to 2026-09-21 ");

        assertEquals(CommandType.EVENT, command.type());
        assertEquals(List.of("briefing", "2026-09-20", "2026-09-21"), command.arguments());
        assertEquals(List.of("two\twords"), CommandParser.parse("find\ttwo\twords").arguments());
    }

    @Test
    public void parse_extraOrDuplicateArguments_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> CommandParser.parse("help me"));
        assertThrows(SerinaException.class, () -> CommandParser.parse("mark +1"));
        assertThrows(SerinaException.class, () ->
                CommandParser.parse("deadline report /by 2026-09-20 /by 2026-09-21"));
        assertThrows(SerinaException.class, () ->
                CommandParser.parse("event briefing /to 2026-09-21 /from 2026-09-20"));
    }

    @Test
    public void parse_controlCharacter_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> CommandParser.parse("todo unsafe\u0000text"));
    }
}
