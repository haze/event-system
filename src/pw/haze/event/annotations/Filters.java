package pw.haze.event.annotations;
import pw.haze.event.filter.Filter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Haze on 4/26/2015.
 * An annotation used to filter out events being called.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filters {
    Class<? extends Filter>[] value();
    boolean classCheck() default false;
}
