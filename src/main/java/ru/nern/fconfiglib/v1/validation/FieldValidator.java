package ru.nern.fconfiglib.v1.validation;

import ru.nern.fconfiglib.v1.utils.ValueReference;

/*
 * T is the value type.
 * V is the config class,
 * For example, for the List<String> field; the validator would be FieldValidator<MyConfig, List<String>>
 */
public interface FieldValidator<T, V> {
    void validate(ValueReference<T> reference, V config);
}
