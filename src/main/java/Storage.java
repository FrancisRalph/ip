import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Responsible for loading and saving tasks to a file.
 */
public class Storage {
    private final Path dataDir;
    private final Path dataFile;

    public Storage(String filePath) {
        Path p = Paths.get(filePath);
        this.dataDir = p.getParent() == null ? Paths.get(".") : p.getParent();
        this.dataFile = p;
    }

    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks; // nothing to load yet
        }
        List<String> lines = Files.readAllLines(dataFile);
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
                System.out.println(" Warning: skipping malformed saved task: " + line);
            }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
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
    }

    /**
     * Try parsing in several common formats. Returns null on failure.
     */
    private static LocalDateTime tryParseDateTime(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;
        try {
            if (s.contains("T")) return LocalDateTime.parse(s);
            if (s.contains(" ")) {
                String candidate = s.replace(' ', 'T');
                try { return LocalDateTime.parse(candidate); } catch (DateTimeParseException ex) { }
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
            LocalDate d = LocalDate.parse(s);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Exposed helper so other classes (Parser) can reuse the same parsing behaviour.
     */
    public static LocalDateTime tryParseDateTimeForReuse(String input) {
        return tryParseDateTime(input);
    }
}
