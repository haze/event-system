package pw.haze.event.filter;

/**
 * Created by Haze on 4/26/2015.
 * An interface used to describe a certain filter for an event.
 */
public interface Filter<T> {

    boolean isAcceptable(T e);

}
