package ralph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the collection of tasks and basic operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> initial) {
        this.tasks = new ArrayList<>(initial == null ? Collections.emptyList() : initial);
    }

    public int size() {
        return tasks.size();
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }
}
