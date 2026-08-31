package ralph.command;

import java.io.IOException;

import ralph.exception.RalphException;
import ralph.model.Task;
import ralph.model.TaskList;
import ralph.model.Todo;
import ralph.parser.Parser;
import ralph.storage.Storage;
import ralph.ui.Ui;

/**
 * Represents one user action as a command object.
 */
public interface Command {
    int MAX_TASKS = 100;

    boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException;

    static void persist(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.getAll());
        } catch (IOException e) {
            System.out.println(" Error saving tasks: " + e.getMessage());
        }
    }

    final class ByeCommand implements Command {
        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showExit();
            return true;
        }
    }

    final class ListCommand implements Command {
        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) {
            ui.printTaskList(tasks.getAll());
            return false;
        }
    }

    final class MarkCommand implements Command {
        private final int index;

        public MarkCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            tasks.get(index).markAsDone();
            ui.showMarked(tasks.get(index));
            persist(tasks, storage);
            return false;
        }
    }

    final class UnmarkCommand implements Command {
        private final int index;

        public UnmarkCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            tasks.get(index).markAsNotDone();
            ui.showUnmarked(tasks.get(index));
            persist(tasks, storage);
            return false;
        }
    }

    final class DeleteCommand implements Command {
        private final int index;

        public DeleteCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            Task removed = tasks.remove(index);
            ui.showRemoved(removed, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    final class AddTodoCommand implements Command {
        private final String description;

        public AddTodoCommand(String description) {
            this.description = description;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            Todo todo = new Todo(description);
            tasks.add(todo);
            ui.showAdded(todo, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    final class AddDeadlineCommand implements Command {
        private final String description;
        private final String by;

        public AddDeadlineCommand(String description, String by) {
            this.description = description;
            this.by = by;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            if (description.isEmpty()) {
                throw new RalphException("Give me what the deadline is for, please.");
            }
            if (by.isEmpty()) {
                throw new RalphException("Please include '/by' with a date/time in yyyy-MM-dd or yyyy-MM-dd HH:mm format.");
            }
            ralph.model.Deadline deadline = new ralph.model.Deadline(description, Parser.parseDateTime(by));
            tasks.add(deadline);
            ui.showAdded(deadline, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    final class AddEventCommand implements Command {
        private final String description;
        private final String from;
        private final String to;

        public AddEventCommand(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            if (description.isEmpty()) {
                throw new RalphException("Give me a short description for the event, please.");
            }
            if (from.isEmpty() || to.isEmpty()) {
                throw new RalphException("An event needs both a start and end time.");
            }
            ralph.model.Event event = new ralph.model.Event(description, Parser.parseDateTime(from), Parser.parseDateTime(to));
            tasks.add(event);
            ui.showAdded(event, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }
}
