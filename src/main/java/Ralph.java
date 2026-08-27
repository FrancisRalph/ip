import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main entrypoint that wires Ui, Storage, Parser and TaskList together.
 */
public class Ralph {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    public Ralph(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    public void run() {
        ui.printBanner();
        Scanner scanner = new Scanner(System.in);
        ui.printPrompt();

        while (true) {
            String line = scanner.nextLine();
            ui.printSeparator();
            try {
                Parser.Parsed p = parser.parseCommand(line);
                boolean shouldExit = handleCommand(p.command, p.args);
                ui.printSeparator();
                if (shouldExit) break;
            } catch (RalphException e) {
                ui.showError(e.getMessage());
                ui.printSeparator();
            }
        }
        scanner.close();
    }

    private boolean handleCommand(String command, String rest) throws RalphException {
        switch (command) {
            case "bye":
                ui.showExit();
                return true;
            case "list":
                ui.printTaskList(tasks.getAll());
                return false;
            case "mark": {
                int idx = Parser.parseTaskIndex(rest, "mark");
                if (idx < 0 || idx >= tasks.size()) throw new RalphException("That task number doesn't exist.");
                tasks.get(idx).markAsDone();
                ui.showMarked(tasks.get(idx));
                persist();
                return false;
            }
            case "unmark": {
                int idx = Parser.parseTaskIndex(rest, "unmark");
                if (idx < 0 || idx >= tasks.size()) throw new RalphException("That task number doesn't exist.");
                tasks.get(idx).markAsNotDone();
                ui.showUnmarked(tasks.get(idx));
                persist();
                return false;
            }
            case "delete": {
                int idx = Parser.parseTaskIndex(rest, "delete");
                if (idx < 0 || idx >= tasks.size()) throw new RalphException("That task number doesn't exist.");
                Task removed = tasks.remove(idx);
                ui.showRemoved(removed, tasks.size());
                persist();
                return false;
            }
            case "todo": {
                String desc = Parser.getRequiredDescription(rest, "todo");
                if (tasks.size() >= 100) throw new RalphException("My list is full — can't add more than 100 tasks.");
                Todo t = new Todo(desc);
                tasks.add(t);
                ui.showAdded(t, tasks.size());
                persist();
                return false;
            }
            case "deadline": {
                String input = Parser.getRequiredDescription(rest, "deadline");
                String[] parts = input.split(" /by ", 2);
                String desc = parts[0].trim();
                String by = parts.length > 1 ? parts[1].trim() : "";
                if (desc.isEmpty()) throw new RalphException("Give me what the deadline is for, please.");
                if (by.isEmpty()) throw new RalphException("Please include '/by' with a date/time in yyyy-MM-dd or yyyy-MM-dd HH:mm format.");
                java.time.LocalDateTime byDt = Parser.parseDateTime(by);
                Deadline d = new Deadline(desc, byDt);
                tasks.add(d);
                ui.showAdded(d, tasks.size());
                persist();
                return false;
            }
            case "event": {
                String input = Parser.getRequiredDescription(rest, "event");
                String[] parts = input.split(" /from ", 2);
                String desc = parts[0].trim();
                if (desc.isEmpty()) throw new RalphException("Give me a short description for the event, please.");
                if (parts.length < 2) throw new RalphException("Events need '/from' and '/to' times (e.g. 'event meeting /from 2019-10-10 /to 2019-10-11').");
                String[] times = parts[1].split(" /to ", 2);
                String from = times[0].trim();
                String to = times.length > 1 ? times[1].trim() : "";
                if (from.isEmpty() || to.isEmpty()) throw new RalphException("An event needs both a start and end time.");
                java.time.LocalDateTime fromDt = Parser.parseDateTime(from);
                java.time.LocalDateTime toDt = Parser.parseDateTime(to);
                Event e = new Event(desc, fromDt, toDt);
                tasks.add(e);
                ui.showAdded(e, tasks.size());
                persist();
                return false;
            }
            default:
                throw new RalphException("I don't recognise that command. Try: list, todo, deadline, event, mark, unmark, delete, bye.");
        }
    }

    private void persist() {
        try {
            storage.save(tasks.getAll());
        } catch (IOException e) {
            // keep UI simple: print warning
            System.out.println(" Error saving tasks: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Ralph("data/duke.txt").run();
    }
}
