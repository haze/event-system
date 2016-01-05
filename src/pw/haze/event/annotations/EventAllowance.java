package pw.haze.event.annotations;

import pw.haze.event.utility.EventAllowanceEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Haze on 5/26/2015.
<<<<<<< HEAD
 * A method to define what allowance the Event Manager should go by.
=======
 * A method to define what value the Event Manager should go by.
>>>>>>> development
 * If selected, ALLOW_ANY allows any event to invoke the method, however
 * if ACCEPT_ONLY_ALL (default) is selected, only events that are all fired at the same time invoke the method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventAllowance {

    EventAllowanceEnum value() default EventAllowanceEnum.ACCEPT_ONLY_ALL;

}
