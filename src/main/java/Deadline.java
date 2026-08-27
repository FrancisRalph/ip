/**
 * A Deadline task: stores a due date/time using java.time.LocalDateTime.
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

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
