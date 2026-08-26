package serina;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Parses and formats dates used by Serina tasks.
 */
public final class DateParser {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern(
            "MMM d yyyy", Locale.ENGLISH);

    private DateParser() {
    }

    /**
     * Parses a date entered by the user.
     *
     * @throws SerinaException if the date is not in the expected format
     */
    public static LocalDate parseInputDate(String dateText) throws SerinaException {
        return parseDate(dateText, SerinaError.INVALID_DATE);
    }

    /**
     * Parses a date loaded from Serina's save file.
     *
     * @throws SerinaException if the date is not in the expected format
     */
    public static LocalDate parseFileDate(String dateText) throws SerinaException {
        return parseDate(dateText, SerinaError.LOAD_FAILED);
    }

    /**
     * Returns a date in the format shown to the user.
     */
    public static String formatDisplayDate(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Returns a date in the format saved to disk.
     */
    public static String formatFileDate(LocalDate date) {
        return date.format(INPUT_FORMATTER);
    }

    private static LocalDate parseDate(String dateText, SerinaError error) throws SerinaException {
        try {
            return LocalDate.parse(dateText, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new SerinaException(error);
        }
    }
}
