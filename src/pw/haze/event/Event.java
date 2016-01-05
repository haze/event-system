package pw.haze.event;
/**
 * Created by Haze on 4/15/2015.
 * A class any event should extend. Used for being called by the fire method or cancelling certain events.
 */
public class Event {

    private boolean cancelled;

    /**
     * Used to check if a event is cancelled.
     *
     * @return The cancelled check.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the cancelled value to the new boolean.
     *
     * @param cancelled the new boolean for cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
