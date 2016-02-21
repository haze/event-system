package pw.haze.event;

import pw.haze.event.annotations.ETarget;
import pw.haze.event.annotations.EventAllowance;
import pw.haze.event.annotations.EventPriority;
import pw.haze.event.annotations.Filters;
import pw.haze.event.custom.CustomEventHandler;
import pw.haze.event.custom.DefaultEventHandler;
import pw.haze.event.filter.Filter;
import pw.haze.event.utility.Priority;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Haze on 4/15/2015.
 * An Event Manager.
 */
public class EventManager {

    private static EventManager _instance;
    public CustomEventHandler eventHandler;
    private Comparator<CallbackData> comparator;
    private EventCallerThread thread;
    private Map<Class<?>[], ArrayList<CallbackData>> data;
    private Map<Class<?>, ArrayList<CallbackData>> registeredClasses;
    private Map<CallbackData, Event[]> eventsToCall;

    /**
     * Creates a new EventManager.
     */
    public EventManager() {
        data = new HashMap<>();
        registeredClasses = new HashMap<>();
        eventHandler = new DefaultEventHandler();
        thread = new EventCallerThread();
        comparator = (o1, o2) -> o1.priority.compareTo(o2.priority);
        eventsToCall = new TreeMap<>(comparator);

    }

    /**
     * Get the EventManager instance.
     *
     * @return The EventManager instance, if not null.
     */
    public static EventManager getInstance() {
        if (_instance == null) {
            _instance = new EventManager();
        }
        return _instance;
    }


    /**
     * A private method that gets all the classTypes from an array into its own array.
     *
     * @param events - the Events to turn in
     * @return An array of classes from the array of events.
     */
    private static Class<?>[] getClassesFromArray(Event[] events) {
        Class<?>[] list = new Class[events.length];
        for (int i = 0; i < events.length; i++) {
            list[i] = events[i].getClass();
        }
        return list;
    }

    /**
     * A method used to unregister the listening of events for a certian class.
     *
     * @param o The class object itself.
     */
    public synchronized void unregisterAll(Object o) {
        Map<Class<?>[], ArrayList<CallbackData>> copyDataSet = new HashMap<>();
        copyDataSet.putAll(data);
        for (Map.Entry<Class<?>[], ArrayList<CallbackData>> e : copyDataSet.entrySet()) {
            for (CallbackData _datas : e.getValue()) {
                if (_datas.object == o) {
                    data.remove(e.getKey());
                }
            }
        }
    }

