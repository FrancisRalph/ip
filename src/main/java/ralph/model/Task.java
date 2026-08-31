package ralph.model;

/**
 * Represents a simple task with a description and done status.
 */
public class Task {
    /** The task description. */
    protected String description;
    /** Whether the task is done. */
    protected boolean isDone;

    /**
     * Create a new Task with the given description, initially not done.
     * @param description task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when printing the task.
     * @return "X" if done, otherwise a space character
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    /** Marks the task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks the task as not done. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
