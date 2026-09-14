package serina.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import serina.exception.SerinaException;

/** Tests strict calendar-date validation. */
public class DateParserTest {
    @Test
    public void parseInputDate_validLeapDay_returnsDate() throws SerinaException {
        assertEquals(LocalDate.of(2028, 2, 29), DateParser.parseInputDate("2028-02-29"));
    }

    @Test
    public void parseInputDate_invalidDates_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-02-29"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-02-30"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("26-09-14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-9-14"));
    }
}
