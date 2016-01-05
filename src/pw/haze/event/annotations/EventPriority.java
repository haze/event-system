package pw.haze.event.annotations;
import pw.haze.event.utility.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Haze on 6/11/2015.
 * Project EventManager.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventPriority {
    Priority value() default Priority.NORMAL;
}