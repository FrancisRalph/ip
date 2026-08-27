/**
 * An Event task: stores start and end as java.time.LocalDateTime.
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String fromStr = from.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? dateFmt.format(from.toLocalDate())
                : dateFmt.format(from.toLocalDate()) + " " + timeFmt.format(from.toLocalTime());
        String toStr = to.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? dateFmt.format(to.toLocalDate())
                : dateFmt.format(to.toLocalDate()) + " " + timeFmt.format(to.toLocalTime());
        return "[E]" + super.toString() + " (from: " + fromStr + " to: " + toStr + ")";
    }
}
