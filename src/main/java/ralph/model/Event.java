package ralph.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * An Event task: stores start and end as java.time.LocalDateTime.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Constructs an Event with start and end times.
     *
     * @param description short description of the event
     * @param from        start date/time
     * @param to          end date/time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date/time of the event.
     *
     * @return start LocalDateTime
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date/time of the event.
     *
     * @return end LocalDateTime
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns a user-friendly string representation of the event including formatted dates/times.
     *
     * @return formatted string for display
     */
    @Override
    public String toString() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String fromStr = from
            .toLocalTime()
            .equals(LocalTime.MIDNIGHT)
            ? dateFmt.format(from.toLocalDate())
            : dateFmt.format(from.toLocalDate()) + " " + timeFmt.format(from.toLocalTime());
        String toStr = to
            .toLocalTime()
            .equals(LocalTime.MIDNIGHT)
            ? dateFmt.format(to.toLocalDate())
            : dateFmt.format(to.toLocalDate()) + " " + timeFmt.format(to.toLocalTime());
        return "[E]" + super.toString() + " (from: " + fromStr + " to: " + toStr + ")";
    }
}
