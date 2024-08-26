package ru.nern.fconfiglib.v1.api.annotations.validation;

public abstract class OptionValidator<T> {
    private boolean saveConfig = false;
    /*
     * Should return true if any changes to fields are made in order for saving to occur
     */
    public abstract void validate(T config) throws ReflectiveOperationException;

    public void saveConfigAfter() {
        this.saveConfig = true;
    }

    public boolean shouldSaveConfig() {
        return this.saveConfig;
    }
}
