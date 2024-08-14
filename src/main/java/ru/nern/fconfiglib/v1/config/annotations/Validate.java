package ru.nern.fconfiglib.v1.config.annotations;

import ru.nern.fconfiglib.v1.config.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    Class<? extends Validator> validator();
}