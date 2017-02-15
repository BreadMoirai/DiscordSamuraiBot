package samurai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TonTL
 * @since 4.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MustOverride {
}
