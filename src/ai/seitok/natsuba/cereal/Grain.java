package ai.seitok.natsuba.cereal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A "Grain" is a piece of a Bowl; fields marked with @Grain will be serialized.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Grain {}
