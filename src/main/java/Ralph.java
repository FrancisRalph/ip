/**
 * Simple interactive CLI that manages tasks and validates user input.
 * Now persists tasks to disk under ./data/duke.txt and loads them on startup.
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Ralph {
    private static final String SEP = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    // Relative data location from project root
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("duke.txt");

    // Formatters for printing
    private static final DateTimeFormatter PRINT_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter PRINT_TIME = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Entry point: prints a header, loads saved tasks, then reads commands until the user exits.
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

        // Load any saved tasks (file may not exist the first time)
        try {
            loadTasks(tasks);
        } catch (IOException e) {
            System.out.println(" Warning: could not load saved tasks: " + e.getMessage());
        }

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
                    // persist change
                    saveTasks(tasks);
                    return false;
        case "unmark":
                    int unmarkIndex = parseTaskIndex(rest, "unmark");
                    if (unmarkIndex < 0 || unmarkIndex >= tasks.size()) {
                        throw new RalphException("That task number doesn't exist.");
                    }
                    tasks.get(unmarkIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkIndex));
                    // persist change
                    saveTasks(tasks);
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
                    // persist change
                    saveTasks(tasks);
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
                        throw new RalphException("Please include '/by' with a date/time in yyyy-MM-dd or yyyy-MM-dd HH:mm format.");
            }
                    // parse date/time and create Deadline
                    LocalDateTime byDt = parseDateTime(deadlineBy);
                    addTask(tasks, new Deadline(deadlineDescription, byDt));
                    return false;
        case "event":
                    String eventInput = getRequiredDescription(rest, "event");
                    String[] eventParts = eventInput.split(" /from ", 2);
                    String eventDescription = eventParts[0].trim();
                    if (eventDescription.isEmpty()) {
                        throw new RalphException("Give me a short description for the event, please.");
            }
                    if (eventParts.length < 2) {
                        throw new RalphException("Events need '/from' and '/to' times (e.g. 'event meeting /from 2019-10-10 /to 2019-10-11').");
                    }
                    String[] eventTimes = eventParts[1].split(" /to ", 2);
                    String from = eventTimes[0].trim();
                    String to = eventTimes.length > 1 ? eventTimes[1].trim() : "";
                    if (from.isEmpty() || to.isEmpty()) {
                        throw new RalphException("An event needs both a start and end time.");
                    }
                    LocalDateTime fromDt = parseDateTime(from);
                    LocalDateTime toDt = parseDateTime(to);
                    addTask(tasks, new Event(eventDescription, fromDt, toDt));
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
        // persist change
        saveTasks(tasks);
    }

    /**
     * Parse a user-provided date/time string into LocalDateTime.
     * Accepts ISO date (yyyy-MM-dd), ISO datetime (with 'T' or a space), or yyyy-MM-dd HHmm / HH:mm.
     */
    private static LocalDateTime parseDateTime(String input) throws RalphException {
        LocalDateTime dt = tryParseDateTime(input);
        if (dt == null) {
            throw new RalphException("Could not parse date/time. Use yyyy-MM-dd or yyyy-MM-dd HH:mm (e.g. 2019-10-15 or 2019-10-15 18:00)");
        }
        return dt;
    }

    /**
     * Try parsing in several common formats. Returns null on failure.
     */
    private static LocalDateTime tryParseDateTime(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;
        try {
            // If already ISO-like with 'T'
            if (s.contains("T")) return LocalDateTime.parse(s);
            // If contains a space, try replacing with 'T' and parse
            if (s.contains(" ")) {
                String candidate = s.replace(' ', 'T');
                try { return LocalDateTime.parse(candidate); } catch (DateTimeParseException ex) { }
                // support date + HHmm (e.g. 2019-12-02 1800)
                String[] parts = s.split(" ");
                if (parts.length == 2) {
                    String datePart = parts[0];
                    String timePart = parts[1];
                    if (timePart.matches("\\d{4}")) {
                        String hhmm = timePart.substring(0,2) + ":" + timePart.substring(2);
                        try { return LocalDateTime.parse(datePart + "T" + hhmm); } catch (DateTimeParseException ex) { }
                    } else if (timePart.matches("\\d{2}:\\d{2}")) {
                        try { return LocalDateTime.parse(datePart + "T" + timePart); } catch (DateTimeParseException ex) { }
                    }
                }
            }
            // Try parsing as date only
            LocalDate d = LocalDate.parse(s);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Load saved tasks from ./data/duke.txt if present. The file format is pipe-separated:
     * T | 1 | description
     * D | 0 | description | by
     * E | 0 | description | from | to
     */
    private static void loadTasks(List<Task> tasks) throws IOException {
        if (!Files.exists(DATA_FILE)) {
            return; // nothing to load yet
        }
        List<String> lines = Files.readAllLines(DATA_FILE);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] parts = line.split("\\s*\\|\\s*", -1);
            String type = parts.length > 0 ? parts[0] : "";
            boolean done = parts.length > 1 && "1".equals(parts[1]);
            try {
                switch (type) {
                    case "T": {
                        String desc = parts.length > 2 ? parts[2] : "";
                        Todo t = new Todo(desc);
                        if (done) t.markAsDone();
                        tasks.add(t);
                        break;
                    }
                    case "D": {
                        String desc = parts.length > 2 ? parts[2] : "";
                        String byStr = parts.length > 3 ? parts[3] : "";
                        LocalDateTime byDt = tryParseDateTime(byStr);
                        if (byDt == null) throw new IllegalArgumentException("Invalid date/time");
                        Deadline d = new Deadline(desc, byDt);
                        if (done) d.markAsDone();
                        tasks.add(d);
                        break;
                    }
                    case "E": {
                        String desc = parts.length > 2 ? parts[2] : "";
                        String fromStr = parts.length > 3 ? parts[3] : "";
                        String toStr = parts.length > 4 ? parts[4] : "";
                        LocalDateTime fromDt = tryParseDateTime(fromStr);
                        LocalDateTime toDt = tryParseDateTime(toStr);
                        if (fromDt == null || toDt == null) throw new IllegalArgumentException("Invalid date/time");
                        Event e = new Event(desc, fromDt, toDt);
                        if (done) e.markAsDone();
                        tasks.add(e);
                        break;
                    }
                    default:
                        // ignore unknown lines
                        break;
                }
            } catch (Exception ex) {
                // Skip malformed lines but continue loading the rest
                System.out.println(" Warning: skipping malformed saved task: " + line);
            }
        }
    }

    /**
     * Save the current task list to disk, creating data directory/file if necessary.
     */
    private static void saveTasks(List<Task> tasks) {
        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE)) {
                for (Task t : tasks) {
                    String line = null;
                    String doneFlag = (t.isDone ? "1" : "0");
                    if (t instanceof Todo) {
                        line = "T | " + doneFlag + " | " + t.description;
                    } else if (t instanceof Deadline) {
                        Deadline d = (Deadline) t;
                        line = "D | " + doneFlag + " | " + d.description + " | " + d.by;
                    } else if (t instanceof Event) {
                        Event e = (Event) t;
                        line = "E | " + doneFlag + " | " + e.description + " | " + e.from + " | " + e.to;
                    }
                    if (line != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(" Error saving tasks: " + e.getMessage());
        }
    }
}
