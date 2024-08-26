package ru.nern.fconfiglib.v1.api.annotations.validation;

import ru.nern.fconfiglib.v1.validation.AbstractConfigValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValidators {
    Class<? extends AbstractConfigValidator>[] value();
}
