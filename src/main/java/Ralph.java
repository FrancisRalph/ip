/**
 * Simple interactive CLI that echoes user commands and exits on 'bye'.
 */
public class Ralph {
    /**
     * Entry point: prints a header, then reads lines from stdin.
     * Echoes each command back to the user and stops when the user types "bye".
     */
    public static void main(String[] args) {
        String banner = """
                 ____       _      _        ____    _   _
                |  _ \\     / \\    | |      |  _ \\  | | | |
                | |_) |   / _ \\   | |      | |_) | | |_| |
                |  _ <   / ___ \\  | |___   |  __/  |  _  |
                |_| \\_\\ /_/   \\_\\ |_____|  |_|     |_| |_|
                Hello! I'm Ralph.
                """;
        System.out.println(banner);

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        final String SEP = "____________________________________________________________";

        System.out.println("What can I do for you?");

        while (true) {
            String line = scanner.nextLine();
            System.out.println(SEP);
            if (line.trim().equalsIgnoreCase("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEP);
                break;
            } else {
                System.out.println(" " + line);
                System.out.println(SEP);
            }
        }
        scanner.close();
    }
}
