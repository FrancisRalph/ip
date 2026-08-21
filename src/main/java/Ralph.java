/**
 * Simple interactive CLI that manages tasks and validates user input.
 * Now uses Java Collections (ArrayList) and supports deleting tasks.
 */
public class Ralph {
    private static final String SEP = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Entry point: prints a header, then reads commands until the user exits.
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
        System.out.println("What can I do for you?");

        java.util.List<Task> tasks = new java.util.ArrayList<>();

        while (true) {
            String line = scanner.nextLine();
            boolean shouldExit = false;
            System.out.println(SEP);

            try {
                        shouldExit = processCommand(line, tasks);
            } catch (RalphException e) {
                System.out.println(" Oh no! " + e.getMessage());
            }

            System.out.println(SEP);
            if (shouldExit) {
                break;
            }
        }

        scanner.close();
    }

    /**
     * Handles one command line and throws RalphException for invalid input.
     * @param line raw user input
     * @param tasks the current task list
     * @return true if the chatbot should exit; false otherwise
     * @throws RalphException when the command or arguments are invalid
     */
    private static boolean processCommand(String line, java.util.List<Task> tasks) throws RalphException {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty()) {
                    throw new RalphException("You didn't say anything — try typing a command.");
        }

        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
        case "bye":
                    System.out.println(" Bye. Hope to see you again soon!");
                    return true;
        case "list":
                    printTaskList(tasks);
                    return false;
        case "mark":
                    int markIndex = parseTaskIndex(rest, "mark");
                    if (markIndex < 0 || markIndex >= tasks.size()) {
                        throw new RalphException("That task number doesn't exist.");
                    }
                    tasks.get(markIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(markIndex));
                    return false;
        case "unmark":
                    int unmarkIndex = parseTaskIndex(rest, "unmark");
                    if (unmarkIndex < 0 || unmarkIndex >= tasks.size()) {
                        throw new RalphException("That task number doesn't exist.");
                    }
                    tasks.get(unmarkIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkIndex));
                    return false;
        case "delete":
                    int delIndex = parseTaskIndex(rest, "delete");
                    if (delIndex < 0 || delIndex >= tasks.size()) {
                        throw new RalphException("That task number doesn't exist.");
                    }
                    Task removed = tasks.remove(delIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removed);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    return false;
        case "todo":
                    String todoDescription = getRequiredDescription(rest, "todo");
                    addTask(tasks, new Todo(todoDescription));
                    return false;
        case "deadline":
                    String deadlineInput = getRequiredDescription(rest, "deadline");
                    String[] deadlineParts = deadlineInput.split(" /by ", 2);
                    String deadlineDescription = deadlineParts[0].trim();
                    String deadlineBy = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";
                    if (deadlineDescription.isEmpty()) {
                        throw new RalphException("Give me what the deadline is for, please.");
                    }
                    if (deadlineBy.isEmpty()) {
                        throw new RalphException("Please include '/by' with a date/time (e.g. 'deadline return book /by Sunday').");
            }
                    addTask(tasks, new Deadline(deadlineDescription, deadlineBy));
                    return false;
        case "event":
                    String eventInput = getRequiredDescription(rest, "event");
                    String[] eventParts = eventInput.split(" /from ", 2);
                    String eventDescription = eventParts[0].trim();
                    if (eventDescription.isEmpty()) {
                        throw new RalphException("Give me a short description for the event, please.");
            }
                    if (eventParts.length < 2) {
                        throw new RalphException("Events need '/from' and '/to' times (e.g. 'event meeting /from 2pm /to 3pm').");
                    }
                    String[] eventTimes = eventParts[1].split(" /to ", 2);
                    String from = eventTimes[0].trim();
                    String to = eventTimes.length > 1 ? eventTimes[1].trim() : "";
                    if (from.isEmpty() || to.isEmpty()) {
                        throw new RalphException("An event needs both a start and end time.");
                    }
                    addTask(tasks, new Event(eventDescription, from, to));
                    return false;
        default:
                    throw new RalphException("I don't recognise that command. Try: list, todo, deadline, event, mark, unmark, delete, bye.");
        }
    }

    private static void printTaskList(java.util.List<Task> tasks) {
        if (tasks.isEmpty()) {
                    System.out.println(" No tasks.");
                    return;
        }

        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
                    System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    private static int parseTaskIndex(String value, String command) throws RalphException {
        if (value.isEmpty()) {
                    throw new RalphException("Which task number? Say e.g. 'mark 2'.");
        }
        try {
                    return Integer.parseInt(value) - 1;
        } catch (NumberFormatException e) {
                    throw new RalphException("That's not a number — give me a task index like '2'.");
        }
    }

    private static String getRequiredDescription(String value, String command) throws RalphException {
        if (value == null || value.trim().isEmpty()) {
                    if ("todo".equals(command)) {
                        throw new RalphException("Give me a description for your todo, please.");
                    }
                    if ("deadline".equals(command)) {
                        throw new RalphException("Give me what the deadline is for.");
                    }
                    if ("event".equals(command)) {
                        throw new RalphException("Give me a description for the event, please.");
                    }
        }
        return value.trim();
    }

    private static void addTask(java.util.List<Task> tasks, Task task) throws RalphException {
        if (tasks.size() >= MAX_TASKS) {
                    throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
        }
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}