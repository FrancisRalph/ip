package ralph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the collection of tasks and basic operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList initialised with the provided tasks.
     *
     * @param initial initial collection of tasks; null results in an empty list
     */
    public TaskList(List<Task> initial) {
        this.tasks = new ArrayList<>(initial == null ? Collections.emptyList() : initial);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the list.
     *
     * @param t the task to add
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index index of the requested task
     * @return the task at index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns an unmodifiable view of all tasks in the list.
     *
     * @return unmodifiable list of tasks
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }
}
