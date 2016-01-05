package pw.haze.event.custom;

import pw.haze.event.Event;
import pw.haze.event.EventManager;
import pw.haze.event.annotations.Filters;
import pw.haze.event.filter.Filter;

import java.lang.reflect.Parameter;
import java.util.*;

public class DefaultEventHandler implements CustomEventHandler {


    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Event[] events, EventManager.CallbackData data) {
        try {
            boolean filterPass = true;
            if (!data.filters.isEmpty()) {
                Filters annotationFilter = data.callbackMethod.getAnnotation(Filters.class);
                for (Filter f : data.filters) {
                    if (annotationFilter.classCheck()) {
                        List<Class<?>> passedClasses = new ArrayList<>(data.filters.size());
                        for (int i = 0; i < data.filters.size(); i++) {
                            passedClasses.set(i, data.filters.get(i).getClass().getGenericSuperclass().getClass());
                        }
                        boolean classPass = true;
                        for (int i = 0; i < events.length; i++) {
                            System.out.println(events[i].getClass() + "    " + passedClasses.get(i));
                            classPass &= events[i].getClass().isAssignableFrom(passedClasses.get(i));
                        }

                        if (classPass) {
                            for (Event e : events) {
                                filterPass &= f.isAcceptable(e);
                            }
                        }
                    } else {
                        for (Event e : events) {
                            filterPass &= f.isAcceptable(e);
                        }
                    }
                }
            }
            if (filterPass) {
                if (events.length == 1) {
                    if (data.callbackMethod.getParameterCount() == 0) {
                        data.callbackMethod.invoke(data.object);
                    } else if (data.callbackMethod.getParameterCount() == 1) {
                        data.callbackMethod.invoke(data.object, events);
                    } else if (data.callbackMethod.getParameterCount() > 1) {
                        Event[] neededEventArray = new Event[data.callbackMethod.getParameterCount()];
                        int place = 1, helper = 0;
                        for (Parameter p : data.callbackMethod.getParameters()) {
                            if (events[0].getClass().isAssignableFrom(p.getType())) {
                                place = helper;
                                break;
                            }
                            helper++;
                        }
                        for (int i = 0; i < data.callbackMethod.getParameterCount(); i++) {
                            if (i == place) {
                                neededEventArray[i] = events[0];
                            } else {
                                neededEventArray[i] = null;
                            }
                        }
                        System.out.println("Executing method with paramList, " + Arrays.asList(neededEventArray));
                        data.callbackMethod.invoke(data.object, neededEventArray);
                    }
                } else {

                    Event[] neededEventListv = new Event[data.callbackMethod.getParameterCount()];
                    Map<Integer, Event> placeToEvent = new HashMap<>();
                    int help = 0;
                    for (Parameter p : data.callbackMethod.getParameters()) {
                        for (Event ev : events) {
                                                   /* if this event is a instance of the parameter, make a place */
                            if (ev.getClass().isAssignableFrom(p.getType())) {
                                placeToEvent.put(help, ev);
                            }
                        }
                        help++;
                    }
                    boolean foundPlace;
                    for (int i = 0; i < data.callbackMethod.getParameterCount(); i++) {
                        foundPlace = false;
                        for (int place : placeToEvent.keySet()) {
                            if (place == i) {
                                neededEventListv[i] = placeToEvent.get(place);
                                foundPlace = true;
                            }
                        }
                        if (!foundPlace)
                            neededEventListv[i] = null;
                    }
                    data.callbackMethod.invoke(data.object, neededEventListv);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
