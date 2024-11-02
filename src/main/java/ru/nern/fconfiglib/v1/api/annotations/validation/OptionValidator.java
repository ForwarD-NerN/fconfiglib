package ru.nern.fconfiglib.v1.api.annotations.validation;

/*
 * Deprecated. Use {@link ru.nern.fconfiglib.v1.validation.FieldValidator} instead
 */
@Deprecated
public abstract class OptionValidator<T> {
    private boolean saveConfig = false;

    public abstract void validate(T config) throws ReflectiveOperationException;

    public void saveConfigAfter() {
        this.saveConfig = true;
    }

    public boolean shouldSaveConfig() {
        return this.saveConfig;
    }
}