    /**
     * Registers a class for event listening.
     *
     * @param o                The class object itself.
     * @param targetEventKlass The event to register for the class.
     */
    public synchronized void register(Object o, Class<? extends Event> targetEventKlass) {
        if (o == null)
            return;
        try {
            Class<?> klass = o.getClass();
            for (Method m : klass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ETarget.class)) {
                    Priority priority = Priority.NORMAL;
                    if (m.isAnnotationPresent(EventPriority.class)) {
                        priority = m.getAnnotation(EventPriority.class).value();
                    }
                    Parameter[] params = m.getParameters();
                    if (params.length > 0) {
                        boolean areParamsEvents = true;
                        for (Parameter p : params) {
                            areParamsEvents &= Event.class.isAssignableFrom(p.getType());
                        }
                        if (areParamsEvents) {
                            Class<?>[] eventClasses = new Class<?>[params.length];
                            for (int i = 0; i < params.length; i++) {
                                eventClasses[i] = params[i].getType();
                            }

                            ArrayList<Filter> filters = null;
                            boolean superPass = true;
                            for (Class<?> clazz : eventClasses) {
                                superPass &= targetEventKlass.isAssignableFrom(clazz);
                                if (targetEventKlass.isAssignableFrom(clazz)) {
                                    if (m.isAnnotationPresent(Filters.class)) {
                                        filters = getFiltersFor(m.getAnnotation(Filters.class));
                                    }

                                }
                            }
                            if (superPass) {
                                CallbackData data = new CallbackData(o, m, eventClasses, filters, true, priority);
                                ArrayList<CallbackData> datas = this.data.get(eventClasses);
                                if (datas == null) {
                                    datas = new ArrayList<>();
                                    this.data.put(eventClasses, datas);
                                }
                                datas.add(data);
                                register0(data);
                            }
                        } else {
                            boolean superPass = true;
                            ArrayList<Filter> filters = null;
                            Class<?>[] eventArray = new Class<?>[m.getAnnotation(ETarget.class).value().length];
                            int helper = 0;
                            for (Class<? extends Event> clazz : m.getAnnotation(ETarget.class).value()) {
                                superPass &= targetEventKlass.isAssignableFrom(clazz);
                                eventArray[helper] = clazz;
                                if (m.isAnnotationPresent(Filters.class)) {
                                    filters = getFiltersFor(m.getAnnotation(Filters.class));
                                }
                                helper++;
                            }
                            if (superPass) {
                                CallbackData data = new CallbackData(o, m, eventArray, filters, true, priority);
                                ArrayList<CallbackData> datas = this.data.get(eventArray);
                                if (datas == null) {
                                    datas = new ArrayList<>();
                                    this.data.put(eventArray, datas);
                                }
                                datas.add(data);
                                register0(data);
                            }
                        }
                    } else {
                        boolean superPass = true;
                        ArrayList<Filter> filters = null;
                        Class<?>[] eventArray = new Class<?>[m.getAnnotation(ETarget.class).value().length];
                        int helper = 0;
                        for (Class<? extends Event> clazz : m.getAnnotation(ETarget.class).value()) {
                            superPass &= targetEventKlass.isAssignableFrom(clazz);
                            eventArray[helper] = clazz;
                            if (m.isAnnotationPresent(Filters.class)) {
                                filters = getFiltersFor(m.getAnnotation(Filters.class));
                            }
                            helper++;
                        }
                        if (superPass) {
                            CallbackData data = new CallbackData(o, m, eventArray, filters, true, priority);
                            ArrayList<CallbackData> datas = this.data.get(eventArray);
                            if (datas == null) {
                                datas = new ArrayList<>();
                                this.data.put(eventArray, datas);
                            }
                            datas.add(data);
                                register0(data);
                    }
                }
                    }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Used to register a class for the listening of ALL events.
     *
     * @param o The class object itself.
     */
    public synchronized void registerAll(Object o) {
        register(o, Event.class);
    }

    /**
     * Used as a helper method for register.
     *
     * @param data CallbackData
     */
    private synchronized void register0(CallbackData data) {
        Class<?> klass = data.object.getClass();
        ArrayList<CallbackData> datas = this.registeredClasses.get(klass);
        if (datas == null) {
            datas = new ArrayList<>();
            this.registeredClasses.put(klass, datas);
        }
        datas.add(data);
    }

    /**
     * Gets the filters for the annotation/
     *
     * @param anno A annotation to get fitlers from.
     * @return A set of filters for the annotaiton.
     */
    private ArrayList<Filter> getFiltersFor(Filters anno) {
        //TODO: cache hitting
        Class<? extends Filter>[] filters = anno.value();
        if (filters.length == 0)
            return null;
        ArrayList<Filter> set = new ArrayList<>(filters.length);
        for (Class<? extends Filter> filterKlass : filters) {
            try {
                Filter filter = filterKlass.newInstance();
                set.add(filter);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public synchronized void fire(Event[] events) {
        if (events.length != 0) {
            try {
                for (Map.Entry<Class<?>[], ArrayList<CallbackData>> entr : data.entrySet()) {
                    for (CallbackData _data : entr.getValue()) {
                        if (_data.callbackMethod.isAnnotationPresent(EventAllowance.class)) {
                            switch (_data.callbackMethod.getAnnotation(EventAllowance.class).value()) {
                                case ACCEPT_ONLY_ALL:
                                    if (Arrays.deepEquals(_data.eventKlass, getClassesFromArray(events))) {
                                        eventsToCall.put(_data, events);
                                        thread.poke();
                                    }
                                    break;
                                case ALLOW_ANY:
                                    for (Class<?> clazz : _data.eventKlass) {
                                        for (Event e : events) {
                                            if (e.getClass().isAssignableFrom(clazz)) {
                                                eventsToCall.put(_data, events);
                                                thread.poke();
                                                return;
                                            }
                                        }
                                    }
                            }
                        } else {
                            /* lets check every callback data. if the eventklass equals exactly the event list, fire it */
                            if (Arrays.deepEquals(_data.eventKlass, getClassesFromArray(events))) {
                                eventsToCall.put(_data, events);
                                thread.poke();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    /**
     * Overloaded method for firing one event.
     *
     * @param e The Event to be fired.
     */
    @SuppressWarnings("unchecked")
    public synchronized void fire(Event e) {
        if (e == null)
            return;
        fire(new Event[]{e});
    }

    /**
     * A class used to store information about a method, filters, events, classObj etc..
     */
    public static class CallbackData {
        public final Object object;
        public final Method callbackMethod;
        public final List<Filter> filters;
        public final boolean hasParams;
        public final boolean isArrayOfEvents;
        public Class<?>[] eventKlass;
        public final Priority priority;

        public CallbackData(Object object, Method callbackMethod, Class<?>[] eventKlass, ArrayList<Filter> filters, boolean hasParams, Priority priority) {
            this.eventKlass = new Class<?>[eventKlass.length];
            this.object = object;
            this.callbackMethod = callbackMethod;
            this.eventKlass = eventKlass;
            this.filters = filters == null ? new ArrayList<>() : filters;
            this.hasParams = hasParams;
            this.isArrayOfEvents = true;
            this.priority = priority;
            callbackMethod.setAccessible(true);
        }

        /**
         * Invokes the method with the specified
         *
         * @param events Events to throw
         * @throws Throwable Catch exceptions thrown by method.invoke()
         */
        public void invoke(Event[] events) throws Throwable {
            EventManager.getInstance().eventHandler.invoke(events, this);
        }
    }


    public class EventCallerThread implements Runnable {

        @Override
        public void run() {
            try{
                while (eventsToCall.size() != 0) {
                    for (Map.Entry<CallbackData, Event[]> entry : eventsToCall.entrySet()) {
                        entry.getKey().invoke(entry.getValue());
                        eventsToCall.remove(entry.getKey());
                    }
                }
            }catch (Throwable t){
                eventsToCall.clear();
                assert eventsToCall != null;
                t.printStackTrace();
            }
        }

        /**
         * Use this to notify the thread of new events.
         */
        public void poke(){
            if(eventsToCall.size() != 0){
                run();
            }
        }
    }
}
