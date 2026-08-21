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
        Task[] tasks = new Task[MAX_TASKS];
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
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty() && cmd.toLowerCase().startsWith("mark ")) {
                String numStr = cmd.substring(5).trim();
                try {
                    int idx = Integer.parseInt(numStr) - 1;
                    if (idx < 0 || idx >= taskCount) {
                        System.out.println(" Invalid task number.");
                    } else {
                        tasks[idx].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks[idx]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Invalid task number.");
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty() && cmd.toLowerCase().startsWith("unmark ")) {
                String numStr = cmd.substring(7).trim();
                try {
                    int idx = Integer.parseInt(numStr) - 1;
                    if (idx < 0 || idx >= taskCount) {
                        System.out.println(" Invalid task number.");
                    } else {
                        tasks[idx].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks[idx]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Invalid task number.");
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty() && cmd.toLowerCase().startsWith("todo ")) {
                String desc = line.substring(5).trim();
                if (desc.isEmpty()) {
                    System.out.println(" The description of a todo cannot be empty.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = new Todo(desc);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Task list is full (max " + MAX_TASKS + ").");
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty() && cmd.toLowerCase().startsWith("deadline ")) {
                String rest = line.substring(9).trim();
                String[] parts = rest.split(" /by ", 2);
                String desc = parts.length > 0 ? parts[0].trim() : "";
                String by = parts.length > 1 ? parts[1].trim() : "";
                if (desc.isEmpty()) {
                    System.out.println(" The description of a deadline cannot be empty.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = new Deadline(desc, by);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Task list is full (max " + MAX_TASKS + ").");
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty() && cmd.toLowerCase().startsWith("event ")) {
                String rest = line.substring(6).trim();
                String[] parts = rest.split(" /from ", 2);
                String desc = parts.length > 0 ? parts[0].trim() : "";
                String from = "";
                String to = "";
                if (parts.length > 1) {
                    String[] parts2 = parts[1].split(" /to ", 2);
                    from = parts2[0].trim();
                    to = parts2.length > 1 ? parts2[1].trim() : "";
                }
                if (desc.isEmpty()) {
                    System.out.println(" The description of an event cannot be empty.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = new Event(desc, from, to);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Task list is full (max " + MAX_TASKS + ").");
                }
                System.out.println(SEP);
            } else if (!cmd.isEmpty()) {
                if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = new Todo(line);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
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
