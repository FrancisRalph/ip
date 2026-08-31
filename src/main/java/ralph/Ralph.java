package ralph;

import java.io.IOException;
import java.util.Scanner;

import ralph.command.Command;
import ralph.command.CommandHandler;
import ralph.exception.RalphException;
import ralph.model.TaskList;
import ralph.storage.Storage;
import ralph.ui.Ui;

/**
 * Main entrypoint that wires Ui, Storage, Parser and TaskList together.
 */
public class Ralph {
    private final Ui ui;
    private final Storage storage;
    private final CommandHandler commandHandler;
    private final TaskList tasks;

    public Ralph(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.commandHandler = new CommandHandler(new ralph.parser.Parser());
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

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            ui.printSeparator();
            try {
                Command command = commandHandler.parse(line);
                boolean shouldExit = command.execute(tasks, ui, storage);
                ui.printSeparator();
                if (shouldExit) {
                    break;
                }
            } catch (RalphException e) {
                ui.showError(e.getMessage());
                ui.printSeparator();
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        new Ralph("data/duke.txt").run();
    }
}
