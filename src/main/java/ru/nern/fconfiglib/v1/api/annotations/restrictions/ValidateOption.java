package ru.nern.fconfiglib.v1.api.annotations.restrictions;
import ru.nern.fconfiglib.v1.api.annotations.validation.OptionValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Deprecated. Use {@link ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField} instead
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ValidateOption {
    Class<? extends OptionValidator<?>> value();
}
