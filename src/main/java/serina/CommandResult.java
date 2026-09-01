package serina;

import java.util.List;

/**
 * Contains the messages and application state produced by one Serina command.
 */
public class CommandResult {
    private final List<String> responses;
    private final boolean shouldExit;

    /**
     * Creates a command result with the given responses and exit behavior.
     *
     * @param responses Messages to show in order.
     * @param shouldExit Whether the application should stop accepting commands.
     */
    public CommandResult(List<String> responses, boolean shouldExit) {
        this.responses = List.copyOf(responses);
        this.shouldExit = shouldExit;
    }

    /**
     * Creates a command result from individual responses and the given exit behavior.
     *
     * @param shouldExit Whether the application should stop accepting commands.
     * @param responses Messages to show in order.
     */
    public CommandResult(boolean shouldExit, String... responses) {
        this(List.of(responses), shouldExit);
    }

    /**
     * Returns the messages produced by the command.
     *
     * @return Responses in display order.
     */
    public List<String> getResponses() {
        return responses;
    }

    /**
     * Returns whether the application should stop accepting commands.
     *
     * @return {@code true} if the command ends the conversation.
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
