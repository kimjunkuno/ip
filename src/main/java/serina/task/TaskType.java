package serina.task;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Represents the supported task types and their display icons.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the given display icon.
     *
     * @param icon short icon shown beside a task
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon shown when displaying this task type.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type matching a value from the save file.
     *
     * @throws SerinaException if the value does not match a supported task type
     */
    public static TaskType parseFileValue(String value) throws SerinaException {
        for (TaskType type : values()) {
            if (type.icon.equals(value.trim())) {
                return type;
            }
        }

        throw new SerinaException(SerinaError.LOAD_FAILED);
    }
}
