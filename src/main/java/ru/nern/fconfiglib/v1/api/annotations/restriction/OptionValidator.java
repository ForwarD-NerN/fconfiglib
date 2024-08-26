package ru.nern.fconfiglib.v1.api.annotations.restriction;

import ru.nern.fconfiglib.v1.api.OptionValidatorCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionValidator {
    Class<? extends OptionValidatorCallback<?>> validator();
}
