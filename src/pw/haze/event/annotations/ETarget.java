package pw.haze.event.annotations;

import pw.haze.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Haze on 4/15/2015.
 * An event placed over a method to define what Events to call.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ETarget {

    Class<? extends Event>[] value();

}
