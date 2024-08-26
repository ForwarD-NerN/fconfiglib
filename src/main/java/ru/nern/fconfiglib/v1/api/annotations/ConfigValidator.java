package ru.nern.fconfiglib.v1.api.annotations;

import ru.nern.fconfiglib.v1.validators.AbstractConfigValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValidator {
    Class<? extends AbstractConfigValidator>[] value();
}
