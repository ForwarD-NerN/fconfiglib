package ru.nern.fconfiglib.v1.api.annotations.restrictions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InRangeInt {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
}
