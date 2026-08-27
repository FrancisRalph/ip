import java.time.LocalDateTime;

/**
 * Lightweight parser utilities: split raw input into command and argument,
 * and provide validation helpers.
 */
public class Parser {
    public static class Parsed {
        public final String command;
        public final String args;
        public Parsed(String command, String args) {
            this.command = command;
            this.args = args;
        }
    }

    public Parsed parseCommand(String line) throws RalphException {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty()) throw new RalphException("You didn't say anything — try typing a command.");
        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1].trim() : "";
        return new Parsed(command, rest);
    }

    public static int parseTaskIndex(String value, String command) throws RalphException {
        if (value.isEmpty()) {
            throw new RalphException("Which task number? Say e.g. 'mark 2'.");
        }
        try {
            return Integer.parseInt(value) - 1;
        } catch (NumberFormatException e) {
            throw new RalphException("That's not a number — give me a task index like '2'.");
        }
    }

    public static String getRequiredDescription(String value, String command) throws RalphException {
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

    /**
     * Parse a user-provided date/time string into LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String input) throws RalphException {
        LocalDateTime dt = tryParseDateTime(input);
        if (dt == null) {
            throw new RalphException("Could not parse date/time. Use yyyy-MM-dd or yyyy-MM-dd HH:mm (e.g. 2019-10-15 or 2019-10-15 18:00)");
        }
        return dt;
    }

    private static LocalDateTime tryParseDateTime(String input) {
        return Storage.tryParseDateTimeForReuse(input);
    }
}
