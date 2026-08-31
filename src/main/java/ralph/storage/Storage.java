package ralph.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import ralph.model.Deadline;
import ralph.model.Event;
import ralph.model.Task;
import ralph.model.Todo;

/**
 * Responsible for loading and saving tasks to a file.
 */
public class Storage {
    private final Path dataDir;
    private final Path dataFile;

    /**
     * Constructs a Storage instance configured to read/write the provided file path.
     *
     * @param filePath path to the data file (may include a directory)
     */
    public Storage(String filePath) {
        Path p = Paths.get(filePath);
        this.dataDir = p.getParent() == null ? Paths.get(".") : p.getParent();
        this.dataFile = p;
    }

    /**
     * Loads saved tasks from the configured data file.
     *
     * @return a list of tasks loaded from disk; empty if the file does not exist
     * @throws IOException if reading the file fails unexpectedly
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(dataFile);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s*\\|\\s*", -1);
            String type = parts.length > 0 ? parts[0] : "";
            boolean done = parts.length > 1 && "1".equals(parts[1]);
            try {
                switch (type) {
                case "T": {
                    String desc = parts.length > 2 ? parts[2] : "";
                    Todo t = new Todo(desc);
                    if (done) {
                        t.markAsDone();
                    }
                    tasks.add(t);
                    break;
                }
                case "D": {
                    String desc = parts.length > 2 ? parts[2] : "";
                    String byStr = parts.length > 3 ? parts[3] : "";
                    LocalDateTime byDt = tryParseDateTime(byStr);
                    if (byDt == null) {
                        throw new IllegalArgumentException("Invalid date/time");
                    }
                    Deadline d = new Deadline(desc, byDt);
                    if (done) {
                        d.markAsDone();
                    }
                    tasks.add(d);
                    break;
                }
                case "E": {
                    String desc = parts.length > 2 ? parts[2] : "";
                    String fromStr = parts.length > 3 ? parts[3] : "";
                    String toStr = parts.length > 4 ? parts[4] : "";
                    LocalDateTime fromDt = tryParseDateTime(fromStr);
                    LocalDateTime toDt = tryParseDateTime(toStr);
                    if (fromDt == null || toDt == null) {
                        throw new IllegalArgumentException("Invalid date/time");
                    }
                    Event e = new Event(desc, fromDt, toDt);
                    if (done) {
                        e.markAsDone();
                    }
                    tasks.add(e);
                    break;
                }
                default:
                    break;
                }
            } catch (Exception ex) {
                System.out.println(" Warning: skipping malformed saved task: " + line);
            }
        }
        return tasks;
    }

    /**
     * Saves the provided tasks to the configured data file, creating directories as needed.
     *
     * @param tasks the tasks to persist
     * @throws IOException if writing to disk fails
     */
    public void save(List<Task> tasks) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
            for (Task t : tasks) {
                String line = null;
                String doneFlag = (t.isDone() ? "1" : "0");
                if (t instanceof Todo) {
                    line = "T | " + doneFlag + " | " + t.getDescription();
                } else if (t instanceof Deadline) {
                    Deadline d = (Deadline) t;
                    line = "D | " + doneFlag + " | " + d.getDescription() + " | " + d.getBy();
                } else if (t instanceof Event) {
                    Event e = (Event) t;
                    line = "E | " + doneFlag + " | " + e.getDescription() + " | " + e.getFrom() + " | " + e.getTo();
                }
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Try parsing in several common formats. Returns null on failure.
     */
    private static LocalDateTime tryParseDateTime(String input) {
        if (input == null) {
            return null;
        }
        String s = input.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            if (s.contains("T")) {
                return LocalDateTime.parse(s);
            }
            if (s.contains(" ")) {
                String candidate = s.replace(' ', 'T');
                try {
                    return LocalDateTime.parse(candidate);
                } catch (DateTimeParseException ex) {
                    // Fall through to other formats.
                }
                String[] parts = s.split(" ");
                if (parts.length == 2) {
                    String datePart = parts[0];
                    String timePart = parts[1];
                    if (timePart.matches("\\d{4}")) {
                        String hhmm = timePart.substring(0, 2) + ":" + timePart.substring(2);
                        try {
                            return LocalDateTime.parse(datePart + "T" + hhmm);
                        } catch (DateTimeParseException ex) {
                            // Fall through to other formats.
                        }
                    } else if (timePart.matches("\\d{2}:\\d{2}")) {
                        try {
                            return LocalDateTime.parse(datePart + "T" + timePart);
                        } catch (DateTimeParseException ex) {
                            // Fall through to other formats.
                        }
                    }
                }
            }
            LocalDate d = LocalDate.parse(s);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Exposed helper so other classes (Parser) can reuse the same parsing behaviour.
     */
    /**
     * Public wrapper around the internal parsing routine so other classes can reuse the behaviour.
     *
     * @param input the user-provided date/time string
     * @return parsed LocalDateTime or null when parsing fails
     */
    public static LocalDateTime tryParseDateTimeForReuse(String input) {
        return tryParseDateTime(input);
    }
}
