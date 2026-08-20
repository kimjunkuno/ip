/**
 * Represents whether a task is done and how that status is displayed.
 */
public enum TaskStatus {
    DONE("X"),
    NOT_DONE(" ");

    private final String icon;

    /**
     * Creates a task status with the given display icon.
     *
     * @param icon short icon shown beside a task
     */
    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon shown when displaying this task status.
     */
    public String getIcon() {
        return icon;
    }
}
