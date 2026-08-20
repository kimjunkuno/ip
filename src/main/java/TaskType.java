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
}
