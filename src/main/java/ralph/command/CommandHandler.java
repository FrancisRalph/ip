package ralph.command;

import ralph.exception.RalphException;
import ralph.parser.Parser;

/**
 * Parses raw user input and returns the corresponding command object.
 */
public class CommandHandler {
    private final Parser parser;

    public CommandHandler(Parser parser) {
        this.parser = parser;
    }

    /**
     * Parses a raw input line into the corresponding Command object.
     *
     * @param line raw user input
     * @return Command instance representing the requested action
     * @throws RalphException on invalid or malformed input
     */
    public Command parse(String line) throws RalphException {
        Parser.Parsed parsed = parser.parseCommand(line);
        String command = parsed.command;
        String rest = parsed.args;

        switch (command) {
        case "bye":
            return new Command.ByeCommand();
        case "list":
            return new Command.ListCommand();
        case "mark":
            return new Command.MarkCommand(Parser.parseTaskIndex(rest, "mark"));
        case "unmark":
            return new Command.UnmarkCommand(Parser.parseTaskIndex(rest, "unmark"));
        case "delete":
            return new Command.DeleteCommand(Parser.parseTaskIndex(rest, "delete"));
        case "todo": {
            String desc = Parser.getRequiredDescription(rest, "todo");
            return new Command.AddTodoCommand(desc);
        }
        case "deadline": {
            String input = Parser.getRequiredDescription(rest, "deadline");
            String[] parts = input.split(" /by ", 2);
            String desc = parts[0].trim();
            String by = parts.length > 1 ? parts[1].trim() : "";
            return new Command.AddDeadlineCommand(desc, by);
        }
        case "event": {
            String input = Parser.getRequiredDescription(rest, "event");
            String[] parts = input.split(" /from ", 2);
            String desc = parts[0].trim();
            if (parts.length < 2) {
                throw new RalphException("Events need '/from' and '/to' times (e.g. 'event meeting /from 2019-10-10 /to 2019-10-11').");
            }
            String[] times = parts[1].split(" /to ", 2);
            String from = times[0].trim();
            String to = times.length > 1 ? times[1].trim() : "";
            return new Command.AddEventCommand(desc, from, to);
        }
        default:
            throw new RalphException("I don't recognise that command. Try: list, todo, deadline, event, mark, unmark, delete, bye.");
        }
    }
}
