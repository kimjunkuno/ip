import java.util.Scanner;

/**
 * Runs Serina, a simple chatbot that echoes commands until the user says bye.
 */
public class Serina {
    private static final String LINE = "    ____________________________________________________________";
    private static final String MESSAGE_PREFIX = "     ";

    /**
     * Starts Serina and processes user commands from standard input.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            showGreeting();

            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                if (input.equals("bye")) {
                    showGoodbye();
                    break;
                }

                showMessage(input);
            }
        }
    }

    private static void showGreeting() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Hello! I'm Serina");
        System.out.println(MESSAGE_PREFIX + "What can I do for you?");
        printLine();
    }

    private static void showGoodbye() {
        printLine();
        System.out.println(MESSAGE_PREFIX + "Bye. Hope to see you again soon!");
        printLine();
    }

    private static void showMessage(String message) {
        printLine();
        System.out.println(MESSAGE_PREFIX + message);
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }
}
