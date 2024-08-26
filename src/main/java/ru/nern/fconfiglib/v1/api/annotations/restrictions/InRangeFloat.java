package ru.nern.fconfiglib.v1.api.annotations.restrictions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InRangeFloat {
    float min() default Float.MIN_VALUE;
    float max() default Float.MAX_VALUE;
}