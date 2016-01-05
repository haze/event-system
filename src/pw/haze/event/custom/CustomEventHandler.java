package pw.haze.event.custom;

import pw.haze.event.Event;
import pw.haze.event.EventManager;

public interface CustomEventHandler {
    void invoke(Event[] events, EventManager.CallbackData data);
}
