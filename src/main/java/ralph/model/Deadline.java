package ralph.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A Deadline task: stores a due date/time using java.time.LocalDateTime.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Constructs a Deadline task with the specified due date/time.
     *
     * @param description description of the task
     * @param by due date/time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date/time for this deadline.
     *
     * @return due LocalDateTime
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns a user-friendly string representation of the deadline, with formatted date/time.
     *
     * @return formatted string for display
     */
    @Override
    public String toString() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String when;
        if (by.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            when = dateFmt.format(by.toLocalDate());
        } else {
            when = dateFmt.format(by.toLocalDate()) + " " + timeFmt.format(by.toLocalTime());
        }
        return "[D]" + super.toString() + " (by: " + when + ")";
    }
}
