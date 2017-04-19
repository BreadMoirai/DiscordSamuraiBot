package samurai.messages.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only one of these messages can exist within the specified scope
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Unique {
    MessageScope scope() default MessageScope.Author;
    boolean shouldPrompt() default true;
    String prompt() default "Another instance of this command is already running. Do you want to remove the previous instance?";
}
