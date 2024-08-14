package ru.nern.fconfiglib.v1;

public interface Validator<T> {
    /*
     * Should return true if any changes to fields are made in order for saving to occur
     */
    boolean validate(T config) throws ReflectiveOperationException;
}
