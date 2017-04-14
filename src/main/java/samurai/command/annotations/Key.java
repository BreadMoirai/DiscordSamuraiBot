package samurai.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks what classes should be available as a command and defines what key is used to trigger the class.
 * <p> should only be used on <? extends Command> classes</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Key {

    String[] value() default {};
}
