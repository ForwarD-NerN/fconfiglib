package ru.nern.fconfiglib.v1.api.annotations.restrictions;

import ru.nern.fconfiglib.v1.api.annotations.validation.OptionValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateOption {
    Class<? extends OptionValidator<?>> value();
}
