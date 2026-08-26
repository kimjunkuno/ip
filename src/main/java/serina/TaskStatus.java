package serina;

/**
 * Represents whether a task is done and how that status is displayed.
 */
public enum TaskStatus {
    DONE("X", "1"),
    NOT_DONE(" ", "0");

    private final String icon;
    private final String fileValue;

    /**
     * Creates a task status with the given display icon.
     *
     * @param icon short icon shown beside a task
     * @param fileValue value used when saving the status to disk
     */
    TaskStatus(String icon, String fileValue) {
        this.icon = icon;
        this.fileValue = fileValue;
    }

    /**
     * Returns the icon shown when displaying this task status.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the compact value written to the save file.
     */
    public String getFileValue() {
        return fileValue;
    }

    /**
     * Returns the status matching a value from the save file.
     *
     * @throws SerinaException if the value does not match a supported task status
     */
    public static TaskStatus parseFileValue(String value) throws SerinaException {
        for (TaskStatus status : values()) {
            if (status.fileValue.equals(value.trim())) {
                return status;
            }
        }

        throw new SerinaException(SerinaError.LOAD_FAILED);
    }
}
