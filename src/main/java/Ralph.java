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

        final int MAX_TASKS = 100;
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

        while (true) {
            String line = scanner.nextLine();
            System.out.println(SEP);
            String cmd = line.trim();
            if (cmd.equalsIgnoreCase("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEP);
                break;
            } else if (cmd.equalsIgnoreCase("list")) {
                if (taskCount == 0) {
                    System.out.println(" No tasks.");
                } else {
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks[i]);
                    }
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty()) {
                if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = line;
                    System.out.println(" added: " + line);
                } else {
                    System.out.println(" Task list is full (max " + MAX_TASKS + ").");
                }
                System.out.println(SEP);
            } else {
                System.out.println(SEP);
            }
        }
        scanner.close();
    }
}
